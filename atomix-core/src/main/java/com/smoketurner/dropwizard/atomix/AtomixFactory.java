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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.atomix.Atomix;
import io.atomix.cluster.Node;

public class AtomixFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AtomixFactory.class);
    private final AtomicReference<CompletableFuture<Atomix>> atomixRef = new AtomicReference<>();

    /**
     * Sets the cluster name
     * 
     * @see {@link Atomix.Builder.DEFAULT_CLUSTER_NAME}
     */
    @NotEmpty
    private String clusterName = "atomix";

    @Nullable
    private AtomixNode localNode;

    @NotNull
    private List<AtomixNode> bootstrapNodes = Collections.emptyList();

    /**
     * Sets the coordination (Raft) partition size.
     *
     * @see {@link Atomix.Builder.DEFAULT_COORDINATION_PARTITION_SIZE}
     */
    @Min(1)
    private int coordinationPartitionSize = 3;

    /**
     * Sets the number of coordination (Raft) partitions.
     */
    @Min(0)
    private int numCoordinationPartitions = 0;

    /**
     * Sets the number of data partitions.
     *
     * @see {@link Atomix.Builder.DEFAULT_DATA_PARTITIONS}
     */
    @Min(1)
    private int numDataPartitions = 71;

    @Nullable
    private String dataDirectory;

    @JsonProperty
    public String getClusterName() {
        return clusterName;
    }

    @JsonProperty
    public void setClusterName(String name) {
        this.clusterName = name;
    }

    @Nullable
    @JsonProperty
    public Node getLocalNode() {
        if (localNode != null) {
            return localNode.build();
        }
        return null;
    }

    @JsonProperty
    public void setLocalNode(@Nullable AtomixNode node) {
        this.localNode = node;
    }

    @JsonProperty
    public Collection<Node> getBootstrapNodes() {
        return bootstrapNodes.stream().map(n -> n.build())
                .collect(Collectors.toList());
    }

    @JsonProperty
    public void setBootstrapNodes(List<AtomixNode> nodes) {
        this.bootstrapNodes = nodes;
    }

    @JsonProperty
    public int getNumCoordinationPartitions() {
        return numCoordinationPartitions;
    }

    @JsonProperty
    public void setNumCoordinationPartitions(int corePartitions) {
        this.numCoordinationPartitions = corePartitions;
    }

    @JsonProperty
    public int getCoordinationPartitionSize() {
        return coordinationPartitionSize;
    }

    @JsonProperty
    public void setCoordinationPartitionSize(int size) {
        this.coordinationPartitionSize = size;
    }

    @JsonProperty
    public int getNumDataPartitions() {
        return numDataPartitions;
    }

    @JsonProperty
    public void setNumDataPartitions(int dataPartitions) {
        this.numDataPartitions = dataPartitions;
    }

    @JsonProperty
    public File getDataDirectory() {
        if (dataDirectory == null) {
            return new File(System.getProperty("user.dir"), "data");
        }
        return new File(dataDirectory);
    }

    @JsonProperty
    public void setDataDirectory(String directory) {
        this.dataDirectory = directory;
    }

    @JsonIgnore
    public CompletableFuture<Atomix> build() {
        LOGGER.info("start build()");
        final CompletableFuture<Atomix> existingAtomix = atomixRef.get();
        if (existingAtomix != null) {
            LOGGER.info("end build(): Returning existing Atomix reference");
            return existingAtomix;
        }

        final Node localNode = getLocalNode();

        final Atomix.Builder builder = Atomix.builder()
                .withClusterName(clusterName);
        if (localNode != null) {
            builder.withLocalNode(localNode);
        }
        if (numCoordinationPartitions > 0) {
            // builder.withCoordinationPartitions(numCoordinationPartitions);
        }

        builder.withBootstrapNodes(getBootstrapNodes())
                // .withCoordinationPartitionSize(coordinationPartitionSize)
                // .withDataPartitions(numDataPartitions)
                .withDataDirectory(getDataDirectory());

        final CompletableFuture<Atomix> atomix = builder.buildAsync();
        if (atomixRef.compareAndSet(null, atomix)) {
            LOGGER.info("end build(): Returning new Atomix reference");
            return atomix;
        }

        LOGGER.info(
                "end build(): reference was already set, calling build() again");
        return build();
    }
}
