package com.diamondq.maply.impl;

import com.diamondq.maply.spi.ExecutionContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ExecutionContextImpl implements ExecutionContext {
  private final ConcurrentMap<String, Object> mData;

  public ExecutionContextImpl() {
    mData = new ConcurrentHashMap<>();
  }

  /**
   * @see com.diamondq.maply.api.MapContext#set(java.lang.String, java.lang.Object)
   */
  @Override
  public <T> void set(String pKey, @NonNull T pValue) {
    mData.put(pKey, pValue);
  }

  /**
   * @see com.diamondq.maply.api.MapContext#get(java.lang.String, java.lang.Class)
   */
  @Override
  public <T> @Nullable T get(String pKey, Class<T> pClass) {
    Object value = mData.get(pKey);
    if (value == null)
      return null;
    if (pClass.isInstance(value) == false)
      throw new IllegalArgumentException("The object at key " + pKey + " is a " + value.getClass().getName()
        + " but is expected to be a " + pClass.getName());

    @SuppressWarnings("unchecked")
    T result = (T) value;
    return result;
  }

}
