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
    private static final String DEFAULT_TYPE = "core";

    @NotEmpty
    private final String id;

    @NotNull
    private final HostAndPort endpoint;

    @NotEmpty
    @OneOf({ "core", "client", "data" })
    private final String type;

    @NotNull
    private final Optional<String> zone;

    @NotNull
    private final Optional<String> rack;

    @NotNull
    private final Optional<String> host;

    @JsonCreator
    private AtomixNode(@JsonProperty("id") String id,
            @JsonProperty("endpoint") HostAndPort endpoint,
            @JsonProperty("type") Optional<String> type,
            @JsonProperty("zone") Optional<String> zone,
            @JsonProperty("rack") Optional<String> rack,
            @JsonProperty("host") Optional<String> host) {
        this.id = id;
        this.endpoint = endpoint;
        this.type = type.orElse(DEFAULT_TYPE);
        this.zone = zone;
        this.rack = rack;
        this.host = host;
    }

    private AtomixNode(Builder builder) {
        this.id = builder.id;
        this.endpoint = builder.endpoint;
        this.type = builder.type;
        this.zone = builder.zone;
        this.rack = builder.rack;
        this.host = builder.host;
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
        private String type = DEFAULT_TYPE;
        private Optional<String> zone = Optional.empty();
        private Optional<String> rack = Optional.empty();
        private Optional<String> host = Optional.empty();

        public Builder fromNode(Node node) {
            withId(node.id());
            withType(node.type());
            withEndpoint(node.endpoint());
            withZone(node.zone());
            withRack(node.rack());
            withHost(node.host());
            return this;
        }

        public Builder withId(String id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        public Builder withId(NodeId id) {
            this.id = id.id();
            return this;
        }

        public Builder withEndpoint(HostAndPort endpoint) {
            this.endpoint = Objects.requireNonNull(endpoint);
            return this;
        }

        public Builder withEndpoint(Endpoint endpoint) {
            this.endpoint = HostAndPort.fromParts(
                    endpoint.host().getHostAddress(), endpoint.port());
            return this;
        }

        public Builder withEndpoint(String host, int port) {
            this.endpoint = HostAndPort.fromParts(host, port);
            return this;
        }

        public Builder withType(String type) {
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public Builder withType(Type type) {
            this.type = type.toString();
            return this;
        }

        public Builder withZone(String zone) {
            this.zone = Optional.ofNullable(zone);
            return this;
        }

        public Builder withRack(String rack) {
            this.rack = Optional.ofNullable(rack);
            return this;
        }

        public Builder withHost(String host) {
            this.host = Optional.ofNullable(host);
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

    @JsonProperty
    public Optional<String> getZone() {
        return zone;
    }

    @JsonProperty
    public Optional<String> getRack() {
        return rack;
    }

    @JsonProperty
    public Optional<String> getHost() {
        return host;
    }

    @JsonIgnore
    public Node toNode() {
        return Node.builder(id)
                .withEndpoint(
                        Endpoint.from(endpoint.getHost(), endpoint.getPort()))
                .withType(Type.valueOf(type.toUpperCase(Locale.ENGLISH)))
                .withZone(zone.orElse(null)).withRack(rack.orElse(null))
                .withHost(host.orElse(null)).build();
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
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id)
                .add("endpoint", endpoint).add("type", type).add("zone", zone)
                .add("rack", rack).add("host", host).toString();
    }
}
