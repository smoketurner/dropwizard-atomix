/**
 * Copyright 2017 Smoke Turner, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.helloworld.resources.HelloWorldResource;
import com.smoketurner.dropwizard.atomix.AtomixBundle;
import com.smoketurner.dropwizard.atomix.AtomixFactory;
import io.atomix.Atomix;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldApplication
        extends Application<HelloWorldConfiguration> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(HelloWorldApplication.class);

    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(
                new AtomixBundle<HelloWorldConfiguration>(getName()) {
                    @Override
                    public AtomixFactory getAtomixFactory(
                            HelloWorldConfiguration configuration) {
                        return configuration.getAtomix();
                    }
                });
    }

    @Override
    public void run(HelloWorldConfiguration configuration,
            Environment environment) throws Exception {

        LOGGER.info("start run()");
        final Atomix atomix = configuration.getAtomix().build();

        LOGGER.info("registering resource");
        final HelloWorldResource resource = new HelloWorldResource(atomix,
                configuration.getTemplate(), configuration.getDefaultName());
        environment.jersey().register(resource);

        LOGGER.info("end run()");
    }
}
