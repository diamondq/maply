package com.diamondq.maply.spi;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapInstructions;
import com.google.common.net.MediaType;

import java.util.Set;

/**
 * This interface is used to load instructions
 */
public interface MapInstructionsLoader {

  /**
   * The set of supported file extensions. Used to identify possible maps
   * 
   * @return the set of extensions
   */
  public Set<String> getSupportedFileExtensions();

  /**
   * The set of supported media types (only the type/subtype)
   * 
   * @return the set of media types
   */
  public Set<MediaType> getSupportedMediaTypes();

  /**
   * Returns true if this loader supports this media type. NOTE: It's guaranteed that the media type already is one of
   * the media content types.
   * 
   * @param pIsLoad true if this is for a load or false if it's for a save
   * @param pMediaType the media type
   * @return true if this loader supports it or false otherwise
   */
  public boolean supportsContent(boolean pIsLoad, MediaType pMediaType);

  /**
   * Parse the bytes into a MapInstructions data
   * 
   * @param pContext the context
   * @param pBytesData the bytes
   * @return the instructions
   */
  public MapInstructions load(MapContext pContext, MapBytesData pBytesData);

}
