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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.net.HostAndPort;
import io.atomix.cluster.Member;
import io.atomix.cluster.MemberBuilder;
import io.atomix.cluster.MemberId;
import io.atomix.utils.net.Address;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

@Immutable
public final class AtomixMember {
  /** @see {@link Address.DEFAULT_PORT} */
  private static final int DEFAULT_PORT = 5679;

  @NotEmpty private final String id;

  @NotNull private final HostAndPort address;

  @NotNull private final Optional<String> zone;

  @NotNull private final Optional<String> rack;

  @NotNull private final Optional<String> host;

  @NotNull private final Optional<Properties> properties;

  @JsonCreator
  private AtomixMember(
      @JsonProperty("id") String id,
      @JsonProperty("address") HostAndPort address,
      @JsonProperty("zone") Optional<String> zone,
      @JsonProperty("rack") Optional<String> rack,
      @JsonProperty("host") Optional<String> host,
      @JsonProperty("properties") Optional<Properties> properties) {
    this.id = id;
    this.address = address;
    this.zone = zone;
    this.rack = rack;
    this.host = host;
    this.properties = properties;
  }

  private AtomixMember(Builder builder) {
    this.id = builder.id;
    this.address = builder.address;
    this.zone = builder.zone;
    this.rack = builder.rack;
    this.host = builder.host;
    this.properties = builder.properties;
  }

  public static AtomixMember create() {
    return builder().build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String id = UUID.randomUUID().toString();
    private HostAndPort address = HostAndPort.fromParts("127.0.0.1", DEFAULT_PORT);
    private Optional<String> zone = Optional.empty();
    private Optional<String> rack = Optional.empty();
    private Optional<String> host = Optional.empty();
    private Optional<Properties> properties = Optional.empty();

    public Builder fromMember(Member member) {
      withId(member.id());
      withAddress(member.address());
      withZone(member.zone());
      withRack(member.rack());
      withHost(member.host());
      withProperties(member.properties());
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
      this.address = HostAndPort.fromParts(address.host(), address.port());
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

    public Builder withProperties(Properties properties) {
      this.properties = Optional.ofNullable(properties);
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

  @JsonProperty
  public Optional<Properties> getProperties() {
    return properties;
  }

  @JsonIgnore
  public Member toMember() {
    final MemberBuilder builder =
        Member.builder(id).withAddress(address.getHost(), address.getPort());
    zone.ifPresent(zone -> builder.withZone(zone));
    rack.ifPresent(rack -> builder.withRack(rack));
    host.ifPresent(host -> builder.withHost(host));
    properties.ifPresent(properties -> builder.withProperties(properties));
    return builder.build();
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
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("address", address)
        .add("zone", zone)
        .add("rack", rack)
        .add("host", host)
        .add("properties", properties)
        .toString();
  }
}
