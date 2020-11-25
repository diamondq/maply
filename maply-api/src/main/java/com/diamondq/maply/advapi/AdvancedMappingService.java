package com.diamondq.maply.advapi;

import com.diamondq.maply.api.MappingService;

import java.util.List;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Responsible for performing mapping between objects
 */
public interface AdvancedMappingService extends MappingService {

  /**
   * Creates a new, empty, MapObject
   * 
   * @param pDestMediaType the class
   * @param pIdentifier the optional identifier
   * @return the MapObject
   */
  public MapObject createEmptyMapObject(MediaType pDestMediaType, @Nullable String pIdentifier);

  public <T> MapObject createMapObject(@NonNull T pValue, @Nullable String pIdentifier);

  /**
   * Creates a new context. A MapContext represents an environment to use when determining how to perform a map. This
   * might represent a user to restrict how mapping occurs, or might represent a dev/prod env difference.
   * 
   * @param pObjects an optional list of objects that could be used to define the context. This is generally the 'pWith'
   *          list
   * @return the context
   */
  public MapContext getOrCreateContext(@NonNull Object @Nullable... pObjects);

  /**
   * Finds the necessary instructions
   * 
   * @param pContext the context
   * @param pDest the destination object
   * @param pSource the source object
   * @param pWith optional MapObjects
   * @return the instructions
   */
  public List<MapInstructions> loadMapInstructions(MapContext pContext, MapObject pDest, MapObject pSource,
    @NonNull MapObject @Nullable... pWith);
}
