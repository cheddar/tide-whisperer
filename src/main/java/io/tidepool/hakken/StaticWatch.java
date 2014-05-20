package io.tidepool.hakken;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public class StaticWatch implements Watch
{
  private List<HostSpec> hostSpecs;

  @JsonCreator
  public StaticWatch(
      @JsonProperty List<HostSpec> hostSpecs
  )
  {
    this.hostSpecs = hostSpecs;
  }

  @Override
  public Watcher makeWatcher(HakkenClient client)
  {
    return new Watcher() {
      @Override
      public void start()
      {

      }

      @Override
      public void close() throws IOException
      {

      }

      @Override
      public List<HostSpec> get()
      {
        return hostSpecs;
      }
    };
  }
}
