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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This MappingProvider is able to explode Java objects into their properties.
 */
@Singleton
public class ObjectExploder implements MapInstructionProvider, MappingProvider, MapInstruction {

  private final ContextFactory       mContextFactory;

  private final NamingAdapterService mNamingAdapterService;

  @Inject
  public ObjectExploder(ContextFactory pContextFactory, NamingAdapterService pNamingAdapterService) {
    mContextFactory = pContextFactory;
    mNamingAdapterService = pNamingAdapterService;
  }

  /**
   * @see com.diamondq.maply.spi2.MappingProvider#setup(com.diamondq.maply.api2.MappingService)
   */
  @Override
  public void setup(MappingService pMappingService) {
    try (Context ctx = mContextFactory.newContext(ObjectExploder.class, this)) {
      Location loc = pMappingService.location("/class:*/*").build();
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
      mContextFactory.newContextWithMeta(ObjectExploder.class, this, pWant, ContextBuilders.sNotIndented)) {

      /* Get the class */

      Location firstStep = pWant.getFirstStep();

      Set<Location> result = new HashSet<>();

      Set<Location> additionalProvides = new HashSet<>();

      Class<?> clazz = firstStep.getFirstStepClass().get();

      /* Figure out the properties */

      for (Field field : clazz.getFields()) {
        if (Modifier.isPublic(field.getModifiers()) == true) {
          additionalProvides
            .add(pMappingService.location().step(clazz).localName(field.getName()).build().mergePredicates(pWant));
        }
      }
      additionalProvides.remove(pWant);

      // for (Constructor<?> constructor : clazz.getConstructors()) {
      // @NonNull
      // Parameter[] parameters = constructor.getParameters();
      // for (Parameter param : parameters) {
      // String name = param.getName();
      // String updatedName = mNamingAdapterService.adaptConstructorParamName(param, name);
      // result.add(pMappingService.location(pWant.getXPath()).localName(updatedName).build());
      // }
      // }
      result.add(firstStep);

      StringBuilder sb = new StringBuilder();
      sb.append("ObjExploder(");
      sb.append(clazz.getSimpleName());
      sb.append(')');

      return new NeedsResult(sb.toString(), result, additionalProvides, this, 1);
    }
  }

  /**
   * @see com.diamondq.maply.spi2.MapInstruction#execute()
   */
  @Override
  public void execute() {
    try (Context ctx = mContextFactory.newContextWithMeta(ObjectExploder.class, this)) {
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
