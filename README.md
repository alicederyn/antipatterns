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
