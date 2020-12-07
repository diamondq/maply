package com.diamondq.maply.spi2;

import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.MappingService;

import java.util.List;

public interface MapInstructionProvider {

  /**
   * For a given want, evaluate the needs that are required
   * 
   * @param pMappingService the mapping service
   * @param pWant the want
   * @return the list of needs
   */
  public List<Location> evaluateNeeds(MappingService pMappingService, Location pWant);

}
