package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.LocationBuilder;
import com.diamondq.maply.api2.PreparedMap;
import com.diamondq.maply.api2.PreparedMapBuilder;

import java.util.ArrayList;
import java.util.List;

public class MappingBuilderImpl implements PreparedMapBuilder {

  private final MappingServiceImpl       mMappingService;

  private final ContextFactory           mContextFactory;

  public final List<LocationBuilderImpl> wants;

  public final List<LocationBuilderImpl> withs;

  public MappingBuilderImpl(ContextFactory pContextFactory, MappingServiceImpl pMappingService) {
    mContextFactory = pContextFactory;
    mMappingService = pMappingService;
    wants = new ArrayList<>();
    withs = new ArrayList<>();
  }

  /**
   * @see com.diamondq.maply.api2.PreparedMapBuilder#build()
   */
  @Override
  public PreparedMap build() {
    try (Context ctx = mContextFactory.newContext(MappingBuilderImpl.class, this)) {
      return mMappingService.prepare(this);
    }
  }

  /**
   * @see com.diamondq.maply.api2.PreparedMapBuilder#want(com.diamondq.maply.api2.LocationBuilder)
   */
  @Override
  public PreparedMapBuilder want(LocationBuilder pLocation) {
    try (Context ctx = mContextFactory.newContext(MappingBuilderImpl.class, this, pLocation)) {
      if (pLocation instanceof LocationBuilderImpl)
        wants.add((LocationBuilderImpl) pLocation);
      else
        throw new IllegalStateException("Only LocationBuilderImpl's are supported");
      return this;
    }
  }

  /**
   * @see com.diamondq.maply.api2.PreparedMapBuilder#with(com.diamondq.maply.api2.LocationBuilder)
   */
  @Override
  public PreparedMapBuilder with(LocationBuilder pLocation) {
    try (Context ctx = mContextFactory.newContext(MappingBuilderImpl.class, this, pLocation)) {
      if (pLocation instanceof LocationBuilderImpl)
        withs.add((LocationBuilderImpl) pLocation);
      else
        throw new IllegalStateException("Only LocationBuilderImpl's are supported");
      return this;
    }
  }

}
