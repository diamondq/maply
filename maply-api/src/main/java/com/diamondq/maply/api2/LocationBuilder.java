package com.diamondq.maply.api2;

/**
 * A Builder that represents the location of an object
 */
public interface LocationBuilder {

  /**
   * Indicates that the object must have the given property with the given variable
   * 
   * @param pProp the property name
   * @param pVar the variable
   * @return the builder
   */
  public LocationBuilder whereEq(String pProp, Variable pVar);

  /**
   * Indicates that the object must have a parent with the given property with the given variable
   * 
   * @param pProp the property name
   * @param pVar the variable
   * @return the builder
   */
  public LocationBuilder whereParentEq(String pProp, Variable pVar);

  /**
   * Indicates that the location must represent a Java class
   * 
   * @param pClass the class
   * @return the builder
   */
  public LocationBuilder classEq(Class<?> pClass);

}
