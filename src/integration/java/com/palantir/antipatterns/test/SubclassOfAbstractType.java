package com.palantir.antipatterns.test;

import java.util.AbstractList;

public class SubclassOfAbstractType extends AbstractList<String> {

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String get(int index) {
        throw new UnsupportedOperationException();
    }
}
