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
package com.smoketurner.dropwizard.atomix.managed;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.atomix.Atomix;
import io.dropwizard.lifecycle.Managed;

public class AtomixManager implements Managed {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AtomixManager.class);
    private final Atomix atomix;

    /**
     * Constructor
     *
     * @param atomix
     *            Atomix instance to manage
     */
    public AtomixManager(@Nonnull final Atomix atomix) {
        this.atomix = Objects.requireNonNull(atomix);
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Starting Atomix (will block until quorum is reached)...");
        atomix.start().join();
        LOGGER.info("Started Atomix");
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Stopping Atomix...");
        atomix.stop();
        LOGGER.info("Stopped Atomix");
    }
}
