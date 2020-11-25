package com.diamondq.maply.spi;

import com.diamondq.maply.advapi.MapContext;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A MapContextLoader handles the loading of data from an 'object' to a MapContext
 */
public interface MapContextLoader {

  public void addCacheKey(@Nullable Object pObj, @Nullable Class<?> pAsClass, List<String> pCacheKeyBuilder);

  /**
   * Returns the list of supported classes by this loader. NULL is supported which indicates that it should be called
   * for all contexts with no objects.
   * 
   * @return the list of classes
   */
  public List<@Nullable Class<?>> getSupportedClasses();

  /**
   * Loads the MapContext based on the given object
   * 
   * @param pMapContext the map context
   * @param pObj the given object
   * @param pAsClass the class type this load is running as
   */
  public void loadMapContext(MapContext pMapContext, @Nullable Object pObj, @Nullable Class<?> pAsClass);
}
