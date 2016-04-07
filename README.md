# antipatterns

A FindBugs plugin for detecting misuse of inheritance and the final keyword in Java, inspired by [codingantihero]. Eliminate fragile subclassing and un-mockable APIs!

The plugin name, 'antipatterns', is aspirational: we hope to add more modern Java antipatterns in future, and would love to consider any suggestions in our [issue tracker].

[codingantihero]: https://codingantihero.wordpress.com/2016/02/08/antipattern-final-classes/
[issue tracker]: https://github.com/palantir/antipatterns/issues

[![CircleCI Build Status](https://circleci.com/gh/palantir/antipatterns.svg?style=svg&circle-token=198b658b598ace680a676ece564df2a0f0690d33)](https://circleci.com/gh/palantir/antipatterns)
[![Download](https://api.bintray.com/packages/palantir/releases/antipatterns/images/download.svg) ](https://bintray.com/palantir/releases/antipatterns/_latestVersion)

## Checking for antipatterns

**Gradle:** To enable the antipatterns plugin in Gradle, just use the `findbugsPlugins` extension point:

```gradle
plugin 'findbugs'
dependencies {
    findbugsPlugins 'com.palantir.common:antipatterns:+'
}
```

Now running `gradle check` will include the antipatterns checks.

**Maven:** If you're using [the FindBugs Maven plugin](http://gleclaire.github.io/findbugs-maven-plugin/usage.html#Using_Detectors_from_a_Repository):

```maven
<configuration>
  <plugins>
    <plugin>
      <groupId>com.palantir.common</groupId>
      <artifactId>antipatterns</artifactId>
      <version>[1.0.0,)</version>
    </plugin>
  </plugins>
</configuration>
```

**Standalone:** Alternatively, if you're running FindBugs standalone, download [the latest antipatterns version](https://bintray.com/palantir/releases/antipatterns/_latestVersion), and place it in the `plugin` directory in your FindBugs installation.

### ExtendsConcreteTypeDetector

**Antipattern detected:** Extending a non-abstract type.

**Motivation:** Fragile. Designing for extension is expensive, and should only be done when necessary. Extending types that are not intended to be extended is dangerous, as the implementation may change in ways that do not affect composition, but break subclasses; for instance, changing one method to call another (to remove duplication) or to stop calling it (to improve performance). Preventing extension of a type that's not designed for it is expensive in Java: declaring the class final breaks dynamic proxies like [Mockito]; hiding the constructor behind a public factory method requires more code, and is often less idiomatic. (It's also easy to miss when a constructor has been accidentally left public.) Instead, types that are designed for extension should unambiguously indicate it by being declared abstract; types that are not abstract should never be extended.

**Suggested alternatives:** Instances of this antipattern should be refactored in one of the following ways (assuming type Bar extends type Foo):

 1. **[Abstract class]:** Extract an abstract superclass, AbstractFoo, and have both Foo and Bar extend it.
 2. **[Delegation pattern]:** Add a field of type Foo to Bar, typically called `delegate`, and explicitly call Foo's methods from Bar's methods.
 3. **[Strategy pattern]:** Add a strategy object to Foo that allows you to configure it to act like a Bar.

[Mockito]: http://mockito.org/
[Abstract class]: https://en.wikipedia.org/wiki/Abstract_type
[Delegation pattern]: https://en.wikipedia.org/wiki/Delegation_pattern
[Strategy pattern]: https://en.wikipedia.org/wiki/Strategy_pattern

**Related detectors:** [DesignForExtension] places additional constraints on classes intended to be extended.

[DesignForExtension]: http://checkstyle.sourceforge.net/config_design.html#DesignForExtension

### FinalSignatureDetector

**Antipattern detected:** Accepting or returning a final type in a public method.

**Motivation:** Untestable. Unit testing involves isolating the component being tested from the rest of the system by substituting the real implementation of its dependencies for [test doubles]. Final types subvert this by being impossible to substitute: the JVM itself prevents behaviour substitution. This is rooted in the origins of the language as a way to run untrusted user code in a sandbox; without final types, the sandbox could never trust the values returned by the user code, as they could trigger arbitrary behaviour that would be very hard to reason about. (For instance, overriding equals could give the type access to other classloaders.) This is clearly overkill for most Java code, and disables really useful testing tools, such as [Mockito's ReturnsSmartNulls]. If you provide a library, exposing final types forces your users to use the [Adapter pattern] to hide your library behind a testable façade.

**Suggested alternatives:** Instances of this antipattern should be refactored in one of the following ways (assuming type Bar has a method baz that consumes or returns type Foo):

1. **Interface:** Rename Foo as FooImpl, and extract an interface called Foo. Consume/return the Foo interface rather than the concrete FooImpl type in Bar.baz.
2. **Factory method:** Remove the final keyword from Foo, and instead declare the constructor private to prevent subclassing. Make a static factory method on Foo to allow instances to be constructed.
3. **Pin your API:** Remove the final keyword from Foo, and guarantee not to make subclass-breaking changes in future.
4. **Trust your users:** Remove the final keyword from Foo, and trust your users not to make any fragile subclasses. Use the antipatterns plugin to enforce this contract within your own company. May be a bit optimistic for a big open-source project like Guava, but otherwise the cleanest option.

[test doubles]: https://nirajrules.wordpress.com/2011/08/27/dummy-vs-stub-vs-spy-vs-fake-vs-mock/
[Mockito's ReturnsSmartNulls]: http://site.mockito.org/mockito/docs/current/org/mockito/internal/stubbing/defaultanswers/ReturnsSmartNulls.html
[Adapter pattern]: https://en.wikipedia.org/wiki/Adapter_pattern

**Related detectors:** [DesignForExtension] requires public methods on non-final classes be abstract, final or empty; if you want to follow options 3 or 4, you will need to disable it. [FinalClass] requires that classes with private constructors be declared as final; if you want to follow option 2, you will need to disable it. Option 1 is compatible with both detectors.

[FinalClass]: http://checkstyle.sourceforge.net/config_design.html#FinalClass

## Building from source

To build antipatterns from source, run `./gradlew shadowJar`. To run the tests, run `./gradlew integrationTest`. Publishing a new artifact is done automatically in CircleCI when a new tag is committed to GitHub.

