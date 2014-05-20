package io.tidepool.hakken;

import java.util.List;
import java.util.function.Supplier;

/**
 *
 */
public interface Updater extends Supplier<List<HostSpec>>
{
  public void update(List<HostSpec> updates);
}
