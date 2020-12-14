package com.diamondq.maply.api2;

import java.lang.reflect.Parameter;

public interface NamingAdapterService {

  /**
   * Attempts to adapt the given parameter name
   * 
   * @param pParam the Parameter
   * @param pName the name
   * @return the adapted name
   */
  public String adaptConstructorParamName(Parameter pParam, String pName);

}
