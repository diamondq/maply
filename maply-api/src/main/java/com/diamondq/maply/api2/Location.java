package com.diamondq.maply.api2;

import java.util.List;
import java.util.Objects;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Location implements ToStringIndented {

  public final DataType            dataType;

  public final @Nullable MediaType format;

  public final List<Where>         where;

  public Location(DataType pDataType, @Nullable MediaType pFormat, List<Where> pWhere) {
    dataType = pDataType;
    format = pFormat;
    where = pWhere;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(dataType, format, where);
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
    Location obj = (Location) pObj;
    return Objects.equals(dataType, obj.dataType) && Objects.equals(format, obj.format)
      && Objects.equals(where, obj.where);
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
    pSB = pSB.append("{\"Location").append('@').append(Integer.toHexString(hashCode())).append("\":{");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"dataType\": ");
    dataType.toStringIndented(pSB, pIndexStr + "  ", pWithIndenting);
    pSB = pSB.append(", ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"format\": ");
    if (format == null)
      pSB = pSB.append("null");
    else
      pSB = pSB.append("\"").append(format).append("\"");
    pSB = pSB.append(", ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"where\": [");
    boolean isFirst = true;
    for (Where localWhere : where) {
      if (isFirst == true)
        isFirst = false;
      else {
        pSB = pSB.append(", ");
        if (pWithIndenting)
          pSB = pSB.append("\n").append(pIndexStr).append("  ");
      }
      pSB = localWhere.toStringIndented(pSB, pIndexStr + "    ", pWithIndenting);
    }
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr).append("  ");
    pSB = pSB.append("]}}");
    return pSB;
  }
}
