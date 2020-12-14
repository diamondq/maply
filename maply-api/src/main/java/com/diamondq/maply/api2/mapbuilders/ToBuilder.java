package com.diamondq.maply.api2.mapbuilders;

import java.util.ArrayList;
import java.util.List;

public class ToBuilder {

  public final Class<?>          toClass;

  public final List<FromBuilder> fromBuilders;

  public ToBuilder(Class<?> pClass) {
    toClass = pClass;
    fromBuilders = new ArrayList<>();
  }

  public <T> FromBuilder from(Class<T> pClass) {
    return new FromBuilder(pClass, (fromBuilder) -> {
      fromBuilders.add(fromBuilder);
      return ToBuilder.this;
    });
  }
}
