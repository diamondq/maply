package com.diamondq.maply.api;

import java.util.Map;

public interface MapInstructions {

  /**
   * Executes the instructions to map the sources to the destination
   * 
   * @param pContext the context
   * @param pObjects the objects
   */
  void map(MapContext pContext, Map<String, MapObject> pObjects);

}
