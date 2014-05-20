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
public class GatekeeperClient
{
  private HttpClient http;
  private TokenMonster tokenMonster;
  private ObjectMapper jackson;

  public GatekeeperClient(
      HttpClient http,
      TokenMonster tokenMonster,
      ObjectMapper jackson
  )
  {
    this.http = http;
    this.tokenMonster = tokenMonster;
    this.jackson = jackson;
  }

  public boolean canViewData(String userId, String groupId) {
    HttpGet req = new HttpGet(String.format("http://localhost:9123/access/%s/%s", groupId, userId));
    req.addHeader("x-tidepool-session-token", tokenMonster.getToken());

    try {
      Map perms = jackson.readValue(http.execute(req).getEntity().getContent(), Map.class);
      return perms.containsKey("view") || perms.containsKey("root");
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}
