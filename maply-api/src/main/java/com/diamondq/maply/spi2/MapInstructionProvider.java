package com.diamondq.maply.spi2;

import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.MappingService;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface MapInstructionProvider {

  public static class NeedsResult {
    public final String                   name;

    public final Set<Location>            needs;

    public final Set<Location>            additionalProvides;

    public final @Nullable MapInstruction instruction;

    public final int                      priority;

    public NeedsResult(String pName, Set<Location> pNeeds, Set<Location> pAdditionalProvides,
      @Nullable MapInstruction pInstruction, int pPriority) {
      name = pName;
      needs = pNeeds;
      additionalProvides = pAdditionalProvides;
      instruction = pInstruction;
      priority = pPriority;
    }
  }

  /**
   * For a given want, evaluate the needs that are required
   * 
   * @param pMappingService the mapping service
   * @param pWant the want
   * @return the list of needs
   */
  public NeedsResult evaluateNeeds(MappingService pMappingService, Location pWant);

}
