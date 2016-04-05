# antipatterns

A FindBugs plugin for detecting misuse of inheritance and the final keyword in Java, inspired by [codingantihero]. Eliminate fragile subclassing and un-mockable APIs!

The plugin name, 'antipatterns', is aspirational: we hope to add more modern Java antipatterns in future, and would love to consider any suggestions in our [issue tracker].

[codingantihero]: https://codingantihero.wordpress.com/2016/02/08/antipattern-final-classes/
[issue tracker]: https://github.com/palantir/antipatterns/issues

[![CircleCI Build Status](https://circleci.com/gh/palantir/antipatterns.svg?style=svg&circle-token=198b658b598ace680a676ece564df2a0f0690d33)](https://circleci.com/gh/palantir/antipatterns)
[![Download](https://api.bintray.com/packages/palantir/releases/antipatterns/images/download.svg) ](https://bintray.com/palantir/releases/antipatterns/_latestVersion)

## Checking for antipatterns

To enable the antipatterns plugin in Gradle, just use the `findbugsPlugins` extension point:

```gradle
plugin 'findbugs'
dependencies {
    findbugsPlugins 'com.palantir.common:antipatterns:+'
}
```

Now running `gradle check` will include the antipatterns checks.

If you're using [the FindBugs Maven plugin](http://gleclaire.github.io/findbugs-maven-plugin/usage.html#Using_Detectors_from_a_Repository):

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

Alternatively, if you're running FindBugs standalone, download [the latest antipatterns version](https://bintray.com/palantir/releases/antipatterns/_latestVersion), and place it in the `plugin` directory in your FindBugs installation.

### ExtendsConcreteTypeDetector

**Antipattern detected:** Extending a non-abstract type.

**Motivation:** Designing for extension is expensive, and should only be done when necessary. Extending types that are not intended to be extended is dangerous, as the implementation may change in ways that do not affect composition, but break subclasses; for instance, changing one method to call another (to remove duplication) or to stop calling it (to improve performance). Preventing extension of a type that's not designed for it is expensive in Java: declaring the class final breaks dynamic proxies like [Mockito]; hiding the constructor behind a public factory method requires more code, and is often less idiomatic. (It's also easy to miss when a constructor has been accidentally left public.) Instead, types that are designed for extension should unambiguously indicate it by being declared abstract; types that are not abstract should never be extended.

**Suggested alternatives:** Instances of this antipattern should be refactored in one of the following ways (assuming type Bar extends type Foo):

 1. **[Abstract class]:** Extract an abstract superclass, AbstractFoo, and have both Foo and Bar extend it.
 2. **[Delegation pattern]:** Add a field of type Foo to Bar, typically called `delegate`, and explicitly call Foo's methods from Bar's methods.
 3. **[Strategy pattern]:** Add a strategy object to Foo that allows you to configure it to act like a Bar.

[Mockito]: http://mockito.org/
[Abstract class]: https://en.wikipedia.org/wiki/Abstract_type
[Delegation pattern]: https://en.wikipedia.org/wiki/Delegation_pattern
[Strategy pattern]: https://en.wikipedia.org/wiki/Strategy_pattern
