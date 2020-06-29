package com.diamondq.maply.api;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Responsible for performing mapping between objects
 */
public interface MappingService {

  /**
   * Creates a new context
   * 
   * @return the context
   */
  public MapContext createContext();

  /**
   * Generate a new map of map objects
   * 
   * @param pContext the context
   * @return the map
   */
  public Map<String, MapObject> getInitialMapObjects(MapContext pContext);

  /**
   * Loads a MapObject based on a URI
   * 
   * @param pContext the context
   * @param pURI the URI
   * @return the MapObject
   */
  public @Nullable MapObject loadMapObject(MapContext pContext, URI pURI);

  /**
   * Saves a MapObject to a URI
   * 
   * @param pContext the context
   * @param pMapObject the object
   * @param pTargetURI the target
   */
  public void saveMapObject(MapContext pContext, MapObject pMapObject, URI pTargetURI);

  /**
   * Finds the necessary instructions
   * 
   * @param pContext the context
   * @param pSourceObjects the source objects
   * @param pDest the destination object
   * @return the instructions
   */
  public List<MapInstructions> loadMapInstructions(MapContext pContext, Map<String, MapObject> pSourceObjects,
    MapObject pDest);

  /**
   * Performs the mapping using a series of map objects and follows a set of instructions
   * 
   * @param pContext the context
   * @param pSourceObjects the map of Map Objects
   * @param pDest the source object
   * @param pInstructions the instructions
   */
  public void map(MapContext pContext, Map<String, MapObject> pSourceObjects, MapObject pDest,
    List<MapInstructions> pInstructions);

}
