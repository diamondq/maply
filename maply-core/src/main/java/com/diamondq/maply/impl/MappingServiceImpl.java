package com.diamondq.maply.impl;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapInstructions;
import com.diamondq.maply.api.MapObject;
import com.diamondq.maply.api.MappingService;
import com.diamondq.maply.spi.BytesLoader;
import com.diamondq.maply.spi.MapBytesData;
import com.diamondq.maply.spi.MapInstructionsLoader;
import com.diamondq.maply.spi.MapObjectContentLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class MappingServiceImpl implements MappingService {

  protected static final String                           PREFIX = "maply.instructions";

  private static final Object                             sMAP   = "map";

  private final Map<String, List<BytesLoader>>            mBytesLoaders;

  private final Map<String, List<MapObjectContentLoader>> mMapContentLoaders;

  private final Map<String, List<MapInstructionsLoader>>  mMapInstructionLoaders;

  private final List<URI>                                 mInstructionURIs;

  private final List<URI>                                 mInitialURIs;

  private final Set<String>                               mSupportedFileExtensions;

  @Inject
  public MappingServiceImpl(List<BytesLoader> pByteLoaders, List<MapObjectContentLoader> pContentLoaders,
    List<MapInstructionsLoader> pInstructionLoaders, MappingServiceConfig pConfig) {

    ImmutableList.Builder<URI> uriBuilder = ImmutableList.builder();
    for (String uriStr : pConfig.uris) {
      String[] splitList = uriStr.split(",");
      for (String split : splitList) {
        try {
          URI uri = new URI(split);
          uriBuilder.add(uri);
        }
        catch (URISyntaxException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
    mInstructionURIs = uriBuilder.build();

    uriBuilder = ImmutableList.builder();
    if (pConfig.initialUris.isPresent() == true)
      for (String uriStr : pConfig.initialUris.get()) {
        String[] splitList = uriStr.split(",");
        for (String split : splitList) {
          try {
            URI uri = new URI(split);
            uriBuilder.add(uri);
          }
          catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
          }
        }
      }
    mInitialURIs = uriBuilder.build();

    /* Take the list of MapObjectLoader's and make a Map keyed of the supported scheme's */

    Map<String, ImmutableList.Builder<BytesLoader>> tempMap = new HashMap<>();
    for (BytesLoader bl : pByteLoaders) {
      for (String scheme : bl.getSupportedSchemes()) {
        ImmutableList.Builder<BytesLoader> list = tempMap.get(scheme);
        if (list == null) {
          list = ImmutableList.builder();
          tempMap.put(scheme, list);
        }
        list.add(bl);
      }
    }
    ImmutableMap.Builder<String, List<BytesLoader>> builder = ImmutableMap.builder();
    for (Map.Entry<String, ImmutableList.Builder<BytesLoader>> pair : tempMap.entrySet())
      builder.put(pair.getKey(), pair.getValue().build());
    mBytesLoaders = builder.build();

    /* Take the list of MapObjectLoader's and make a Map keyed of the supported scheme's */

    Map<String, ImmutableList.Builder<MapObjectContentLoader>> tempContentMap = new HashMap<>();
    for (MapObjectContentLoader mocl : pContentLoaders) {
      for (MediaType mediaType : mocl.getSupportedMediaTypes()) {
        String plainMediaType = mediaType.withoutParameters().toString();
        ImmutableList.Builder<MapObjectContentLoader> list = tempContentMap.get(plainMediaType);
        if (list == null) {
          list = ImmutableList.builder();
          tempContentMap.put(plainMediaType, list);
        }
        list.add(mocl);
      }
    }
    ImmutableMap.Builder<String, List<MapObjectContentLoader>> contentBuilder = ImmutableMap.builder();
    for (Map.Entry<String, ImmutableList.Builder<MapObjectContentLoader>> pair : tempContentMap.entrySet())
      contentBuilder.put(pair.getKey(), pair.getValue().build());
    mMapContentLoaders = contentBuilder.build();

    Map<String, ImmutableList.Builder<MapInstructionsLoader>> tempInstructionMap = new HashMap<>();
    ImmutableSet.Builder<String> tempExtensions = ImmutableSet.builder();
    for (MapInstructionsLoader mil : pInstructionLoaders) {
      tempExtensions.addAll(mil.getSupportedFileExtensions());
      for (MediaType mediaType : mil.getSupportedMediaTypes()) {
        String plainMediaType = mediaType.withoutParameters().toString();
        ImmutableList.Builder<MapInstructionsLoader> list = tempInstructionMap.get(plainMediaType);
        if (list == null) {
          list = ImmutableList.builder();
          tempInstructionMap.put(plainMediaType, list);
        }
        list.add(mil);
      }
    }
    ImmutableMap.Builder<String, List<MapInstructionsLoader>> instructionsBuilder = ImmutableMap.builder();
    for (Map.Entry<String, ImmutableList.Builder<MapInstructionsLoader>> pair : tempInstructionMap.entrySet())
      instructionsBuilder.put(pair.getKey(), pair.getValue().build());
    mMapInstructionLoaders = instructionsBuilder.build();
    mSupportedFileExtensions = tempExtensions.build();
  }

  /**
   * @see com.diamondq.maply.api.MappingService#createContext()
   */
  @Override
  public MapContext createContext() {
    return new MapContextImpl();
  }

  private @Nullable MapBytesData loadBytes(MapContext pContext, URI pURI) {

    /* Use the scheme of the URI to determine the loader */

    String scheme = pURI.getScheme();
    List<BytesLoader> bytesList = mBytesLoaders.get(scheme);
    if (bytesList == null)
      throw new IllegalArgumentException("There is no BytesLoader that supports " + scheme);
    BytesLoader bytesMatch = null;
    for (BytesLoader bl : bytesList) {
      if (bl.supportsURI(true, pURI) == true) {
        bytesMatch = bl;
        break;
      }
    }
    if (bytesMatch == null)
      throw new IllegalArgumentException("There is no BytesLoader that supports " + pURI.toASCIIString());

    /* Now get the loader to load the bytes */

    return bytesMatch.load(pContext, pURI);
  }

  /**
   * @see com.diamondq.maply.api.MappingService#loadMapObject(com.diamondq.maply.api.MapContext, java.net.URI)
   */
  @Override
  public @Nullable MapObject loadMapObject(MapContext pContext, URI pURI) {

    MapBytesData loadedData = loadBytes(pContext, pURI);
    if (loadedData == null)
      return null;

    /* Find something that'll load the content */

    List<MapObjectContentLoader> contentList =
      mMapContentLoaders.get(loadedData.contentType.withoutParameters().toString());
    if (contentList == null)
      throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + loadedData.contentType);
    MapObjectContentLoader contentMatch = null;
    for (MapObjectContentLoader mocl : contentList) {
      if (mocl.supportsContent(true, loadedData.contentType) == true) {
        contentMatch = mocl;
        break;
      }
    }
    if (contentMatch == null)
      throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + loadedData.contentType);

    String uriPath = pURI.getPath();
    String identifier;
    if (uriPath == null)
      identifier = Objects.requireNonNull(pURI.getScheme());
    else {
      int lastOffset = uriPath.lastIndexOf('/');
      if (lastOffset != -1)
        identifier = uriPath.substring(lastOffset + 1);
      else
        identifier = uriPath;
    }
    return contentMatch.load(pContext, pURI, identifier, loadedData);
  }

  /**
   * @see com.diamondq.maply.api.MappingService#saveMapObject(com.diamondq.maply.api.MapContext,
   *      com.diamondq.maply.api.MapObject, java.net.URI)
   */
  @Override
  public void saveMapObject(MapContext pContext, MapObject pSourceObject, URI pTargetURI) {

    MediaType contentType = pSourceObject.getMediaType();

    /* Find something that'll load the content */

    List<MapObjectContentLoader> contentList = mMapContentLoaders.get(contentType.withoutParameters().toString());
    if (contentList == null)
      throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + contentType);
    MapObjectContentLoader contentMatch = null;
    for (MapObjectContentLoader mocl : contentList) {
      if (mocl.supportsContent(false, contentType) == true) {
        contentMatch = mocl;
        break;
      }
    }
    if (contentMatch == null)
      throw new IllegalArgumentException("There is no MapObjectContentLoader that supports " + contentType);

    byte[] bytes = contentMatch.save(pContext, contentType, pSourceObject);

    /* Use the scheme of the URI to determine the loader */

    String scheme = pTargetURI.getScheme();
    List<BytesLoader> bytesList = mBytesLoaders.get(scheme);
    if (bytesList == null)
      throw new IllegalArgumentException("There is no BytesLoader that supports " + scheme);
    BytesLoader bytesMatch = null;
    for (BytesLoader bl : bytesList) {
      if (bl.supportsURI(false, pTargetURI) == true) {
        bytesMatch = bl;
        break;
      }
    }
    if (bytesMatch == null)
      throw new IllegalArgumentException("There is no BytesLoader that supports " + pTargetURI.toASCIIString());

    bytesMatch.save(pContext, pTargetURI, contentType, bytes);
  }

  /**
   * @see com.diamondq.maply.api.MappingService#loadMapInstructions(com.diamondq.maply.api.MapContext, java.util.Map,
   *      com.diamondq.maply.api.MapObject)
   */
  @Override
  public List<MapInstructions> loadMapInstructions(MapContext pContext, Map<String, MapObject> pSourceObjects,
    MapObject pDest) {

    /* Look for the different combinations of mapping from the different map objects to the primary */

    String destLabel = "dest";
    String destContentType = pDest.getMediaType().withoutParameters().toString();
    String destIdentifier = pDest.getIdentifier();
    String[] destVariants = new String[] {
        //
        new StringBuilder().append("/id/").append(destIdentifier).toString(),
        //
        new StringBuilder().append("/id/").append(destIdentifier).append("/l/").append(destLabel).toString(),
        //
        new StringBuilder().append("/id/").append(destIdentifier).append("/l/").append(destLabel).append("/ct/")
          .append(destContentType).toString(),
        //
        new StringBuilder().append("/id/").append(destIdentifier).append("/ct/").append(destContentType).toString(),
        //
        new StringBuilder().append("/l/").append(destLabel).append("/ct/").append(destContentType).toString(),
        //
        new StringBuilder().append("/ct/").append(destContentType).toString(),
        //
        new StringBuilder().append("/l/").append(destLabel).toString()};
    List<MapInstructions> results = new ArrayList<>();
    for (Map.Entry<String, MapObject> source : pSourceObjects.entrySet()) {
      MapObject mo = source.getValue();
      String sourceLabel = source.getKey();
      String sourceContentType = mo.getMediaType().withoutParameters().toString();
      String sourceIdentifier = mo.getIdentifier();

      String[] sourceVariants = new String[] {
          //
          new StringBuilder().append("id/").append(sourceIdentifier).toString(),
          //
          new StringBuilder().append("id/").append(sourceIdentifier).append("/l/").append(sourceLabel).toString(),
          //
          new StringBuilder().append("id/").append(sourceIdentifier).append("/l/").append(sourceLabel).append("/ct/")
            .append(sourceContentType).toString(),
          //
          new StringBuilder().append("id/").append(sourceIdentifier).append("/ct/").append(sourceContentType)
            .toString(),
          //
          new StringBuilder().append("/l/").append(sourceLabel).append("ct/").append(sourceContentType).toString(),
          //
          new StringBuilder().append("ct/").append(sourceContentType).toString(),
          //
          new StringBuilder().append("l/").append(sourceLabel).toString()};

      for (String destVariant : sourceVariants)
        for (String sourceVariant : destVariants)
          for (String extension : mSupportedFileExtensions) {
            String variant = new StringBuilder().append(destVariant).append("/to").append(sourceVariant).append("/")
              .append(sMAP).append('.').append(extension).toString();

            MapBytesData matchBytes = null;
            URI matchURI = null;
            for (URI rootURI : mInstructionURIs) {
              URI resolved = rootURI.resolve(variant);
              MapBytesData resolvedBytes = loadBytes(pContext, resolved);
              if (resolvedBytes != null) {
                matchBytes = resolvedBytes;
                matchURI = resolved;
                break;
              }
            }
            if ((matchBytes == null) || (matchURI == null))
              continue;

            /* Does this variant exist? */

            /* Find something that'll load the content */

            List<MapInstructionsLoader> instructionsList =
              mMapInstructionLoaders.get(matchBytes.contentType.withoutParameters().toString());
            if (instructionsList == null)
              throw new IllegalArgumentException(
                "There is no MapInstructionsLoader that supports " + matchBytes.contentType + " for " + matchURI);
            MapInstructionsLoader instructionMatch = null;
            for (MapInstructionsLoader mil : instructionsList) {
              if (mil.supportsContent(true, matchBytes.contentType) == true) {
                instructionMatch = mil;
                break;
              }
            }
            if (instructionMatch == null)
              throw new IllegalArgumentException(
                "There is no MapInstructionsLoader that supports " + matchBytes.contentType + " for " + matchURI);

            MapInstructions instructions = instructionMatch.load(pContext, matchBytes);
            results.add(instructions);

          }

    }

    return results;
  }

  /**
   * @see com.diamondq.maply.api.MappingService#map(com.diamondq.maply.api.MapContext, java.util.Map,
   *      com.diamondq.maply.api.MapObject, java.util.List)
   */
  @Override
  public void map(MapContext pContext, Map<String, MapObject> pSourceObjects, MapObject pDest,
    List<MapInstructions> pInstructions) {

    Map<String, MapObject> allObjects = new HashMap<>(pSourceObjects);
    allObjects.put("dest", pDest);

    /* Iterate through each of the instructions */

    for (MapInstructions instructions : pInstructions) {
      instructions.map(pContext, allObjects);
    }

  }

  /**
   * @see com.diamondq.maply.api.MappingService#getInitialMapObjects(com.diamondq.maply.api.MapContext)
   */
  @Override
  public Map<String, MapObject> getInitialMapObjects(MapContext pContext) {
    Map<String, MapObject> results = new HashMap<>();
    for (URI initialURI : mInitialURIs) {
      MapObject authMapObject = Objects.requireNonNull(loadMapObject(pContext, initialURI));
      results.put(Objects.requireNonNull(initialURI.getScheme()), authMapObject);
    }
    return results;
  }

}
