package com.diamondq.maply.advapi;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Holds context information
 */
public interface MapContext {

  // public <T> void set(String pKey, @NonNull T pValue);

  /**
   * @param <T>
   * @param pKey
   * @param pClass
   * @return the value
   */
  public <T> @Nullable T get(String pKey, Class<T> pClass);
}
