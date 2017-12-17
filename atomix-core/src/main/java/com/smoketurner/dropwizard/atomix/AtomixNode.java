package com.smoketurner.dropwizard.atomix;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HostAndPort;
import io.atomix.cluster.Node;
import io.atomix.cluster.Node.Type;
import io.atomix.cluster.NodeId;
import io.atomix.messaging.Endpoint;
import io.atomix.messaging.impl.NettyMessagingService;
import io.dropwizard.validation.OneOf;

public class AtomixNode {

    @Nullable
    private String id;

    @NotNull
    private HostAndPort endpoint = HostAndPort.fromParts("127.0.0.1",
            NettyMessagingService.DEFAULT_PORT);

    @NotEmpty
    @OneOf({ "client", "data" })
    private String type = "data";

    @Nullable
    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(@Nullable String id) {
        this.id = id;
    }

    @JsonProperty
    public Endpoint getEndpoint() {
        return Endpoint.from(endpoint.getHost(), endpoint.getPort());
    }

    @JsonProperty
    public void setEndpoint(HostAndPort endpoint) {
        this.endpoint = endpoint;
    }

    @JsonProperty
    public Type getType() {
        switch (type) {
        case "client":
            return Node.Type.CLIENT;
        case "data":
        default:
            return Node.Type.DATA;
        }
    }

    @JsonProperty
    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public Node build() {
        final Node.Builder builder = Node.builder();
        if (id != null) {
            builder.withId(NodeId.from(id));
        }
        final Node node = builder.withEndpoint(getEndpoint())
                .withType(getType()).build();
        return node;
    }
}
