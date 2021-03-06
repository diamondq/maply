package com.diamondq.maply.impl2.mappers;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.MappingService;
import com.diamondq.maply.api2.NamingAdapterService;
import com.diamondq.maply.spi.ContextBuilders;
import com.diamondq.maply.spi2.MapInstruction;
import com.diamondq.maply.spi2.MapInstructionProvider;
import com.diamondq.maply.spi2.MappingProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This MappingProvider is able to construct Java objects from their properties.
 */
@Singleton
public class ObjectConstructor implements MapInstructionProvider, MappingProvider, MapInstruction {

  private final ContextFactory       mContextFactory;

  private final NamingAdapterService mNamingAdapterService;

  @Inject
  public ObjectConstructor(ContextFactory pContextFactory, NamingAdapterService pNamingAdapterService) {
    mContextFactory = pContextFactory;
    mNamingAdapterService = pNamingAdapterService;
  }

  /**
   * @see com.diamondq.maply.spi2.MappingProvider#setup(com.diamondq.maply.api2.MappingService)
   */
  @Override
  public void setup(MappingService pMappingService) {
    try (Context ctx = mContextFactory.newContext(ObjectConstructor.class, this)) {
      Location loc = pMappingService.location("/class:*").build();
      pMappingService.register(Collections.singleton(loc), 1, this);
    }
  }

  /**
   * @see com.diamondq.maply.spi2.MapInstructionProvider#evaluateNeeds(com.diamondq.maply.api2.MappingService,
   *      com.diamondq.maply.api2.Location)
   */
  @Override
  public NeedsResult evaluateNeeds(MappingService pMappingService, Location pWant) {
    try (Context ctx =
      mContextFactory.newContextWithMeta(ObjectConstructor.class, this, pWant, ContextBuilders.sNotIndented)) {

      /* Get the class */

      Class<?> clazz = pWant.getFirstStepClass().get();

      /* Figure out the constructor */

      Set<Location> result = new HashSet<>();
      for (Constructor<?> constructor : clazz.getConstructors()) {
        @NonNull
        Parameter[] parameters = constructor.getParameters();
        for (Parameter param : parameters) {
          String name = param.getName();
          String updatedName = mNamingAdapterService.adaptConstructorParamName(param, name);
          result.add(pMappingService.location(pWant.getXPath()).localName(updatedName).build());
        }
      }

      StringBuilder sb = new StringBuilder();
      sb.append("ObjConstructor(");
      sb.append(clazz.getSimpleName());
      sb.append(')');
      return new NeedsResult(sb.toString(), result, Collections.emptySet(), this, 1);
    }
  }

  /**
   * @see com.diamondq.maply.spi2.MapInstruction#execute()
   */
  @Override
  public void execute() {
    try (Context ctx = mContextFactory.newContextWithMeta(ObjectConstructor.class, this)) {
      throw new UnsupportedOperationException();
      // Map<Location, Object> result = new HashMap<>();
      // for (Location want : pWants) {
      // Object object = pObjs.get(want.dataType);
      // if (object != null)
      // result.put(want, object);
      // }
      // return result;
    }
  }
}
