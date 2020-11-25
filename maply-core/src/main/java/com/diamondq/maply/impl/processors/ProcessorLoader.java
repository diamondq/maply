package com.diamondq.maply.impl.processors;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.advapi.MapInstructions;
import com.diamondq.maply.spi.InstructionLoader;
import com.diamondq.maply.spi.MapProcessor;
import com.diamondq.maply.spi.MapProcessor.MapProcessorInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class ProcessorLoader implements InstructionLoader {

  private final ContextFactory                               mContextFactory;

  private final Map<MediaType, Map<MediaType, MapProcessor>> mMapProcessors;

  @Inject
  public ProcessorLoader(ContextFactory pContextFactory, List<MapProcessor> pMapProcessors) {
    mContextFactory = pContextFactory;
    Map<MediaType, ImmutableMap.Builder<MediaType, MapProcessor>> outer = new HashMap<>();
    for (MapProcessor mp : pMapProcessors) {
      for (MapProcessorInfo info : mp.getSupported()) {
        Builder<MediaType, MapProcessor> builder = outer.get(info.source);
        if (builder == null) {
          builder = ImmutableMap.builder();
          outer.put(info.source, builder);
        }
        builder.put(info.dest, mp);
      }
    }

    ImmutableMap.Builder<MediaType, Map<MediaType, MapProcessor>> builder = ImmutableMap.builder();
    for (Map.Entry<MediaType, ImmutableMap.Builder<MediaType, MapProcessor>> pair : outer.entrySet()) {
      builder.put(pair.getKey(), pair.getValue().build());
    }

    mMapProcessors = builder.build();
  }

  /**
   * @see com.diamondq.maply.spi.InstructionLoader#loadInstruction(org.apache.tika.mime.MediaType, java.lang.String,
   *      org.apache.tika.mime.MediaType, java.lang.String, org.apache.tika.mime.MediaType[])
   */
  @Override
  public @Nullable MapInstructions loadInstruction(MediaType pSourceMediaType, @Nullable String pSourceIdentifier,
    MediaType pDestMediaType, @Nullable String pDestIdentifier, @NonNull MediaType[] pWithMediaTypes) {
    try (Context ctx = mContextFactory.newContext(ProcessorLoader.class, this, pSourceMediaType, pSourceIdentifier,
      pDestMediaType, pDestIdentifier, pWithMediaTypes)) {

      Map<MediaType, MapProcessor> map = mMapProcessors.get(pSourceMediaType);
      if (map == null)
        return null;
      MapProcessor processor = map.get(pDestMediaType);
      if (processor == null)
        return null;
      return processor;
    }
  }

}
