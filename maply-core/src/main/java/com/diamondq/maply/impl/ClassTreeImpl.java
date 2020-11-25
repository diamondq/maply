package com.diamondq.maply.impl;

import com.diamondq.maply.spi.ClassTree;
import com.google.common.collect.ImmutableList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClassTreeImpl implements ClassTree {

  private ConcurrentMap<Class<?>, List<Class<?>>> mCache;

  @Inject
  public ClassTreeImpl() {
    mCache = new ConcurrentHashMap<>();
  }

  private void addClass(Class<?> pClass, ImmutableList.Builder<Class<?>> pListBuilder, Set<Class<?>> pSeenSet) {

    /* If we've already seen this class/interface, then return */

    if (pSeenSet.add(pClass) == false)
      return;

    /* Add self if it's an interface */

    if (pClass.isInterface() == true)
      pListBuilder.add(pClass);

    /* Add interfaces */

    for (Class<?> intf : pClass.getInterfaces()) {
      addClass(intf, pListBuilder, pSeenSet);
    }

    /* Add classes */

    Class<?> superClass = pClass.getSuperclass();
    if ((superClass != null) && (superClass.getName().equals("java.lang.Object") == false)) {
      addClass(superClass, pListBuilder, pSeenSet);
    }

    if (pClass.isInterface() == false)
      pListBuilder.add(pClass);

  }

  /**
   * @see com.diamondq.maply.spi.ClassTree#getTree(java.lang.Class)
   */
  @Override
  public List<Class<?>> getTree(Class<?> pClass) {
    List<Class<?>> list = mCache.get(pClass);
    if (list == null) {
      ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();
      Set<Class<?>> seenSet = new HashSet<>();
      addClass(pClass, builder, seenSet);
      List<Class<?>> newList = builder.build();
      if ((list = mCache.put(pClass, newList)) == null)
        list = newList;
    }

    return list;
  }

}
