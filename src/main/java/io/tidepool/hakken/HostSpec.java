package io.tidepool.hakken;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;

import java.util.Map;

/**
 *
 */
public class HostSpec
{
  private final String protocol;
  private final String host;
  private final Map<String, Object> fields;

  @JsonCreator
  public static HostSpec create(Map<String, Object> spec) {
    String host = Preconditions.checkNotNull(spec.get("host"), "host cannot be null").toString();
    String protocol = spec.get("protocol") == null ? "http" : spec.get("protocol").toString();

    return new HostSpec(protocol, host, spec);
  }

  public HostSpec(
      String protocol,
      String host,
      Map<String, Object> fields
  ) {
    this.protocol = protocol;
    this.host = host;
    this.fields = fields;
  }

  public String getProtocol()
  {
    return protocol;
  }

  public String getHost()
  {
    return host;
  }

  public Map<String, Object> getFields()
  {
    return fields;
  }

  public String toUrlString()
  {
    return String.format("%s://%s", protocol, host);
  }
}
