Dropwizard Atomix
=================
[![Build Status](https://travis-ci.org/smoketurner/dropwizard-atomix.svg?branch=master)](https://travis-ci.org/smoketurner/dropwizard-atomix)
[![Coverage Status](https://coveralls.io/repos/smoketurner/dropwizard-atomix/badge.svg?branch=master)](https://coveralls.io/r/smoketurner/dropwizard-atomix?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.smoketurner.dropwizard/dropwizard-atomix.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.smoketurner.dropwizard/dropwizard-atomix/)
[![GitHub license](https://img.shields.io/github/license/smoketurner/dropwizard-atomix.svg?style=flat-square)](https://github.com/smoketurner/dropwizard-atomix/tree/master)

A bundle for integrating [Atomix](http://atomix.io/atomix/) in Dropwizard applications.

Usage
-----

Within your `Configuration` class, add the following:

```java
@Valid
@NotNull
private final AtomixFactory atomix = new AtomixFactory();

@JsonProperty
public AtomixFactory getAtomixFactory() {
    return atomix;
}
```

Then with your `Application` class, you can access an `Atomix` by doing the following:

```java
@Override
public void initialize(Bootstrap<MyConfiguration> bootstrap) {
    bootstrap.addBundle(new AtomixBundle<MyConfiguration>() {
        @Override
        public AtomixFactory getAtomixFactory(MyConfiguration configuration) {
            return configuration.getAtomixFactory();
        }
    });
}

@Override
public void run(MyConfiguration configuration, Environment environment) throws Exception {
    Atomix atomix = configuration.getAtomixFactory().build();
}
```

Maven Artifacts
---------------

This project is available on Maven Central. To add it to your project simply add the following dependencies to your `pom.xml`:

```xml
<dependency>
    <groupId>com.smoketurner.dropwizard</groupId>
    <artifactId>dropwizard-atomix</artifactId>
    <version>1.2.0-1</version>
</dependency>
```

Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/smoketurner/dropwizard-atomix/issues).


License
-------

Copyright (c) 2017 Smoke Turner, LLC

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
