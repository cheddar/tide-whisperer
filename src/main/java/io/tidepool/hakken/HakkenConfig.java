package io.tidepool.hakken;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.Interval;
import org.joda.time.Period;

/**
 *
 */
public class HakkenConfig
{
  private final String host;
  private final Period heartbeatInterval;
  private final Period pollInterval;
  private final Period resyncInterval;

  @JsonCreator
  public HakkenConfig(
      @JsonProperty("host") String host,
      @JsonProperty("host") Period heartbeatInterval,
      @JsonProperty("host") Period pollInterval,
      @JsonProperty("host") Period resyncInterval
  ) {
    this.host = host;
    this.heartbeatInterval = heartbeatInterval;
    this.pollInterval = pollInterval;
    this.resyncInterval = resyncInterval;
  }

  @JsonProperty
  public String getHost()
  {
    return host;
  }

  @JsonProperty
  public Period getHeartbeatInterval()
  {
    return heartbeatInterval;
  }

  @JsonProperty
  public Period getPollInterval()
  {
    return pollInterval;
  }

  @JsonProperty
  public Period getResyncInterval()
  {
    return resyncInterval;
  }
}
