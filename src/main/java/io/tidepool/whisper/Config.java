package io.tidepool.whisper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 */
public class Config extends Configuration
{
  @NotEmpty
  private String mongoConnectionString = "mongodb://localhost/streams";

  @JsonProperty
  public String getMongoConnectionString()
  {
    return mongoConnectionString;
  }

  public void setMongoConnectionString(String mongoConnectionString)
  {
    this.mongoConnectionString = mongoConnectionString;
  }

  @Valid
  @NotNull
  @JsonProperty
  private HttpClientConfiguration httpClient = new HttpClientConfiguration();

  public HttpClientConfiguration getHttpClientConfiguration() {
    return httpClient;
  }
}
