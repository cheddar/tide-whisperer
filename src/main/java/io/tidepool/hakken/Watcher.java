package io.tidepool.hakken;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 */
public abstract class Watcher implements Supplier<List<HostSpec>>, Closeable
{
  public abstract void start();

  public void stop() throws IOException
  {
    close();
  }
}
