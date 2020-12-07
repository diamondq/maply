package com.diamondq.maply.impl2;

import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.ToStringIndented;
import com.diamondq.maply.spi2.MapInstructionProvider;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Registration implements ToStringIndented {

  public final List<Location>         provides;

  public final int                    priority;

  public final MapInstructionProvider provider;

  public Registration(List<Location> pProvides, int pPriority, MapInstructionProvider pProvider) {
    provides = ImmutableList.copyOf(pProvides);
    priority = pPriority;
    provider = pProvider;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(provides, priority, provider);
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
    Registration obj = (Registration) pObj;
    return Objects.equals(provides, obj.provides) && Objects.equals(priority, obj.priority)
      && Objects.equals(provider, obj.provider);
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
    pSB = pSB.append("{\"Registration").append('@').append(Integer.toHexString(hashCode())).append("\":{");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"provides\": [");
    boolean isFirst = true;
    for (Location localProvide : provides) {
      if (isFirst == true)
        isFirst = false;
      else {
        pSB = pSB.append(", ");
        if (pWithIndenting)
          pSB = pSB.append("\n").append(pIndexStr).append("  ");
      }
      pSB = localProvide.toStringIndented(pSB, pIndexStr + "    ", pWithIndenting);
    }
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr).append("  ");
    pSB = pSB.append("], ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"priority\": ").append(priority).append(", ");
    if (pWithIndenting)
      pSB = pSB.append("\n").append(pIndexStr);
    pSB = pSB.append("\"instruction\": \"").append(provider).append("\"");
    pSB = pSB.append("}}");
    return pSB;
  }
}
