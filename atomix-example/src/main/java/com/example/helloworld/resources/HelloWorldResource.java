/*
 * Copyright © 2019 Smoke Turner, LLC (github@smoketurner.com)
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
package com.example.helloworld.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.helloworld.api.Saying;
import com.smoketurner.dropwizard.atomix.AtomixMember;
import io.atomix.core.Atomix;
import io.atomix.core.counter.AtomicCounter;
import io.atomix.protocols.raft.MultiRaftProtocol;
import io.atomix.protocols.raft.ReadConsistency;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {
  private final Atomix atomix;
  private final String template;
  private final String defaultName;

  /**
   * Constructor
   *
   * @param atomix
   * @param template
   * @param defaultName
   */
  public HelloWorldResource(Atomix atomix, String template, String defaultName) {
    this.atomix = Objects.requireNonNull(atomix);
    this.template = template;
    this.defaultName = defaultName;
  }

  @GET
  @Timed
  @Path("/hello-world")
  public Saying sayHello(@QueryParam("name") Optional<String> name) {
    final AtomicCounter counter =
        atomix
            .atomicCounterBuilder("counter")
            .withProtocol(
                MultiRaftProtocol.builder()
                    .withReadConsistency(ReadConsistency.LINEARIZABLE)
                    .build())
            .build();

    final String value = String.format(template, name.orElse(defaultName));
    return new Saying(counter.incrementAndGet(), value);
  }

  @GET
  @Timed
  @Path("/members")
  public List<AtomixMember> getMembers() {
    return atomix.getMembershipService().getMembers().stream()
        .map(m -> AtomixMember.builder().fromMember(m).build())
        .collect(Collectors.toList());
  }
}
