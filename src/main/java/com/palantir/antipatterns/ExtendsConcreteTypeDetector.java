package com.palantir.antipatterns;

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
        for (JavaClass superClass : cls.getSuperClasses()) {
            if (THROWABLE.equals(superClass.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void report() {}
}
