package com.diamondq.maply.impl2.mappers;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.MappingService;
import com.diamondq.maply.api2.ObjectToObjectMapping;
import com.diamondq.maply.api2.ObjectToObjectMapping.Mapping;
import com.diamondq.maply.spi2.MappingProvider;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This MappingProvider is able to map an object from one type to another
 */
@Singleton
public class ObjectToObjectMapper implements MappingProvider {

  private final ContextFactory              mContextFactory;

  private final List<ObjectToObjectMapping> mMappings;

  @Inject
  public ObjectToObjectMapper(ContextFactory pContextFactory, List<ObjectToObjectMapping> pMappings) {
    mContextFactory = pContextFactory;
    mMappings = pMappings;
  }

  /**
   * @see com.diamondq.maply.spi2.MappingProvider#setup(com.diamondq.maply.api2.MappingService)
   */
  @Override
  public void setup(MappingService pMappingService) {
    try (Context ctx = mContextFactory.newContext(ObjectToObjectMapper.class, this)) {
      for (ObjectToObjectMapping mapping : mMappings) {
        for (Mapping childMapping : mapping.getMappings(mContextFactory, pMappingService)) {
          pMappingService.register(childMapping.locations, childMapping.priority, childMapping.provider);
        }
      }
    }
  }
}
