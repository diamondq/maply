package com.diamondq.maply.impl2.jxpath;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.Parser;
import org.apache.commons.jxpath.ri.compiler.Expression;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DQJXPathContextReferenceImpl extends JXPathContextReferenceImpl {

  public DQJXPathContextReferenceImpl(JXPathContext pParentContext, @Nullable Object pContextBean) {
    super(pParentContext, pContextBean);
  }

  public DQJXPathContextReferenceImpl(JXPathContext pParentContext, @Nullable Object pContextBean,
    @Nullable Pointer pContextPointer) {
    super(pParentContext, pContextBean, pContextPointer);
  }

  /**
   * @see org.apache.commons.jxpath.ri.JXPathContextReferenceImpl#compilePath(java.lang.String)
   */
  @Override
  protected CompiledExpression compilePath(String xpath) {
    Expression expr = (Expression) Parser.parseExpression(xpath, getCompiler());

    return new DQJXPathCompiledExpression(xpath, expr);
  }

}
