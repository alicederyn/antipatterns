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

import static edu.umd.cs.findbugs.ba.Hierarchy.isSubtype;

import org.apache.bcel.classfile.JavaClass;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.BCELUtil;

public class ExtendsConcreteTypeDetector implements Detector {

    private static final String OBJECT = Object.class.getName();
    private static final String THROWABLE = Throwable.class.getName();

    private final BugReporter bugReporter;

    public ExtendsConcreteTypeDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass obj = classContext.getJavaClass();
        if (BCELUtil.isSynthetic(obj)) {
            return;
        }
        try {
            // Extending Object, an abstract type or Throwable are all permitted.
            if (OBJECT.equals(obj.getSuperclassName())
                    || obj.getSuperClass().isAbstract()
                    || obj.getSuperClass().isEnum()
                    || isThrowable(obj)) {
                return;
            }
        } catch (ClassNotFoundException e) {
            // Ignore
            return;
        }
        bugReporter.reportBug(new BugInstance(this, "PT_EXTENDS_CONCRETE_TYPE", HIGH_PRIORITY)
                .addClass(obj.getClassName())
                .addClass(obj.getSuperclassName()));
    }

    private static boolean isThrowable(JavaClass cls) throws ClassNotFoundException {
        return isSubtype(cls.getClassName(), THROWABLE);
    }

    @Override
    public void report() {}
}
