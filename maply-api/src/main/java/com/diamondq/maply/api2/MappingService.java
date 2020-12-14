package com.diamondq.maply.api2;

import com.diamondq.maply.api2.operationbuilder.PreparedOperationBuilder;
import com.diamondq.maply.api2.operationbuilder.XPathBuilder;
import com.diamondq.maply.spi2.MapInstructionProvider;

import java.util.Set;

/**
 * Responsible for performing mapping between objects
 */
public interface MappingService {

  /**
   * Start a new PreparedMap builder
   * 
   * @return the builder
   */
  public PreparedOperationBuilder preparedOperation();

  public XPathBuilder<Location> location();

  public XPathBuilder<Location> location(String pPath);

  public void register(Set<Location> pProvides, int pPriority, MapInstructionProvider pProvider);

}
