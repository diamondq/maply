package com.diamondq.maply.spi;

import com.diamondq.maply.api2.ToStringIndented;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ContextBuilders {

  public static final Function<@Nullable Object, @Nullable Object> sNotIndented;

  public static final Function<@Nullable Object, @Nullable Object> sNotIndentedCol;

  public static final Function<@Nullable Object, @Nullable Object> sNotIndentedMapKey;

  static {
    sNotIndented = (s) -> {
      if (s == null)
        return null;
      if (s instanceof ToStringIndented == false)
        throw new IllegalStateException();
      return ((ToStringIndented) s).toStringIndented(new StringBuilder(), "", false).toString();
    };
    sNotIndentedCol = (s) -> {
      if (s == null)
        return null;
      if (s instanceof Collection == false)
        throw new IllegalStateException();
      List<String> result = new ArrayList<>();
      @SuppressWarnings("unchecked")
      Collection<ToStringIndented> list = (Collection<ToStringIndented>) s;
      for (ToStringIndented obj : list) {
        result.add(obj.toStringIndented(new StringBuilder(), "", false).toString());
      }
      return result;
    };
    sNotIndentedMapKey = (s) -> {
      if (s == null)
        return null;
      if (s instanceof Map == false)
        throw new IllegalStateException();
      Map<String, Object> result = new HashMap<>();
      @SuppressWarnings("unchecked")
      Map<ToStringIndented, Object> map = (Map<ToStringIndented, Object>) s;
      for (Map.Entry<ToStringIndented, Object> pair : map.entrySet()) {
        result.put(pair.getKey().toStringIndented(new StringBuilder(), "", false).toString(), pair.getValue());
      }
      return result;
    };
  }
}
