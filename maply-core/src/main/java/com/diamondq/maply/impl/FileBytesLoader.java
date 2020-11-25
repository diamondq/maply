package com.diamondq.maply.impl;

import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.spi.old.BytesLoader;
import com.diamondq.maply.spi.old.MapBytesData;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.StringUtils;

@Requires(property = FileBytesLoader.PREFIX + ".enabled", value = StringUtils.TRUE)
@Singleton
public class FileBytesLoader implements BytesLoader {

  private static final Logger      sLogger = LoggerFactory.getLogger(FileBytesLoader.class);

  protected static final String    PREFIX  = "maply.bytesloader.file";

  private static final Set<String> sSUPPORTED_SCHEMES;

  static {
    sSUPPORTED_SCHEMES = ImmutableSet.of("file");
  }

  private final List<Path>                         mRootDirs;

  private final LoadingCache<String, MapBytesData> mCachedFiles;

  private final Tika                               mTika;

  @Inject
  public FileBytesLoader(@Value("${" + PREFIX + ".dirs}") List<String> pDirs,
    @Value("${" + PREFIX + ".max-bytes-to-cache:1000000}") long pMaxBytesToCache, Tika pTika) {
    mTika = pTika;
    ImmutableList.Builder<Path> builder = ImmutableList.builder();
    for (String dir : pDirs) {

      /* A comma is supported as a split character */

      String[] splitArray = dir.split(",");
      for (String split : splitArray) {
        Path path = Paths.get(split);
        builder.add(path.toAbsolutePath());
      }
    }
    mRootDirs = builder.build();

    mCachedFiles = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).maximumWeight(pMaxBytesToCache)
      .<String, MapBytesData> weigher((k, v) -> v.data.length + v.contentType.toString().length() + 1)
      .build(new CacheLoader<String, MapBytesData>() {
        @Override
        public MapBytesData load(String pPath) throws Exception {

          Path path = Paths.get(pPath);

          /* Test the path against each of the root directories */

          Path matched = null;
          for (Path rootDir : mRootDirs) {
            Path resolved = rootDir.resolve(path);
            sLogger.debug("Testing for file {}", resolved);
            if (Files.exists(resolved) == true) {
              if (resolved.startsWith(rootDir) == true) {
                matched = resolved;
                sLogger.debug("Found file {}", resolved);
                break;
              }
            }
          }

          /* If there was no match, then we weren't able to load */

          if (matched == null)
            return MapBytesData.EMPTY_MAPDATA;

          /* Now load the bytes */

          byte[] data;
          try (FileInputStream fis = new FileInputStream(matched.toFile())) {
            try (BufferedInputStream bis = new BufferedInputStream(fis)) {
              try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[32000];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1)
                  baos.write(buffer, 0, bytesRead);
                data = baos.toByteArray();
              }
            }
          }

          /* Now calculate the content type of the file */

          String contentType = mTika.detect(data, matched.getName(matched.getNameCount() - 1).toString());
          return new MapBytesData(pPath, MediaType.parse(contentType), data);
        }
      });
  }

  /**
   * @see com.diamondq.maply.spi.old.BytesLoader#getSupportedSchemes()
   */
  @Override
  public Set<String> getSupportedSchemes() {
    return sSUPPORTED_SCHEMES;
  }

  /**
   * @see com.diamondq.maply.spi.old.BytesLoader#supportsURI(boolean, java.net.URI)
   */
  @Override
  public boolean supportsURI(boolean pIsLoad, URI pURI) {
    return true;
  }

  /**
   * @see com.diamondq.maply.spi.old.BytesLoader#load(com.diamondq.maply.advapi.MapContext, java.net.URI)
   */
  @Override
  public @Nullable MapBytesData load(MapContext pContext, URI pURI) {

    String pathStr = Objects.requireNonNull(pURI.getPath());
    if (pathStr.startsWith("/"))
      pathStr = pathStr.substring(1);
    try {
      MapBytesData result = mCachedFiles.get(pathStr);
      if (result == MapBytesData.EMPTY_MAPDATA)
        return null;
      return result;
    }
    catch (ExecutionException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see com.diamondq.maply.spi.old.BytesLoader#save(com.diamondq.maply.advapi.MapContext, java.net.URI,
   *      com.google.common.net.MediaType, byte[])
   */
  @Override
  public void save(MapContext pContext, URI pTargetURI, MediaType pContentType, byte[] pBytes) {

    /* Find the first path that we can write to */

    String pathStr = Objects.requireNonNull(pTargetURI.getPath());
    if (pathStr.startsWith("/"))
      pathStr = pathStr.substring(1);
    Path path = Paths.get(pathStr);
    Path match = null;
    for (Path rootPath : mRootDirs) {
      Path resolved = rootPath.resolve(path);
      if (Files.isWritable(resolved.getParent()) == true) {

        /* Make sure that the resolved path is inside the root path for security reasons */

        if (resolved.startsWith(rootPath) == true) {
          match = resolved;
          break;
        }
      }
    }
    if (match == null)
      throw new IllegalStateException("Unable to find a place to write based on " + pTargetURI.toASCIIString());

    try (FileOutputStream fos = new FileOutputStream(match.toFile())) {
      fos.write(pBytes);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
