package com.diamondq.maply.spi.base;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.spi.MapProcessor;

import java.util.List;

public abstract class BaseMapProcessor implements MapProcessor {
  protected final ContextFactory         mContextFactory;

  protected final List<MapProcessorInfo> mSupported;

  public BaseMapProcessor(ContextFactory pContextFactory, List<MapProcessorInfo> pSupported) {
    mContextFactory = pContextFactory;

    mSupported = pSupported;
  }

  /**
   * @see com.diamondq.maply.spi.MapProcessor#getSupported()
   */
  @Override
  public List<MapProcessorInfo> getSupported() {
    return mSupported;
  }
}
