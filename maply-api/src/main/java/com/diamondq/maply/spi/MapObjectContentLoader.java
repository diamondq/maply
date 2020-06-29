package com.diamondq.maply.spi;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapObject;
import com.google.common.net.MediaType;

import java.net.URI;
import java.util.Set;

public interface MapObjectContentLoader {
  /**
   * The set of supported media types (only the type/subtype)
   * 
   * @return the set of media types
   */
  public Set<MediaType> getSupportedMediaTypes();

  /**
   * Returns true if this loader supports this media type. NOTE: It's guaranteed that the media type already is one of
   * the supported media types.
   * 
   * @param pIsLoad true if this is for a load or false if it's for a save
   * @param pMediaType the media type
   * @return true if this loader supports it or false otherwise
   */
  public boolean supportsContent(boolean pIsLoad, MediaType pMediaType);

  /**
   * Loads the MapObject from a byte[]
   * 
   * @param pContext the context
   * @param pOriginalURI the original URI used to load the bytes
   * @param pIdentifier the identifier to use
   * @param pBytesData the content type and bytes
   * @return the map object
   */
  public MapObject load(MapContext pContext, URI pOriginalURI, String pIdentifier, MapBytesData pBytesData);

  /**
   * Saves the MapObject to a byte array
   * 
   * @param pContext the context
   * @param pMediaType the media type
   * @param pMapObject the map object
   * @return the byte array
   */
  public byte[] save(MapContext pContext, MediaType pMediaType, MapObject pMapObject);
}
