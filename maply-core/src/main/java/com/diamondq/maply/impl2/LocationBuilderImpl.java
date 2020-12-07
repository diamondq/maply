package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.DataType;
import com.diamondq.maply.api2.LocationBuilder;
import com.diamondq.maply.api2.Variable;
import com.diamondq.maply.api2.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LocationBuilderImpl implements LocationBuilder {

  private final ContextFactory mContextFactory;

  private @Nullable Class<?>   mClass;

  public final List<Where>     wheres;

  private @Nullable DataType   mDataType;

  private @Nullable MediaType  mFormat;

  public LocationBuilderImpl(ContextFactory pContextFactory) {
    mContextFactory = pContextFactory;
    wheres = new ArrayList<>();
  }

  public void verify() {
    try (Context ctx = mContextFactory.newContext(LocationBuilderImpl.class, this)) {
      Class<?> clazz = mClass;
      if (clazz == null)
        throw new IllegalStateException("The class is required");

      /* Build the DataType */

      mDataType = new DataType(DataType.JAVA_CLASS_NS, clazz.getName(), null, clazz);
      mFormat = MediaType.application("javaclass");
    }
  }

  /**
   * Internal method to get the DataType
   * 
   * @return the DataType
   */
  public DataType getDataType() {
    DataType dataType = mDataType;
    if (dataType == null) {
      verify();
      dataType = Objects.requireNonNull(mDataType);
    }
    return dataType;
  }

  public MediaType getFormat() {
    MediaType format = mFormat;
    if (format == null) {
      verify();
      format = Objects.requireNonNull(mFormat);
    }
    return format;
  }

  /**
   * @see com.diamondq.maply.api2.LocationBuilder#whereEq(java.lang.String, com.diamondq.maply.api2.Variable)
   */
  @Override
  public LocationBuilderImpl whereEq(String pProp, Variable pVar) {
    try (Context ctx = mContextFactory.newContext(LocationBuilderImpl.class, this, pProp, pVar)) {
      wheres.add(new Where(false, pProp, pVar));
      return this;
    }
  }

  /**
   * @see com.diamondq.maply.api2.LocationBuilder#whereParentEq(java.lang.String, com.diamondq.maply.api2.Variable)
   */
  @Override
  public LocationBuilder whereParentEq(String pProp, Variable pVar) {
    try (Context ctx = mContextFactory.newContext(LocationBuilderImpl.class, this, pProp, pVar)) {
      wheres.add(new Where(true, pProp, pVar));
      return this;
    }
  }

  /**
   * @see com.diamondq.maply.api2.LocationBuilder#classEq(java.lang.Class)
   */
  @Override
  public LocationBuilderImpl classEq(Class<?> pClass) {
    try (Context ctx = mContextFactory.newContext(LocationBuilderImpl.class, this, pClass)) {
      mClass = pClass;
      return this;
    }
  }

}
