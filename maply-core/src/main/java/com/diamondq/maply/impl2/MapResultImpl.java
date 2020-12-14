package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.MapResult;

import java.util.Map;

public class MapResultImpl implements MapResult {

  private final ContextFactory        mContextFactory;

  private final MappingServiceImpl    mMappingService;

  private final Map<Location, Object> mData;

  public MapResultImpl(ContextFactory pContextFactory, MappingServiceImpl pMappingService,
    Map<Location, Object> pData) {
    mContextFactory = pContextFactory;
    mMappingService = pMappingService;
    mData = pData;
  }

  /**
   * @see com.diamondq.maply.api2.MapResult#get(java.lang.Class)
   */
  @Override
  public <T> T get(Class<T> pClass) {
    try (Context ctx = mContextFactory.newContext(MapResultImpl.class, this, pClass)) {
      // Location loc = mMappingService.locationFromClass(pClass);
      // Object dataObj = mData.get(loc);
      // @SuppressWarnings("unchecked")
      // T data = (T) dataObj;
      // return data;
      return null;
    }
  }

}
