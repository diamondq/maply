package com.diamondq.maply.spi;

import com.diamondq.maply.api.MapObject;
import com.google.common.net.MediaType;

public abstract class AbstractMapObject implements MapObject {

  protected final MediaType mMediaType;

  protected final String    mIdentifier;

  public AbstractMapObject(MediaType pMediaType, String pIdentifier) {
    mMediaType = pMediaType;
    mIdentifier = pIdentifier;
  }

  /**
   * @see com.diamondq.maply.api.MapObject#getMediaType()
   */
  @Override
  public MediaType getMediaType() {
    return mMediaType;
  }

  /**
   * @see com.diamondq.maply.api.MapObject#getIdentifier()
   */
  @Override
  public String getIdentifier() {
    return mIdentifier;
  }

}
