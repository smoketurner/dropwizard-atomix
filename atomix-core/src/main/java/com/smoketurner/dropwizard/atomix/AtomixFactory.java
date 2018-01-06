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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import io.atomix.cluster.Node;
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
    private AtomixNode localNode;

    @NotNull
    private List<AtomixNode> bootstrapNodes = Collections.emptyList();

    /**
     * Default to 7 Raft partitions to allow a leader per node in 7 node
     * clusters
     *
     * @see {@link Atomix.Builder.DEFAULT_COORDINATION_PARTITIONS}
     */
    @Min(1)
    private int numCoordinationPartitions = 7;

    /**
     * Default to 3-node partitions for the best latency/throughput per Raft
     * partition
     *
     * @see {@link Atomix.Builder.DEFAULT_COORDINATION_PARTITION_SIZE}
     */
    @Min(1)
    private int coordinationPartitionSize = 3;

    /**
     * Default to 71 primary-backup partitions - a prime number that creates
     * about 10 partitions per node in a 7-node cluster
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

    @JsonProperty
    public Optional<Node> getLocalNode() {
        return Optional.ofNullable(localNode).map(n -> n.toNode());
    }

    @JsonProperty
    public void setLocalNode(@Nullable AtomixNode node) {
        this.localNode = node;
    }

    @JsonProperty
    public Collection<Node> getBootstrapNodes() {
        return bootstrapNodes.stream().map(n -> n.toNode())
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
    public Atomix build() {
        final Atomix existingAtomix = atomixRef.get();
        if (existingAtomix != null) {
            return existingAtomix;
        }

        LOGGER.info("Atomix Cluster Name: {}", clusterName);

        final Atomix.Builder builder = Atomix.builder()
                .withClusterName(clusterName)
                .withCoordinationPartitions(numCoordinationPartitions)
                .withCoordinationPartitionSize(coordinationPartitionSize)
                .withDataPartitions(numDataPartitions)
                .withBootstrapNodes(getBootstrapNodes());

        final Optional<Node> localNode = getLocalNode();
        localNode.ifPresent(node -> builder.withLocalNode(node));

        final File dataDirectory = getDataDirectory();
        LOGGER.info("Raft Data Directory: {}", dataDirectory);
        builder.withDataDirectory(dataDirectory);

        final Atomix atomix = builder.build();
        if (atomixRef.compareAndSet(null, atomix)) {
            return atomix;
        }
        return build();
    }
}
