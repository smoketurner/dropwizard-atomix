/*
 * Copyright © 2018 Smoke Turner, LLC (contact@smoketurner.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smoketurner.dropwizard.atomix;

import java.util.Objects;
import com.smoketurner.dropwizard.atomix.health.AtomixHealthCheck;
import com.smoketurner.dropwizard.atomix.managed.AtomixManager;
import io.atomix.core.Atomix;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class AtomixBundle<C extends Configuration>
    implements ConfiguredBundle<C>, AtomixConfiguration<C> {

  private final String clusterId;

  /**
   * Constructor
   *
   * @param clusterId Atomix cluster ID
   */
  public AtomixBundle(final String clusterId) {
    this.clusterId = Objects.requireNonNull(clusterId);
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    // nothing to initialize
  }

  @Override
  public void run(final C configuration, final Environment environment) throws Exception {

    final AtomixFactory factory = getAtomixFactory(configuration);
    factory.setClusterId(clusterId);

    final Atomix atomix = factory.build();
    environment.lifecycle().manage(new AtomixManager(atomix));
    environment.healthChecks().register("atomix", new AtomixHealthCheck(atomix));
  }
}
