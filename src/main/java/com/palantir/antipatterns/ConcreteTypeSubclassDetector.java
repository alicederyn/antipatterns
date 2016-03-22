package com.palantir.antipatterns;

import org.apache.bcel.classfile.JavaClass;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.BCELUtil;
import edu.umd.cs.findbugs.bcel.PreorderDetector;

public class ConcreteTypeSubclassDetector extends PreorderDetector {

    public static final String EXTENDED_CONCRETE_TYPE_BUG = "PT_EXTENDED_CONCRETE_TYPE";

    private final BugReporter bugReporter;
    private final Set<String> visited = new LinkedHashSet<>();

    public ConcreteTypeSubclassDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visitJavaClass(JavaClass obj) {
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
        if ("java.lang.Object".equals(obj.getSuperclassName())) {
            return;
        }
        try {
            if (!obj.getSuperClass().isAbstract()) {
                return;
            }
        } catch (ClassNotFoundException e) {
            // Ignore
            return;
        }
        bugReporter.reportBug(new BugInstance(this, EXTENDED_CONCRETE_TYPE_BUG, HIGH_PRIORITY)
                .addClass(name)
                .addClass(obj.getSuperclassName()));
    }
}
