package com.diamondq.maply.spi;

import java.util.List;

public interface ClassTree {

  public List<Class<?>> getTree(Class<?> pClass);
  
}
