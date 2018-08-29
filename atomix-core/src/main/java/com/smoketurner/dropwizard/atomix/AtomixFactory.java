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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.atomix.cluster.ClusterConfig;
import io.atomix.cluster.Member;
import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.cluster.discovery.NodeDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.profile.ConsensusProfile;
import io.atomix.core.profile.ConsensusProfileConfig;
import io.atomix.core.profile.Profile;

public class AtomixFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AtomixFactory.class);
  private final AtomicReference<Atomix> atomixRef = new AtomicReference<>();

  /** @see {@link ClusterConfig.DEFAULT_CLUSTER_NAME} */
  @NotEmpty private String clusterId = "atomix";

  @Nullable private AtomixMember localMember;

  @NotNull private List<AtomixMember> members = Collections.emptyList();

  /** @see {@link ConsensusProfileConfig.dataPath} */
  @NotEmpty private String dataPath = System.getProperty("atomix.data", ".data");

  @JsonProperty
  public String getClusterId() {
    return clusterId;
  }

  @JsonProperty
  public void setClusterId(String id) {
    this.clusterId = id;
  }

  @JsonProperty
  public String getDataPath() {
    return dataPath;
  }

  @JsonProperty
  public void setDataPath(String path) {
    this.dataPath = path;
  }

  @JsonProperty
  public Optional<Member> getLocalMember() {
    return Optional.ofNullable(localMember).map(n -> n.toMember());
  }

  @JsonProperty
  public void setLocalMember(@Nullable AtomixMember member) {
    this.localMember = member;
  }

  @JsonIgnore
  public Set<String> getMemberIds() {
    return members.stream().map(m -> m.getId()).collect(Collectors.toSet());
  }

  @JsonProperty
  public Set<Node> getMembers() {
    return members.stream().map(m -> m.toMember()).collect(Collectors.toSet());
  }

  @JsonProperty
  public void setMembers(List<AtomixMember> members) {
    this.members = members;
  }

  @JsonIgnore
  public Atomix build() {
    final Atomix existingAtomix = atomixRef.get();
    if (existingAtomix != null) {
      return existingAtomix;
    }

    LOGGER.info("Atomix Cluster ID: {} (data path: {})", clusterId, dataPath);

    final Profile consensus =
        ConsensusProfile.builder().withDataPath(dataPath).withMembers(getMemberIds()).build();

    final NodeDiscoveryProvider locationProvider =
        BootstrapDiscoveryProvider.builder().withNodes(getMembers()).build();

    final AtomixBuilder builder =
        Atomix.builder()
            .withClusterId(clusterId)
            .withProfiles(consensus, Profile.dataGrid())
            .withMulticastEnabled(false)
            .withShutdownHook(false)
            .withMembershipProvider(locationProvider);

    final Optional<Member> localMember = getLocalMember();
    localMember.ifPresent(
        member ->
            builder
                .withMemberId(member.id())
                .withAddress(member.address())
                .withHost(member.host())
                .withProperties(member.properties())
                .withZone(member.zone())
                .withRack(member.rack()));

    final Atomix atomix = builder.build();
    if (atomixRef.compareAndSet(null, atomix)) {
      return atomix;
    }
    return build();
  }
}
