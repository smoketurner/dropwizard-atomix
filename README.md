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
    <artifactId>atomix-core</artifactId>
    <version>1.2.2-1</version>
</dependency>
```

Example Application
-------------------

To try the example application, checkout the code from Github then build the example hello world application.

```
git clone https://github.com/smoketurner/dropwizard-atomix.git
cd dropwizard-atomix
./mvnw compile package
cd atomix-example
```

You then need to open 3 separate terminal windows to launch 3 instances of the application:

```
java -jar target/atomix-example-1.2.2-2-SNAPSHOT.jar server config1.yml
java -jar target/atomix-example-1.2.2-2-SNAPSHOT.jar server config2.yml
java -jar target/atomix-example-1.2.2-2-SNAPSHOT.jar server config3.yml
```

Once a quorum has been reached, Jetty will start up as normal on a random port which you'll be able to see in the logs.

```
INFO  [2018-01-07 15:29:43,489] org.eclipse.jetty.server.handler.ContextHandler: Started i.d.j.MutableServletContextHandler@2ad99cf3{/admin,null,AVAILABLE}
INFO  [2018-01-07 15:29:43,494] org.eclipse.jetty.server.AbstractConnector: Started hello-world@554f0dfb{HTTP/1.1,[http/1.1]}{0.0.0.0:55419}
INFO  [2018-01-07 15:29:43,494] org.eclipse.jetty.server.Server: Started @13775ms
```

You can then visit `http://localhost:55419/hello-world` to see the Dropwizard [Getting Started](http://www.dropwizard.io/1.2.2/docs/getting-started.html) example, but using a distributed counter across the cluster.

Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/smoketurner/dropwizard-atomix/issues).


License
-------

Copyright (c) 2018 Smoke Turner, LLC

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
