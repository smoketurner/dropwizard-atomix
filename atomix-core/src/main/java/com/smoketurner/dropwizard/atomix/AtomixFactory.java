/**
 * Copyright 2018 Smoke Turner, LLC.
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.atomix.cluster.Member;
import io.atomix.core.Atomix;

public class AtomixFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AtomixFactory.class);
    private final AtomicReference<Atomix> atomixRef = new AtomicReference<>();

    /**
     * Sets the cluster name
     * 
     * @see {@link Atomix.Builder.DEFAULT_CLUSTER_NAME}
     */
    @NotEmpty
    private String clusterName = "atomix";

    @Nullable
    private AtomixMember localMember;

    @NotNull
    private List<AtomixMember> members = Collections.emptyList();

    @JsonProperty
    public String getClusterName() {
        return clusterName;
    }

    @JsonProperty
    public void setClusterName(String name) {
        this.clusterName = name;
    }

    @JsonProperty
    public Optional<Member> getLocalMember() {
        return Optional.ofNullable(localMember).map(n -> n.toMember());
    }

    @JsonProperty
    public void setLocalMember(@Nullable AtomixMember member) {
        this.localMember = member;
    }

    @JsonProperty
    public Collection<Member> getMembers() {
        return members.stream().map(n -> n.toMember())
                .collect(Collectors.toList());
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

        LOGGER.info("Atomix Cluster Name: {}", clusterName);

        final Atomix.Builder builder = Atomix.builder()
                .withClusterName(clusterName).withMembers(getMembers());

        final Optional<Member> localMember = getLocalMember();
        localMember.ifPresent(member -> builder.withLocalMember(member));

        final Atomix atomix = builder.build();
        if (atomixRef.compareAndSet(null, atomix)) {
            return atomix;
        }
        return build();
    }
}
