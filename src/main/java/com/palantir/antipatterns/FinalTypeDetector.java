package com.palantir.antipatterns;

import org.apache.bcel.classfile.JavaClass;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.bcel.BCELUtil;

public class FinalTypeDetector implements Detector {

    private final BugReporter bugReporter;

    public FinalTypeDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass obj = classContext.getJavaClass();
        if (BCELUtil.isSynthetic(obj)) {
            return;
        }
        if (obj.isFinal()) {
            bugReporter.reportBug(new BugInstance(this, "PT_FINAL_TYPE", LOW_PRIORITY)
                    .addClass(obj.getClassName()));
        }
    }

    @Override
    public void report() {}
}
