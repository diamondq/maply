package com.diamondq.maply.api;

import org.apache.tika.mime.MediaType;

public interface MediaTypeLookup {
  /**
   * Returns the MediaType for a given class
   * 
   * @param <D> the type
   * @param pClass the class
   * @return the MediaType
   */
  public <D> MediaType lookup(Class<D> pClass);

}
