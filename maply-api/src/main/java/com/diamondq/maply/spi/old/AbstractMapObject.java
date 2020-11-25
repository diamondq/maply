package com.diamondq.maply.spi.old;

import com.diamondq.maply.advapi.MapObject;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractMapObject implements MapObject {

  protected MediaType        mMediaType;

  protected @Nullable String mIdentifier;

  public AbstractMapObject(MediaType pMediaType, String pIdentifier) {
    mMediaType = pMediaType;
    mIdentifier = pIdentifier;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#getMediaType()
   */
  @Override
  public MediaType getMediaType() {
    return mMediaType;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#setMediaType(org.apache.tika.mime.MediaType)
   */
  @Override
  public void setMediaType(@NonNull MediaType pMediaType) {
    mMediaType = pMediaType;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#getIdentifier()
   */
  @Override
  public @Nullable String getIdentifier() {
    return mIdentifier;
  }

  @Override
  public MapObject copy() {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#setIdentifier(java.lang.String)
   */
  @Override
  public void setIdentifier(@NonNull String pValue) {
    mIdentifier = pValue;
  }

}
