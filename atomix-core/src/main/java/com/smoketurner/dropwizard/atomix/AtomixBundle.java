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
package com.smoketurner.dropwizard.atomix;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.smoketurner.dropwizard.atomix.managed.AtomixManager;
import io.atomix.Atomix;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class AtomixBundle<C extends Configuration>
        implements ConfiguredBundle<C>, AtomixConfiguration<C> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AtomixBundle.class);
    private final String clusterName;

    /**
     * Constructor
     *
     * @param clusterName
     *            Atomix cluster name
     */
    public AtomixBundle(@Nonnull final String clusterName) {
        this.clusterName = Objects.requireNonNull(clusterName);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // nothing to initialize
    }

    @Override
    public void run(final C configuration, final Environment environment)
            throws Exception {

        LOGGER.info("start run()");

        final AtomixFactory factory = getAtomixFactory(configuration);

        LOGGER.info("Setting Atomix cluster name to: {}", clusterName);
        factory.setClusterName(clusterName);

        final Atomix atomix = factory.build();

        LOGGER.info("registering manager");
        environment.lifecycle().manage(new AtomixManager(atomix));

        LOGGER.info("end run()");
    }
}
