package com.diamondq.maply.api2;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Where implements ToStringIndented {

  public final boolean  parent;

  public final String   propName;

  public final Variable var;

  public Where(boolean pParent, String pPropName, Variable pVar) {
    parent = pParent;
    propName = pPropName;
    var = pVar;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(parent, propName, var);
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
    Where obj = (Where) pObj;
    return Objects.equals(parent, obj.parent) && Objects.equals(propName, obj.propName) && Objects.equals(var, obj.var);
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
    pSB = pSB.append("{\"Where").append('@').append(Integer.toHexString(hashCode())).append("\":{");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"parent\":").append(parent).append(", ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"propName\": \"").append(propName).append("\", ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"var\": \"").append(var.toString()).append("\"}}");
    return pSB;
  }
}
