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

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.smoketurner.dropwizard.atomix.AtomixBundle;
import com.smoketurner.dropwizard.atomix.AtomixFactory;
import com.smoketurner.dropwizard.atomix.managed.AtomixManager;
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
        final CompletableFuture<Atomix> atomixFuture = configuration.getAtomix()
                .build();

        LOGGER.info("registering resource");
        // final HelloWorldResource resource = new
        // HelloWorldResource(atomixFuture,configuration.getTemplate(),
        // configuration.getDefaultName());
        // environment.jersey().register(resource);

        LOGGER.info("registering manager");
        environment.lifecycle().manage(new AtomixManager(atomixFuture));

        LOGGER.info("end run()");
    }
}
