package io.tidepool.whisper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class SeagullClient
{
  private final HttpClient http;
  private final ObjectMapper jackson;

  public SeagullClient(
      HttpClient http,
      ObjectMapper jackson
  ) {
    this.http = http;
    this.jackson = jackson;
  }

  public Map getPrivatePair(String userId, String hashName, String token)
  {
    HttpGet req = new HttpGet(String.format("http://localhost:9120/%s/private/%s", userId, hashName));
    req.addHeader("x-tidepool-session-token", token);

    try {
      return jackson.readValue(http.execute(req).getEntity().getContent(), Map.class);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
