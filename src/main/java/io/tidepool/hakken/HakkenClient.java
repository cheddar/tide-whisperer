package io.tidepool.hakken;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.tidepool.common.Poller;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class HakkenClient
{
  private final HakkenConfig config;
  private final HttpClient http;
  private final ObjectMapper jackson;
  private final Poller poller;

  private final RequestConfig reqConfig = RequestConfig.custom()
                                                       .setConnectTimeout(5000)
                                                       .setSocketTimeout(5000)
                                                       .build();

  public HakkenClient(
      HakkenConfig config,
      HttpClient http,
      ObjectMapper jackson,
      Poller poller
  )
  {
    this.config = config;
    this.http = http;
    this.jackson = jackson;
    this.poller = poller;
  }

  public Watcher watch(final String serviceName, Updater updater)
  {
    return new Watcher()
    {
      private final AtomicBoolean started = new AtomicBoolean(false);

      private volatile Closeable pollHandle = () -> {};

      @Override
      public void start()
      {
        if (!started.getAndSet(true)) {
          Closeable pollHandle = poller.scheduleWithFixedDelay(
              () -> {
                try {

                  HttpGet request = new HttpGet(makeUrl("/v1/listings/" + serviceName));
                  request.setConfig(reqConfig);

                  updater.update(Lists.newArrayList(jackson.readValues(
                      jackson.getFactory().createParser(http.execute(request).getEntity().getContent()),
                      HostSpec.class
                  )));
                } catch (Exception ignored) {

                }
              },
              0,
              config.getPollInterval().toStandardDuration().getMillis(),
              TimeUnit.MILLISECONDS
          );
        }
      }

      @Override
      public void close() throws IOException
      {
        if (started.getAndSet(true)) {
          pollHandle.close();
          pollHandle = () -> {};
          started.set(false);
        }
      }

      @Override
      public List<HostSpec> get()
      {
        return updater.get();
      }
    };
  }

  private String makeUrl(String path)
  {
    return String.format("http://%s%s", config.getHost(), path);
  }
}
