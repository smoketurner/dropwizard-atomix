package com.smoketurner.dropwizard.atomix;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.net.HostAndPort;
import io.atomix.cluster.Node;
import io.atomix.cluster.Node.Type;
import io.atomix.cluster.NodeId;
import io.atomix.messaging.Endpoint;
import io.atomix.messaging.impl.NettyMessagingService;
import io.dropwizard.validation.OneOf;

@Immutable
public final class AtomixNode {

    @NotEmpty
    private final String id;

    @NotNull
    private final HostAndPort endpoint;

    @NotEmpty
    @OneOf({ "client", "data" })
    private final String type;

    @JsonCreator
    private AtomixNode(@JsonProperty("id") String id,
            @JsonProperty("endpoint") HostAndPort endpoint,
            @JsonProperty("type") Optional<String> type) {
        this.id = id;
        this.endpoint = endpoint;
        this.type = type.orElse("data");
    }

    private AtomixNode(Builder builder) {
        this.id = builder.id;
        this.endpoint = builder.endpoint;
        this.type = builder.type;
    }

    public static AtomixNode create() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private HostAndPort endpoint = HostAndPort.fromParts("127.0.0.1",
                NettyMessagingService.DEFAULT_PORT);
        private String type = "data";

        public Builder fromNode(Node node) {
            id(node.id());
            type(node.type());
            endpoint(node.endpoint());
            return this;
        }

        public Builder id(String id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        public Builder id(NodeId id) {
            this.id = id.id();
            return this;
        }

        public Builder endpoint(HostAndPort endpoint) {
            this.endpoint = Objects.requireNonNull(endpoint);
            return this;
        }

        public Builder endpoint(Endpoint endpoint) {
            this.endpoint = HostAndPort.fromParts(
                    endpoint.host().getHostAddress(), endpoint.port());
            return this;
        }

        public Builder endpoint(String host, int port) {
            this.endpoint = HostAndPort.fromParts(host, port);
            return this;
        }

        public Builder type(String type) {
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public Builder type(Type type) {
            this.type = type.toString();
            return this;
        }

        public AtomixNode build() {
            return new AtomixNode(this);
        }
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public HostAndPort getEndpoint() {
        return endpoint;
    }

    @JsonProperty
    public String getType() {
        return type;
    }

    @JsonIgnore
    public Node toNode() {
        return Node.builder(id)
                .withEndpoint(
                        Endpoint.from(endpoint.getHost(), endpoint.getPort()))
                .withType(Type.valueOf(type.toUpperCase(Locale.ENGLISH)))
                .build();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        final AtomixNode other = (AtomixNode) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(endpoint, other.endpoint)
                && Objects.equals(type, other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, endpoint, type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id)
                .add("endpoint", endpoint).add("type", type).toString();
    }
}
