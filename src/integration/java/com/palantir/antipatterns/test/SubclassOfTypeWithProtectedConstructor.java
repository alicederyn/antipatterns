package com.palantir.antipatterns.test;

public class SubclassOfTypeWithProtectedConstructor extends TypeWithProtectedConstructor {}

class TypeWithProtectedConstructor {
  protected TypeWithProtectedConstructor() {}
}