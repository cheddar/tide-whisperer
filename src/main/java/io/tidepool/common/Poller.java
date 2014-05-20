package io.tidepool.common;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class Poller
{
  private ScheduledExecutorService cron;

  public Poller(ScheduledExecutorService cron)
  {
    this.cron = cron;
  }

  public Closeable scheduleWithFixedDelay(Runnable runnable, long immediateDelay, long delay, TimeUnit units)
  {
    final AtomicBoolean repeat = new AtomicBoolean(true);
    cron.schedule(
        (Runnable) () -> {
          if (repeat.get()) {
            doScheduled(runnable, delay, units, repeat);
          }
        },
        immediateDelay,
        units
    );

    return new Closeable() {
      @Override
      public void close() throws IOException
      {
        repeat.set(false);
      }
    };
  }

  private void doScheduled(Runnable runnable, long delay, TimeUnit units, AtomicBoolean repeat)
  {
    runnable.run();
    cron.schedule(
        (Runnable)() -> {
          if (repeat.get()) {
            doScheduled(runnable, delay, units, repeat);
          }
        },
        delay,
        units
    );
  }


}
