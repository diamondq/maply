package com.diamondq.maply.advapi;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Defines an object that can be mapped
 */
public interface MapObject extends AutoCloseable {

  /**
   * The media type
   * 
   * @return the MediaType
   */
  public MediaType getMediaType();

  public void setMediaType(MediaType pMediaType);

  /**
   * The identifier
   * 
   * @return the identifier
   */
  public @Nullable String getIdentifier();

  /**
   * Sets the identifier
   * 
   * @param pValue the new identifier
   */
  public void setIdentifier(String pValue);

  /**
   * The value
   * 
   * @return the value
   */
  public @Nullable Object getValue();

  /**
   * Sets a new value into the object
   * 
   * @param pValue the value
   */
  public void setValue(Object pValue);

  /**
   * Returns a copy of this object
   * 
   * @return the copy
   */
  public MapObject copy();
}
