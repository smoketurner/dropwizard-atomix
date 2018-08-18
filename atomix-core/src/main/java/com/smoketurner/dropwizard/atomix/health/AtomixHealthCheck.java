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
import io.atomix.cluster.Member;
import io.atomix.core.Atomix;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class AtomixHealthCheck extends HealthCheck {

  private final Atomix atomix;

  /**
   * Constructor
   *
   * @param atomix Atomix instance
   */
  public AtomixHealthCheck(@Nonnull final Atomix atomix) {
    this.atomix = Objects.requireNonNull(atomix);
  }

  @Override
  protected Result check() throws Exception {
    final Set<Member> members = atomix.getMembershipService().getReachableMembers();
    if (members.size() > 0) {
      return Result.healthy(String.format("Cluster is healthly (%d members)", members.size()));
    }
    return Result.unhealthy("No reachable members found");
  }
}
