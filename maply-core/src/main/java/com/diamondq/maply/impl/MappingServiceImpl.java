package com.diamondq.maply.impl;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.advapi.AdvancedMappingService;
import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapInstructions;
import com.diamondq.maply.advapi.MapObject;
import com.diamondq.maply.api.MediaTypeLookup;
import com.diamondq.maply.spi.ClassTree;
import com.diamondq.maply.spi.InstructionLoader;
import com.diamondq.maply.spi.MapContextLoader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class MappingServiceImpl implements AdvancedMappingService {

  private static class NullClass {

  }

  protected static final String                          PREFIX = "maply.instructions";

  // private static final Object sMAP = "map";

  private final ContextFactory                           mContextFactory;

  // private final Map<String, List<BytesLoader>> mBytesLoaders;
  //
  // private final Map<String, List<MapObjectContentLoader>> mMapContentLoaders;
  //
  // private final Map<String, List<MapInstructionsLoader>> mMapInstructionLoaders;

  private final List<InstructionLoader>                  mInstructionLoaders;

  // private final List<URI> mInstructionURIs;
  //
  // private final List<URI> mInitialURIs;
  //
  // private final Set<String> mSupportedFileExtensions;

  private final ListMultimap<Class<?>, MapContextLoader> mMapContextLoaderMap;

  private final ClassTree                                mClassTree;

  private final Cache<String, MapContext>                mMapContextCache;

  private final MediaTypeLookup                          mMediaTypeLookup;

  @Inject
  public MappingServiceImpl(ContextFactory pContextFactory,
    // List<BytesLoader> pByteLoaders,
    // List<MapObjectContentLoader> pContentLoaders, List<MapInstructionsLoader> pInstructionLoaders,
    // MappingServiceConfig pConfig
    List<InstructionLoader> pInstructionLoaders, List<MapContextLoader> pMapContextLoaders, ClassTree pClassTree,
    MediaTypeLookup pMediaTypeLookup) {

    mContextFactory = pContextFactory;
    mInstructionLoaders = pInstructionLoaders;
    mClassTree = pClassTree;
    mMediaTypeLookup = pMediaTypeLookup;
    mMapContextCache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build();

    ImmutableListMultimap.Builder<Class<?>, MapContextLoader> mclBuilder = ImmutableListMultimap.builder();
    for (MapContextLoader mcl : pMapContextLoaders) {
      List<@Nullable Class<?>> supportedClasses = mcl.getSupportedClasses();
      for (@Nullable
      Class<?> clazz : supportedClasses) {
        if (clazz == null)
          clazz = NullClass.class;
        mclBuilder.put(clazz, mcl);
      }
    }
    mMapContextLoaderMap = mclBuilder.build();

    // ImmutableList.Builder<URI> uriBuilder = ImmutableList.builder();
    // for (String uriStr : pConfig.uris) {
    // String[] splitList = uriStr.split(",");
    // for (String split : splitList) {
    // try {
    // URI uri = new URI(split);
    // uriBuilder.add(uri);
    // }
    // catch (URISyntaxException ex) {
    // throw new RuntimeException(ex);
    // }
    // }
    // }
    // mInstructionURIs = uriBuilder.build();
    //
    // uriBuilder = ImmutableList.builder();
    // if (pConfig.initialUris.isPresent() == true)
    // for (String uriStr : pConfig.initialUris.get()) {
    // String[] splitList = uriStr.split(",");
    // for (String split : splitList) {
    // try {
    // URI uri = new URI(split);
    // uriBuilder.add(uri);
    // }
    // catch (URISyntaxException ex) {
    // throw new RuntimeException(ex);
    // }
    // }
    // }
    // mInitialURIs = uriBuilder.build();
    //
    // /* Take the list of MapObjectLoader's and make a Map keyed of the supported scheme's */
    //
    // Map<String, ImmutableList.Builder<BytesLoader>> tempMap = new HashMap<>();
    // for (BytesLoader bl : pByteLoaders) {
    // for (String scheme : bl.getSupportedSchemes()) {
    // ImmutableList.Builder<BytesLoader> list = tempMap.get(scheme);
    // if (list == null) {
    // list = ImmutableList.builder();
    // tempMap.put(scheme, list);
    // }
    // list.add(bl);
    // }
    // }
    // ImmutableMap.Builder<String, List<BytesLoader>> builder = ImmutableMap.builder();
    // for (Map.Entry<String, ImmutableList.Builder<BytesLoader>> pair : tempMap.entrySet())
    // builder.put(pair.getKey(), pair.getValue().build());
    // mBytesLoaders = builder.build();
    //
    // /* Take the list of MapObjectLoader's and make a Map keyed of the supported scheme's */
    //
    // Map<String, ImmutableList.Builder<MapObjectContentLoader>> tempContentMap = new HashMap<>();
    // for (MapObjectContentLoader mocl : pContentLoaders) {
    // for (MediaType mediaType : mocl.getSupportedMediaTypes()) {
    // String plainMediaType = mediaType.withoutParameters().toString();
    // ImmutableList.Builder<MapObjectContentLoader> list = tempContentMap.get(plainMediaType);
    // if (list == null) {
    // list = ImmutableList.builder();
    // tempContentMap.put(plainMediaType, list);
    // }
    // list.add(mocl);
    // }
    // }
    // ImmutableMap.Builder<String, List<MapObjectContentLoader>> contentBuilder = ImmutableMap.builder();
    // for (Map.Entry<String, ImmutableList.Builder<MapObjectContentLoader>> pair : tempContentMap.entrySet())
    // contentBuilder.put(pair.getKey(), pair.getValue().build());
    // mMapContentLoaders = contentBuilder.build();
    //
    // Map<String, ImmutableList.Builder<MapInstructionsLoader>> tempInstructionMap = new HashMap<>();
    // ImmutableSet.Builder<String> tempExtensions = ImmutableSet.builder();
    // for (MapInstructionsLoader mil : pInstructionLoaders) {
    // tempExtensions.addAll(mil.getSupportedFileExtensions());
    // for (MediaType mediaType : mil.getSupportedMediaTypes()) {
    // String plainMediaType = mediaType.withoutParameters().toString();
    // ImmutableList.Builder<MapInstructionsLoader> list = tempInstructionMap.get(plainMediaType);
    // if (list == null) {
    // list = ImmutableList.builder();
    // tempInstructionMap.put(plainMediaType, list);
    // }
    // list.add(mil);
    // }
    // }
    // ImmutableMap.Builder<String, List<MapInstructionsLoader>> instructionsBuilder = ImmutableMap.builder();
    // for (Map.Entry<String, ImmutableList.Builder<MapInstructionsLoader>> pair : tempInstructionMap.entrySet())
    // instructionsBuilder.put(pair.getKey(), pair.getValue().build());
    // mMapInstructionLoaders = instructionsBuilder.build();
    // mSupportedFileExtensions = tempExtensions.build();
  }

  /**
   * @see com.diamondq.maply.advapi.AdvancedMappingService#createEmptyMapObject(org.apache.tika.mime.MediaType,
   *      java.lang.String)
   */
  @Override
  public MapObject createEmptyMapObject(MediaType pDestMediaType, @Nullable String pIdentifier) {
    return new MapObjectImpl(null, pDestMediaType, pIdentifier);
  }

  /**
   * @see com.diamondq.maply.advapi.AdvancedMappingService#createMapObject(java.lang.Object, java.lang.String)
   */
  @Override
  public <T> MapObject createMapObject(@NonNull T pValue, @Nullable String pIdentifier) {
    return new MapObjectImpl(pValue, mMediaTypeLookup.lookup(pValue.getClass()), pIdentifier);
  }

  /**
   * @see com.diamondq.maply.advapi.AdvancedMappingService#getOrCreateContext(java.lang.Object[])
   */
  @Override
  public MapContext getOrCreateContext(@NonNull Object @Nullable... pObjects) {
    List<MapContextLoader> emptyList = mMapContextLoaderMap.get(NullClass.class);

    /* First, generate the cache key */

    List<String> keyBuilder = new ArrayList<>();
    if (emptyList != null)
      for (MapContextLoader mcl : emptyList)
        mcl.addCacheKey(null, null, keyBuilder);

    if (pObjects != null)
      for (Object obj : pObjects) {
        for (Class<?> clazz : mClassTree.getTree(obj.getClass())) {
          List<MapContextLoader> clist = mMapContextLoaderMap.get(clazz);
          if (clist != null)
            for (MapContextLoader mcl : clist)
              mcl.addCacheKey(obj, clazz, keyBuilder);
        }
      }

    Collections.sort(keyBuilder);
    StringBuilder sb = new StringBuilder();
    for (String k : keyBuilder)
      sb.append('/').append(k);
    String cacheKey = sb.toString();

    /* Now look up the cache */

    MapContext mapContext = mMapContextCache.getIfPresent(cacheKey);
    if (mapContext == null) {

      mapContext = new MapContextImpl();
      if (emptyList != null)
        for (MapContextLoader mcl : emptyList)
          mcl.loadMapContext(mapContext, null, null);

      if (pObjects != null)
        for (Object obj : pObjects) {
          for (Class<?> clazz : mClassTree.getTree(obj.getClass())) {
            List<MapContextLoader> clist = mMapContextLoaderMap.get(clazz);
            if (clist != null)
              for (MapContextLoader mcl : clist)
                mcl.loadMapContext(mapContext, obj, clazz);
          }
        }

      mMapContextCache.put(cacheKey, mapContext);
    }

    return mapContext;
  }

  //
  // private @Nullable MapBytesData loadBytes(MapContext pContext, URI pURI) {
  //
  // /* Use the scheme of the URI to determine the loader */
  //
  // String scheme = pURI.getScheme();
  // List<BytesLoader> bytesList = mBytesLoaders.get(scheme);
  // if (bytesList == null)
  // throw new IllegalArgumentException("There is no BytesLoader that supports " + scheme);
  // BytesLoader bytesMatch = null;
  // for (BytesLoader bl : bytesList) {
  // if (bl.supportsURI(true, pURI) == true) {
  // bytesMatch = bl;
  // break;
  // }
  // }
  // if (bytesMatch == null)
  // throw new IllegalArgumentException("There is no BytesLoader that supports " + pURI.toASCIIString());
  //
  // /* Now get the loader to load the bytes */
  //
  // return bytesMatch.load(pContext, pURI);
  // }
  //
  // /**
  // * @see com.diamondq.maply.api.MappingService#loadMapObject(com.diamondq.maply.advapi.MapContext, java.net.URI)
  // */
  // @Override
  // public @Nullable MapObject loadMapObject(MapContext pContext, URI pURI) {
  //
  // MapBytesData loadedData = loadBytes(pContext, pURI);
  // if (loadedData == null)
  // return null;
  //
  // /* Find something that'll load the content */
  //
  // List<MapObjectContentLoader> contentList =
  // mMapContentLoaders.get(loadedData.contentType.withoutParameters().toString());
  // if (contentList == null)
  // throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + loadedData.contentType);
  // MapObjectContentLoader contentMatch = null;
  // for (MapObjectContentLoader mocl : contentList) {
  // if (mocl.supportsContent(true, loadedData.contentType) == true) {
  // contentMatch = mocl;
  // break;
  // }
  // }
  // if (contentMatch == null)
  // throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + loadedData.contentType);
  //
  // String uriPath = pURI.getPath();
  // String identifier;
  // if (uriPath == null)
  // identifier = Objects.requireNonNull(pURI.getScheme());
  // else {
  // int lastOffset = uriPath.lastIndexOf('/');
  // if (lastOffset != -1)
  // identifier = uriPath.substring(lastOffset + 1);
  // else
  // identifier = uriPath;
  // }
  // return contentMatch.load(pContext, pURI, identifier, loadedData);
  // }
  //
  // /**
  // * @see com.diamondq.maply.api.MappingService#saveMapObject(com.diamondq.maply.advapi.MapContext,
  // * com.diamondq.maply.advapi.MapObject, java.net.URI)
  // */
  // @Override
  // public void saveMapObject(MapContext pContext, MapObject pSourceObject, URI pTargetURI) {
  //
  // MediaType contentType = pSourceObject.getMediaType();
  //
  // /* Find something that'll load the content */
  //
  // List<MapObjectContentLoader> contentList = mMapContentLoaders.get(contentType.withoutParameters().toString());
  // if (contentList == null)
  // throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + contentType);
  // MapObjectContentLoader contentMatch = null;
  // for (MapObjectContentLoader mocl : contentList) {
  // if (mocl.supportsContent(false, contentType) == true) {
  // contentMatch = mocl;
  // break;
  // }
  // }
  // if (contentMatch == null)
  // throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + contentType);
  //
  // byte[] bytes = contentMatch.save(pContext, contentType, pSourceObject);
  //
  // /* Use the scheme of the URI to determine the loader */
  //
  // String scheme = pTargetURI.getScheme();
  // List<BytesLoader> bytesList = mBytesLoaders.get(scheme);
  // if (bytesList == null)
  // throw new IllegalArgumentException("There is no BytesLoader that supports " + scheme);
  // BytesLoader bytesMatch = null;
  // for (BytesLoader bl : bytesList) {
  // if (bl.supportsURI(false, pTargetURI) == true) {
  // bytesMatch = bl;
  // break;
  // }
  // }
  // if (bytesMatch == null)
  // throw new IllegalArgumentException("There is no BytesLoader that supports " + pTargetURI.toASCIIString());
  //
  // bytesMatch.save(pContext, pTargetURI, contentType, bytes);
  // }

  /**
   * @see com.diamondq.maply.advapi.AdvancedMappingService#loadMapInstructions(com.diamondq.maply.advapi.MapContext,
   *      com.diamondq.maply.advapi.MapObject, com.diamondq.maply.advapi.MapObject,
   *      com.diamondq.maply.advapi.MapObject[])
   */
  @Override
  public List<MapInstructions> loadMapInstructions(MapContext pContext, MapObject pDest, MapObject pSource,
    @NonNull MapObject @Nullable... pWith) {

    /* Check the cache */

    List<MapInstructions> results = new ArrayList<>();

    MapObject[] with = pWith;
    MediaType[] withMediaTypes;
    if (with != null) {
      int withLen = with.length;
      withMediaTypes = new MediaType[withLen];
      if (withLen > 0)
        for (int i = 0; i < withLen; i++)
          withMediaTypes[i] = with[i].getMediaType();
    }
    else {
      withMediaTypes = new MediaType[0];
    }

    /* First pass is to see if there is a direct map between source and dest */

    MapInstructions instruction = null;
    for (InstructionLoader loader : mInstructionLoaders) {
      instruction = loader.loadInstruction(pSource.getMediaType(), pSource.getIdentifier(), pDest.getMediaType(),
        pDest.getIdentifier(), withMediaTypes);
      if (instruction != null) {
        break;
      }
    }

    if (instruction != null) {
      results.add(instruction);
      return results;
    }

    /* There isn't a direct map. Attempt to find a distant path */

    throw new IllegalStateException(
      "Unable to find mapping instructions from " + pSource.toString() + " to " + pDest.toString());
  }
  //
  // for (String extension : mSupportedFileExtensions) {
  // String variant = new StringBuilder().append(destVariant).append("/to").append(sourceVariant).append("/")
  // .append(sMAP).append('.').append(extension).toString();
  //
  // MapBytesData matchBytes = null;
  // URI matchURI = null;
  // for (URI rootURI : mInstructionURIs) {
  // URI resolved = rootURI.resolve(variant);
  // MapBytesData resolvedBytes = loadBytes(pContext, resolved);
  // if (resolvedBytes != null) {
  // matchBytes = resolvedBytes;
  // matchURI = resolved;
  // break;
  // }
  // }
  // if ((matchBytes == null) || (matchURI == null))
  // continue;
  //
  // /* Does this variant exist? */
  //
  // /* Find something that'll load the content */
  //
  // List<MapInstructionsLoader> instructionsList =
  // mMapInstructionLoaders.get(matchBytes.contentType.withoutParameters().toString());
  // if (instructionsList == null)
  // throw new IllegalArgumentException(
  // "There is no MapInstructionsLoader that supports " + matchBytes.contentType + " for " + matchURI);
  // MapInstructionsLoader instructionMatch = null;
  // for (MapInstructionsLoader mil : instructionsList) {
  // if (mil.supportsContent(true, matchBytes.contentType) == true) {
  // instructionMatch = mil;
  // break;
  // }
  // }
  // if (instructionMatch == null)
  // throw new IllegalArgumentException(
  // "There is no MapInstructionsLoader that supports " + matchBytes.contentType + " for " + matchURI);
  //
  // MapInstructions instructions = instructionMatch.load(pContext, matchBytes);
  // results.add(instructions);
  //
  // }

  /**
   * @see com.diamondq.maply.api.MappingService#map(org.apache.tika.mime.MediaType, java.lang.String, java.lang.Object,
   *      java.lang.Object[])
   */
  @Override
  public <S, D> D map(MediaType pDestMediaType, @Nullable String pSourceIdentifier, @NonNull S pSource,
    @NonNull Object @Nullable... pWith) {

    /* Get the MapContext */

    MapContext mapContext = getOrCreateContext(pWith);

    /* Convert all to MapObjects */

    MapObject destMapObject = createEmptyMapObject(pDestMediaType, null);
    MapObject srcMapObject = createMapObject(pSource, pSourceIdentifier);
    @NonNull
    MapObject[] withArray;
    @NonNull
    Object[] localWith = pWith;
    int withLen;
    if (localWith != null) {
      withLen = localWith.length;
      if (withLen == 0)
        withArray = null;
      else {
        @SuppressWarnings("null")
        @NonNull
        MapObject[] tempArray = new MapObject[withLen];
        withArray = tempArray;
        for (int i = 0; i < withLen; i++) {
          withArray[i] = createMapObject(localWith[i], null);
        }
      }
    }
    else {
      withLen = 0;
      withArray = null;
    }

    /* Next get the instructions */

    List<MapInstructions> mapInstructions = loadMapInstructions(mapContext, destMapObject, srcMapObject, withArray);

    /* Now perform the maps */

    for (MapInstructions instr : mapInstructions) {
      instr.map(mapContext, destMapObject, srcMapObject, withArray);
    }

    /* Return the result */

    @SuppressWarnings("unchecked")
    D result = (D) Objects.requireNonNull(destMapObject.getValue());
    return result;
  }

  /**
   * @see com.diamondq.maply.api.MappingService#getMappingFunction(java.lang.Class, java.lang.String, java.lang.Class,
   *      java.lang.Object[])
   */
  @Override
  public <S, D> Function<@NonNull S, @NonNull D> getMappingFunction(Class<D> pDestClass,
    @Nullable String pSourceIdentifier, Class<S> pSourceClass, @NonNull Object @Nullable... pWith) {
    return getMappingFunction(mMediaTypeLookup.lookup(pDestClass), pSourceIdentifier,
      mMediaTypeLookup.lookup(pSourceClass), pWith);
  }

  /**
   * @see com.diamondq.maply.api.MappingService#getMappingFunction(org.apache.tika.mime.MediaType, java.lang.String,
   *      org.apache.tika.mime.MediaType, java.lang.Object[])
   */
  @Override
  public <S, D> Function<@NonNull S, @NonNull D> getMappingFunction(MediaType pDestMediaType,
    @Nullable String pSourceIdentifier, MediaType pSourceMediaType, @NonNull Object @Nullable... pWith) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this, pDestMediaType, pSourceIdentifier,
      pSourceMediaType, pWith)) {

      /* Get the MapContext */

      MapContext mapContext = getOrCreateContext(pWith);

      /* Convert all to MapObjects */

      MapObject destMapObject = createEmptyMapObject(pDestMediaType, null);
      MapObject srcEmptyMapObject = createEmptyMapObject(pSourceMediaType, pSourceIdentifier);
      @NonNull
      MapObject[] withArray;
      @NonNull
      Object[] localWith = pWith;
      int withLen;
      if (localWith != null) {
        withLen = localWith.length;
        if (withLen == 0)
          withArray = null;
        else {
          @SuppressWarnings("null")
          @NonNull
          MapObject[] tempArray = new MapObject[withLen];
          withArray = tempArray;
          for (int i = 0; i < withLen; i++) {
            withArray[i] = createMapObject(localWith[i], null);
          }
        }
      }
      else {
        withLen = 0;
        withArray = null;
      }

      /* Next get the instructions */

      List<MapInstructions> mapInstructions =
        loadMapInstructions(mapContext, destMapObject, srcEmptyMapObject, withArray);

      /* Return a function */

      return (s) -> {
        try (Context ctx2 = mContextFactory.newContext(MappingServiceImpl.class, this, s, pSourceMediaType, pDestMediaType)) {
          
          MapObject localDestMapObject = destMapObject.copy();
          MapObject srcMapObject = createMapObject(s, pSourceIdentifier);

          /* Now perform the maps */

          for (MapInstructions instr : mapInstructions) {
            instr.map(mapContext, localDestMapObject, srcMapObject, withArray);
          }

          /* Return the result */

          @SuppressWarnings("unchecked")
          D result = (D) Objects.requireNonNull(localDestMapObject.getValue());
          return result;
        }
      };
    }
  }
}
