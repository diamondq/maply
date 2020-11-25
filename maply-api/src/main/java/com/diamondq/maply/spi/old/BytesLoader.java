package com.diamondq.maply.spi.old;

import com.diamondq.maply.advapi.MapContext;

import java.net.URI;
import java.util.Set;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This interface is used for loading the 'bytes' of a given URI
 */
public interface BytesLoader {

  /**
   * The set of supported URI schemes
   * 
   * @return the set of schemes
   */
  public Set<String> getSupportedSchemes();

  /**
   * Returns true if this loader supports this URI. NOTE: It's guaranteed that the URI already is one of the supported
   * schemes.
   * 
   * @param pIsLoad true if this is for a load or false if it's for a save
   * @param pURI the URI
   * @return true if this loader supports it or false otherwise
   */
  public boolean supportsURI(boolean pIsLoad, URI pURI);

  /**
   * @param pContext the context
   * @param pURI the URI
   * @return the pair of 'content type' / 'bytes'. Will be null if the URI doesn't point to a valid location
   */
  public @Nullable MapBytesData load(MapContext pContext, URI pURI);

  /**
   * Saves the data to the storage
   * 
   * @param pContext the context
   * @param pTargetURI the target URI
   * @param pContentType the content type
   * @param pBytes the bytes
   */
  public void save(MapContext pContext, URI pTargetURI, MediaType pContentType, byte[] pBytes);
}
