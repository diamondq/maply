package com.diamondq.maply.spi.base;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapObject;
import com.diamondq.maply.api.MediaTypeLookup;

import java.util.Collections;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class SingleConstructorMapProcessor<S, D> extends BaseMapProcessor {

  protected final Class<D> mDestClass;

  public SingleConstructorMapProcessor(ContextFactory pContextFactory, MediaTypeLookup pMediaTypeLookup,
    Class<S> pSourceClass, Class<D> pDestClass) {
    super(pContextFactory, Collections
      .singletonList(new MapProcessorInfo(pMediaTypeLookup.lookup(pSourceClass), pMediaTypeLookup.lookup(pDestClass))));
    mDestClass = pDestClass;
  }

  @Override
  public void map(MapContext pMapContext, MapObject pDestMapObject, MapObject pSrcMapObject,
    @NonNull MapObject @Nullable [] pWithArray) {
    if (pDestMapObject.getValue() != null)
      throw new UnsupportedOperationException("Updating " + mDestClass.getName() + " is not supported");
    @SuppressWarnings("unchecked")
    S src = (S) Objects.requireNonNull(pSrcMapObject.getValue());
    D dst = map(src);
    pDestMapObject.setValue(dst);
  }

  protected abstract @NonNull D map(@NonNull S pSrc);

}
