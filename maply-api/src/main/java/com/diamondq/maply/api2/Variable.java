package com.diamondq.maply.api2;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Variable {

  public final String name;

  public Variable(String pName) {
    name = pName;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj)
      return true;
    if (pObj == null)
      return false;
    if (getClass() != pObj.getClass())
      return false;
    Variable obj = (Variable) pObj;
    return Objects.equals(name, obj.name);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return new StringBuilder("<<").append(name).append(">>").toString();
  }
}
