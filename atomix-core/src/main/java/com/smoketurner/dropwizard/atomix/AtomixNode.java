package com.smoketurner.dropwizard.atomix;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HostAndPort;
import io.atomix.cluster.Node;
import io.atomix.cluster.Node.Type;
import io.atomix.messaging.Endpoint;
import io.atomix.messaging.impl.NettyMessagingService;
import io.dropwizard.validation.OneOf;

public class AtomixNode {

    @NotEmpty
    private String id = UUID.randomUUID().toString();

    @NotNull
    private HostAndPort endpoint = HostAndPort.fromParts("127.0.0.1",
            NettyMessagingService.DEFAULT_PORT);

    @NotEmpty
    @OneOf({ "client", "data" })
    private String type = "data";

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
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
        return Node.builder(id).withEndpoint(getEndpoint()).withType(getType())
                .build();
    }
}
