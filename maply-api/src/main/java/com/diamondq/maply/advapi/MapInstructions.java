package com.diamondq.maply.advapi;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A set of instructions that can map from <S> to <D>
 */
public interface MapInstructions {

  /**
   * Executes the instructions to map the sources to the destination
   * 
   * @param pMapContext the context
   * @param pDestMapObject the dest map object (may be empty)
   * @param pSrcMapObject the source map object
   * @param pWithArray the with objects
   */
  public void map(MapContext pMapContext, MapObject pDestMapObject, MapObject pSrcMapObject,
    @NonNull MapObject @Nullable [] pWithArray);

}
