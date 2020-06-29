package com.diamondq.maply.spi;

import com.google.common.net.MediaType;

public class MapBytesData {

  public static final MapBytesData EMPTY_MAPDATA = new MapBytesData();

  /**
   * This location is just defined to make it easier for error messages. Some information (such as passwords) may have
   * been stripped from the original URI to provide this location
   */
  public final String              location;

  public final MediaType           contentType;

  public final byte[]              data;

  public MapBytesData(String pLocation, MediaType pContentType, byte[] pData) {
    location = pLocation;
    contentType = pContentType;
    data = pData;
  }

  private MapBytesData() {
    location = "";
    contentType = MediaType.ANY_TYPE;
    data = new byte[0];
  }
}
