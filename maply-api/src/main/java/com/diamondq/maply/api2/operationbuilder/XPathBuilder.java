package com.diamondq.maply.api2.operationbuilder;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class XPathBuilder<RETURN> {

  private Function<String, RETURN> mOnComplete;

  private StringBuilder            mBuilder;

  private boolean                  inWhere;

  public XPathBuilder(@Nullable String pStartPath, Function<String, RETURN> pOnComplete) {
    mOnComplete = pOnComplete;
    mBuilder = new StringBuilder(pStartPath != null ? pStartPath : "/");
    inWhere = false;
  }

  public XPathBuilder<RETURN> step(Class<?> pClass) {
    mBuilder.append("class:").append(pClass.getName());
    return this;
  }

  private void closeWhereIfNeeded() {
    if (inWhere == true) {
      mBuilder.append(']');
      inWhere = false;
    }
  }

  public XPathBuilder<RETURN> localName(String pName) {
    closeWhereIfNeeded();
    mBuilder.append('/');
    mBuilder.append(pName);
    return this;
  }

  public XPathBuilder<RETURN> whereEq(String pProp, String pVar) {
    if (inWhere == false) {
      mBuilder.append("[");
      inWhere = true;
    }
    else
      mBuilder.append(" and ");
    mBuilder.append(pProp);
    mBuilder.append('=');
    mBuilder.append("$");
    mBuilder.append(pVar);
    return this;
  }

  public RETURN build() {
    closeWhereIfNeeded();
    return mOnComplete.apply(mBuilder.toString());
  }

}
