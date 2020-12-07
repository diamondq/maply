package com.diamondq.maply.api2;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DataType implements ToStringIndented {
  public static final String    JAVA_CLASS_NS = "https://www.diamondq.com/ns/maply/javaclass";

  public final String           namespace;

  public final String           name;

  public final @Nullable String child;

  public final @Nullable Object data;

  public DataType(String pNamespace, String pName, @Nullable String pChild, @Nullable Object pData) {
    namespace = pNamespace;
    name = pName;
    child = pChild;
    data = pData;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(namespace, name, child, data);
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
    DataType obj = (DataType) pObj;
    return Objects.equals(namespace, obj.namespace) && Objects.equals(name, obj.name)
      && Objects.equals(child, obj.child) && Objects.equals(data, obj.data);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return toStringIndented(new StringBuilder(), "  ", true).toString();
  }

  /**
   * @see com.diamondq.maply.api2.ToStringIndented#toStringIndented(java.lang.StringBuilder, java.lang.String, boolean)
   */
  @Override
  public StringBuilder toStringIndented(StringBuilder pSB, String pIndexStr, boolean pWithIndenting) {
    pSB = pSB.append("{\"DataType").append('@').append(Integer.toHexString(hashCode())).append("\":{");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"name\": \"").append(name).append("\", ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"child\": ");
    if (child == null)
      pSB = pSB.append("null");
    else
      pSB = pSB.append("\"").append(child).append("\"");
    pSB = pSB.append(", ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"namespace\": \"").append(namespace).append("\"}}");
    return pSB;
  }
}
