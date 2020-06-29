package com.diamondq.maply.api;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Holds context information
 */
public interface MapContext {

  public <T> void set(String pKey, @NonNull T pValue);

  public <T> @Nullable T get(String pKey, Class<T> pClass);
}
