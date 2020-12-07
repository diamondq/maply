package com.diamondq.maply.impl2.mappers;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.DataType;
import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.MappingService;
import com.diamondq.maply.spi2.MapInstructionProvider;
import com.diamondq.maply.spi2.MappingProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This MappingProvider is able to construct Java objects from their properties.
 */
@Singleton
public class ObjectConstructor implements MapInstructionProvider, MappingProvider {

  private ContextFactory mContextFactory;

  @Inject
  public ObjectConstructor(ContextFactory pContextFactory) {
    mContextFactory = pContextFactory;
  }

  /**
   * @see com.diamondq.maply.spi2.MappingProvider#setup(com.diamondq.maply.api2.MappingService)
   */
  @Override
  public void setup(MappingService pMappingService) {
    Location loc = pMappingService.location(pMappingService.dataType(DataType.JAVA_CLASS_NS, "*", null, null),
      MappingService.JAVA_CLASS, Collections.emptyList());
    pMappingService.register(Collections.singletonList(loc), 1, this);
  }

  /**
   * @see com.diamondq.maply.spi2.MapInstructionProvider#evaluateNeeds(com.diamondq.maply.api2.MappingService,
   *      com.diamondq.maply.api2.Location)
   */
  @Override
  public List<Location> evaluateNeeds(MappingService pMappingService, Location pWant) {
    try (Context ctx = mContextFactory.newContext(ObjectConstructor.class, this, pWant)) {

      if (pWant.dataType.namespace.equals(DataType.JAVA_CLASS_NS) == false)
        throw new IllegalStateException();

      /* Get the class */

      Class<?> clazz = (Class<?>) Objects.requireNonNull(pWant.dataType.data);

      /* Figure out the constructor */

      List<Location> result = new ArrayList<>();
      for (Constructor<?> constructor : clazz.getConstructors()) {
        @NonNull
        Parameter[] parameters = constructor.getParameters();
        for (Parameter param : parameters) {
          String name = param.getName();
          result.add(pMappingService.location(
            pMappingService.dataType(pWant.dataType.namespace, pWant.dataType.name, name, null), null, pWant.where));
        }
      }

      return result;
    }
  }

  /**
   * @see com.diamondq.maply.spi2.MapInstruction#execute(java.util.Map, java.util.Map, java.util.Map, java.util.List,
   *      java.util.List)
   */
  // @Override
  // public Map<Location, Object> execute(Map<Location, Object> pData, Map<String, Object> pVars,
  // Map<DataType, Object> pObjs, List<Location> pWants, List<Location> pWiths) {
  // try (Context ctx = mContextFactory.newContextWithMeta(ObjectConstructor.class, this, pData,
  // ContextBuilders.sNotIndentedMapKey, pVars, null, pObjs, ContextBuilders.sNotIndentedMapKey, pWants,
  // ContextBuilders.sNotIndentedCol, pWiths, ContextBuilders.sNotIndentedCol)) {
  // Map<Location, Object> result = new HashMap<>();
  // for (Location want : pWants) {
  // Object object = pObjs.get(want.dataType);
  // if (object != null)
  // result.put(want, object);
  // }
  // return result;
  // }
  // }
}
