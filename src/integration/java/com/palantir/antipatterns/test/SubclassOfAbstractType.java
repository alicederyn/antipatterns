package com.palantir.antipatterns.test;

import java.util.AbstractMap;
import java.util.Set;

public class SubclassOfAbstractType extends AbstractMap<String, String> {

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
