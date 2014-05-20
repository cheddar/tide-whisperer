package io.tidepool.hakken;

/**
 *
 */
public interface Watch
{
  public Watcher makeWatcher(HakkenClient client);
}
