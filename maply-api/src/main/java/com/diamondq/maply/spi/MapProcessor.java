package com.diamondq.maply.spi;

import com.diamondq.maply.advapi.MapInstructions;

import java.util.List;

import org.apache.tika.mime.MediaType;

/**
 * A specific type of instruction where a Java class is used to perform the map
 */
public interface MapProcessor extends MapInstructions {

  public static class MapProcessorInfo {
    public final MediaType source;

    public final MediaType dest;

    public MapProcessorInfo(MediaType pSource, MediaType pDest) {
      super();
      source = pSource;
      dest = pDest;
    }
  }

  public List<MapProcessorInfo> getSupported();

}
