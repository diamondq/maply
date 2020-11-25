package com.diamondq.maply.impl;

import com.diamondq.maply.advapi.MapObject;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MapObjectImpl implements MapObject {

  private @Nullable Object mValue;

  private MediaType        mMediaType;

  private @Nullable String mIdentifier;

  public MapObjectImpl(@Nullable Object pValue, MediaType pMediaType, @Nullable String pIdentifier) {
    super();
    mValue = pValue;
    mMediaType = pMediaType;
    mIdentifier = pIdentifier;
  }

  /**
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    Object value = mValue;
    if (value != null)
      if (value instanceof AutoCloseable) {
        ((AutoCloseable) value).close();
      }
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
  public void setMediaType(MediaType pMediaType) {
    mMediaType = pMediaType;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#getIdentifier()
   */
  @Override
  public @Nullable String getIdentifier() {
    return mIdentifier;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#setIdentifier(java.lang.String)
   */
  @Override
  public void setIdentifier(String pValue) {
    mIdentifier = pValue;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#getValue()
   */
  @Override
  public @Nullable Object getValue() {
    return mValue;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#setValue(java.lang.Object)
   */
  @Override
  public void setValue(Object pValue) {
    mValue = pValue;
  }

  /**
   * @see com.diamondq.maply.advapi.MapObject#copy()
   */
  @Override
  public MapObject copy() {
    return new MapObjectImpl(mValue, mMediaType, mIdentifier);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName());
    sb.append("[id=");
    sb.append(mIdentifier);
    sb.append(", mediatype=");
    sb.append(mMediaType);
    sb.append(", value=");
    sb.append(mValue);
    sb.append("]");
    return sb.toString();
  }
}
