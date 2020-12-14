package com.diamondq.maply.api2.mapbuilders;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class FromBuilder {

  public final Class<?>                          fromClass;

  public final Set<String>                       equalChildren;

  private final Function<FromBuilder, ToBuilder> mOnComplete;

  public <F> FromBuilder(Class<F> pClass, Function<FromBuilder, ToBuilder> pOnComplete) {
    fromClass = pClass;
    equalChildren = new HashSet<>();
    mOnComplete = pOnComplete;
  }

  public FromBuilder eq(String pName) {
    equalChildren.add(pName);
    return this;
  }

  public ToBuilder build() {
    return mOnComplete.apply(this);
  }
}
