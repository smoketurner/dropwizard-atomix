package com.smoketurner.dropwizard.atomix;

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
import io.atomix.cluster.Member;
import io.atomix.cluster.MemberId;
import io.atomix.utils.net.Address;

@Immutable
public final class AtomixMember {
    private static final int DEFAULT_PORT = 5679;

    @NotEmpty
    private final String id;

    @NotNull
    private final HostAndPort address;

    @NotNull
    private final Optional<String> zone;

    @NotNull
    private final Optional<String> rack;

    @NotNull
    private final Optional<String> host;

    @JsonCreator
    private AtomixMember(@JsonProperty("id") String id,
            @JsonProperty("address") HostAndPort address,
            @JsonProperty("zone") Optional<String> zone,
            @JsonProperty("rack") Optional<String> rack,
            @JsonProperty("host") Optional<String> host) {
        this.id = id;
        this.address = address;
        this.zone = zone;
        this.rack = rack;
        this.host = host;
    }

    private AtomixMember(Builder builder) {
        this.id = builder.id;
        this.address = builder.address;
        this.zone = builder.zone;
        this.rack = builder.rack;
        this.host = builder.host;
    }

    public static AtomixMember create() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private HostAndPort address = HostAndPort.fromParts("127.0.0.1",
                DEFAULT_PORT);
        private Optional<String> zone = Optional.empty();
        private Optional<String> rack = Optional.empty();
        private Optional<String> host = Optional.empty();

        public Builder fromMember(Member member) {
            withId(member.id());
            withAddress(member.address());
            withZone(member.zone());
            withRack(member.rack());
            withHost(member.host());
            return this;
        }

        public Builder withId(String id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        public Builder withId(MemberId id) {
            this.id = id.id();
            return this;
        }

        public Builder withAddress(HostAndPort address) {
            this.address = Objects.requireNonNull(address);
            return this;
        }

        public Builder withAddress(Address address) {
            this.address = HostAndPort.fromParts(address.host(),
                    address.port());
            return this;
        }

        public Builder withAddress(String host, int port) {
            this.address = HostAndPort.fromParts(host, port);
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

        public AtomixMember build() {
            return new AtomixMember(this);
        }
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public HostAndPort getAddress() {
        return address;
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
    public Member toMember() {
        return Member.builder(id)
                .withAddress(Address.from(address.getHost(), address.getPort()))
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

        final AtomixMember other = (AtomixMember) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id)
                .add("address", address).add("zone", zone).add("rack", rack)
                .add("host", host).toString();
    }
}