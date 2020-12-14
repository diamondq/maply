package com.diamondq.maply.impl2.jxpath;

import java.util.Objects;

import org.apache.commons.jxpath.ri.JXPathCompiledExpression;
import org.apache.commons.jxpath.ri.compiler.Expression;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DQJXPathCompiledExpression extends JXPathCompiledExpression {

  public DQJXPathCompiledExpression(String pXPath, Expression pExpr) {
    super(pXPath, pExpr);
  }

  /**
   * @see org.apache.commons.jxpath.ri.JXPathCompiledExpression#getExpression()
   */
  @Override
  public Expression getExpression() {
    return super.getExpression();
  }

  /**
   * @see org.apache.commons.jxpath.ri.JXPathCompiledExpression#getXPath()
   */
  @Override
  public String getXPath() {
    return super.getXPath();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(getXPath());
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
    DQJXPathCompiledExpression obj = (DQJXPathCompiledExpression) pObj;
    return Objects.equals(getXPath(), obj.getXPath());
  }
}
