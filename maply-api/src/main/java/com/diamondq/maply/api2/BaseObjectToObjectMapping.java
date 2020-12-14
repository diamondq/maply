package com.diamondq.maply.api2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.mapbuilders.FromBuilder;
import com.diamondq.maply.api2.mapbuilders.ToBuilder;
import com.diamondq.maply.spi2.MapInstructionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

public class BaseObjectToObjectMapping implements ObjectToObjectMapping {

  protected final @NonNull ToBuilder[] mToBuilders;

  public BaseObjectToObjectMapping(@NonNull ToBuilder... pToObjs) {
    mToBuilders = pToObjs;
  }

  protected static ToBuilder to(Class<?> pClass) {
    return new ToBuilder(pClass);
  }

  /**
   * @see com.diamondq.maply.api2.ObjectToObjectMapping#getMappings(com.diamondq.common.context.ContextFactory,
   *      com.diamondq.maply.api2.MappingService)
   */
  @Override
  public Collection<Mapping> getMappings(ContextFactory pContextFactory, MappingService pMappingService) {
    List<Mapping> result = new ArrayList<>();
    for (ToBuilder b : mToBuilders) {

      for (FromBuilder fb : b.fromBuilders) {

        /* Handle the equals */

        for (String name : fb.equalChildren) {
          Set<Location> toLocations = new HashSet<>();

          toLocations.add(pMappingService.location().step(b.toClass).localName(name).build());
          Location fromLocation = pMappingService.location().step(fb.fromClass).localName(name).build();
          int priority = 1;
          MapInstructionProvider provider = new MapInstructionProvider() {

            /**
             * @see com.diamondq.maply.spi2.MapInstructionProvider#evaluateNeeds(com.diamondq.maply.api2.MappingService,
             *      com.diamondq.maply.api2.Location)
             */
            @Override
            public NeedsResult evaluateNeeds(MappingService pLocalMappingService, Location pWant) {
              try (Context ctx = pContextFactory.newContext(this.getClass(), this, pWant)) {

                /* Merge predicates into the from location */

                Location updatedFromLocation = fromLocation.mergePredicates(pWant);

                StringBuilder sb = new StringBuilder();
                sb.append("ObjToObj(");
                sb.append(fb.fromClass.getSimpleName());
                sb.append('#');
                sb.append(name);
                sb.append('=');
                sb.append(b.toClass.getSimpleName());
                sb.append('#');
                sb.append(name);
                sb.append(')');
                return new NeedsResult(sb.toString(), Collections.singleton(updatedFromLocation),
                  Collections.emptySet(), () -> {
                    throw new UnsupportedOperationException();
                  }, 1);
              }
            }
          };

          result.add(new Mapping(toLocations, priority, provider));
        }

      }

    }
    return result;
  }
}
