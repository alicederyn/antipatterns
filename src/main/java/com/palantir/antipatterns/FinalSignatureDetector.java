/*
 * Copyright 2016 Palantir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.antipatterns;

import static org.apache.bcel.Constants.CONSTRUCTOR_NAME;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.BCELUtil;

public class FinalSignatureDetector implements Detector {

    private final BugReporter bugReporter;

    public FinalSignatureDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass obj = classContext.getJavaClass();
        if (BCELUtil.isSynthetic(obj)) {
            return;
        }
        for (Method method : obj.getMethods()) {
            if (method.isPublic() || method.isProtected()) {
                if (isIllegalFinalType(method.getReturnType(), classContext)) {
                    bugReporter.reportBug(new BugInstance(this, "PT_FINAL_TYPE_RETURN", NORMAL_PRIORITY)
                            .addClassAndMethod(obj, method)
                            .addType(method.getReturnType()));
                }
                if (obj.isFinal() && isConstructor(method)) {
                    bugReporter.reportBug(new BugInstance(this, "PT_FINAL_TYPE_CONSTRUCTOR", NORMAL_PRIORITY)
                            .addClassAndMethod(obj, method));
                }
                int param = 0;
                for (Type type : method.getArgumentTypes()) {
                    if (isIllegalFinalType(type, classContext)) {
                        bugReporter.reportBug(
                                new BugInstance(this, "PT_FINAL_TYPE_PARAM", NORMAL_PRIORITY)
                                        .addInt(param)
                                        .addClassAndMethod(obj, method)
                                        .addType(type));
                    }
                    param++;
                }
            }
        }
    }

    private static boolean isConstructor(Method method) {
        return method.getName().equals(CONSTRUCTOR_NAME);
    }

    private static boolean isIllegalFinalType(Type type, ClassContext classContext) {
        if (type instanceof ObjectType) {
            try {
                String className = ((ObjectType) type).getClassName();
                if (className.startsWith("java.")) {
                    // Types in java.lang are final for security reasons.
                    return false;
                }
                JavaClass cls = classContext.getAnalysisContext().lookupClass(className);
                return cls.isFinal() && !cls.isEnum();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public void report() {}
}
