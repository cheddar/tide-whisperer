package io.tidepool.whisper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class UserApiClient implements TokenMonster
{
  private final Object lock = new Object();

  private final HttpClient http;
  private ObjectMapper jackson;

  private volatile long lastTokenLoad = 0;
  private volatile String token = null;

  public UserApiClient(
      HttpClient http,
      ObjectMapper jackson
  )
  {
    this.http = http;
    this.jackson = jackson;
  }

  public Map checkToken(String token)
  {
    HttpGet req = new HttpGet(String.format("http://localhost:9107/token/%s", token));
    req.addHeader("x-tidepool-session-token", getToken());

    try {
      return jackson.readValue(http.execute(req).getEntity().getContent(), Map.class);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public String getToken()
  {
    synchronized (lock) {
      if (token == null || System.currentTimeMillis() > lastTokenLoad + 15 * 60 * 1000) {
        HttpPost req = new HttpPost("http://localhost:9107/serverlogin");
        req.setHeader("x-tidepool-server-name", "tide-whisperer");
        req.setHeader("x-tidepool-server-secret", "This needs to be the same secret everywhere. YaHut75NsK1f9UKUXuWqxNN0RUwHFBCy");

        try {
          HttpResponse res = http.execute(req);
          token = res.getHeaders("x-tidepool-session-token")[0].getValue();
        } catch (IOException e) {
          throw Throwables.propagate(e);
        }
      }

      return token;
    }
  }
}
