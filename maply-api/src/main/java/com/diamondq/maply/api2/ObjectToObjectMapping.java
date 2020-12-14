package com.diamondq.maply.api2;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.spi2.MapInstructionProvider;

import java.util.Collection;
import java.util.Set;

public interface ObjectToObjectMapping {

  public static class Mapping {
    public final Set<Location>          locations;

    public final int                    priority;

    public final MapInstructionProvider provider;

    public Mapping(Set<Location> pLocations, int pPriority, MapInstructionProvider pProvider) {
      locations = pLocations;
      priority = pPriority;
      provider = pProvider;
    }

  }

  public Collection<Mapping> getMappings(ContextFactory pContextFactory, MappingService pMappingService);

}
