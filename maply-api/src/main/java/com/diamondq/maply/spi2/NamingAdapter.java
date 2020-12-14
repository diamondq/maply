package com.diamondq.maply.spi2;

import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Responsible for naming conversions
 */
public interface NamingAdapter {

  public static class NamingResult {
    public final String name;

    public final int    priority;

    public NamingResult(String pName, int pPriority) {
      name = pName;
      priority = pPriority;
    }

  }

  /**
   * Attempt to adapt the constructor parameter name
   * 
   * @param pParam the parameter
   * @param pName the name
   * @return the result
   */
  public Optional<NamingResult> adaptConstructorParamName(Parameter pParam, String pName);
}
