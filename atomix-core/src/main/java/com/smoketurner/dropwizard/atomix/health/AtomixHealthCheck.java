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
package com.smoketurner.dropwizard.atomix.health;

import com.codahale.metrics.health.HealthCheck;
import io.atomix.core.Atomix;
import io.dropwizard.util.Duration;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AtomixHealthCheck extends HealthCheck {

  private final Atomix atomix;
  private final Duration timeout;

  /**
   * Constructor with a default ping timeout of 1 second
   *
   * @param atomix Atomix instance
   */
  public AtomixHealthCheck(@Nonnull final Atomix atomix) {
    this(atomix, Duration.seconds(1));
  }

  /**
   * Constructor
   *
   * @param atomix Atomix instance
   * @param timeout Ping timeout
   */
  public AtomixHealthCheck(@Nonnull final Atomix atomix, @Nonnull final Duration timeout) {
    this.atomix = Objects.requireNonNull(atomix);
    this.timeout = Objects.requireNonNull(timeout);
  }

  @Override
  protected Result check() throws Exception {
    return Result.healthy("Atomix is healthly");
  }
}
