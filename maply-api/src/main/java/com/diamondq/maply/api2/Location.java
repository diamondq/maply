package com.diamondq.maply.api2;

import java.util.Optional;

public interface Location extends ToStringIndented {

  Optional<Location> match(Location pRegWant);

  public String getXPath();

  public Optional<Class<?>> getFirstStepClass();

  /**
   * For each predicate on each step in pWant, add them to the matching step (by order) in this and return the new
   * updated Location. NOTE: Does not modify this. If the step count isn't the same, an error is thrown
   * 
   * @param pWant the Location with the predicates
   * @return the new updated Location
   */
  Location mergePredicates(Location pWant);

  Location getFirstStep();
}
