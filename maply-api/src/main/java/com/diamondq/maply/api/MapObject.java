package com.diamondq.maply.api;

import com.google.common.net.MediaType;

/**
 * Defines an object that can be mapped
 */
public interface MapObject extends AutoCloseable {

  public MediaType getMediaType();

  public String getIdentifier();

}
