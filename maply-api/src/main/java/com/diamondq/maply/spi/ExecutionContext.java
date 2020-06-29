package com.diamondq.maply.spi;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ExecutionContext {

  public <T> void set(String pKey, @NonNull T pValue);

  public <T> @Nullable T get(String pKey, Class<T> pClass);
}
