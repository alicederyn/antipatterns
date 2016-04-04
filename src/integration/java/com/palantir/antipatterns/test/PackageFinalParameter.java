package com.palantir.antipatterns.test;

public class PackageFinalParameter {

    void doSomething(FinalClass withValue) {
        System.out.println(withValue);
    }
}
