package com.diamondq.maply.impl;

import com.diamondq.maply.api.MediaTypeLookup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tika.mime.MediaType;

@Singleton
public class MediaTypeLookupImpl implements MediaTypeLookup {

  private final ConcurrentMap<Class<?>, MediaType> mClassToMediaTypeCache;

  @Inject
  public MediaTypeLookupImpl() {
    mClassToMediaTypeCache = new ConcurrentHashMap<>();
  }

  @Override
  public <D> MediaType lookup(Class<D> pClass) {
    MediaType mediaType = mClassToMediaTypeCache.get(pClass);
    if (mediaType == null) {
      MediaType newMediaType = MediaType.application("x-class-" + pClass.getName());
      if ((mediaType = mClassToMediaTypeCache.putIfAbsent(pClass, newMediaType)) == null)
        mediaType = newMediaType;
    }
    return mediaType;
  }

}
