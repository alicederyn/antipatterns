package com.palantir.antipatterns;

import org.apache.bcel.classfile.JavaClass;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.BCELUtil;

public class ExtendsConcreteTypeDetector implements Detector {

    private final BugReporter bugReporter;
    private final Set<String> visited = new LinkedHashSet<>();

    public ExtendsConcreteTypeDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass obj = classContext.getJavaClass();
        if (BCELUtil.isSynthetic(obj)) {
            return;
        }
        if (obj.isAbstract()) {
            return;
        }
        String name = obj.getClassName();
        if (!visited.add(name)) {
            return;
        }
        if (obj.isFinal()) {
            bugReporter.reportBug(new BugInstance(this, "PT_FINAL_TYPE", NORMAL_PRIORITY)
                    .addClass(name));
        }
        if ("java.lang.Object".equals(obj.getSuperclassName())) {
            // TODO Test this
            return;
        }
        // TODO Exceptions
        try {
            if (obj.getSuperClass().isAbstract()) {
                // TODO Test this
                return;
            }
        } catch (ClassNotFoundException e) {
            // Ignore
            return;
        }
        bugReporter.reportBug(new BugInstance(this, "PT_EXTENDS_CONCRETE_TYPE", HIGH_PRIORITY)
                .addClass(name)
                .addClass(obj.getSuperclassName()));
    }

    @Override
    public void report() {}
}
