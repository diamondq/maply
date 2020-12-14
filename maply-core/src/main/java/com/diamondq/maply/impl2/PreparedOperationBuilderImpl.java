package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.PreparedOperation;
import com.diamondq.maply.api2.operationbuilder.PreparedOperationBuilder;
import com.diamondq.maply.api2.operationbuilder.XPathBuilder;
import com.diamondq.maply.impl2.jxpath.DQJXPathCompiledExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.jxpath.JXPathContext;

public class PreparedOperationBuilderImpl implements PreparedOperationBuilder {

  private final ContextFactory                                            mContextFactory;

  private final Function<PreparedOperationBuilderImpl, PreparedOperation> mOnComplete;

  public final Set<DQJXPathCompiledExpression>                            wants;

  public PreparedOperationBuilderImpl(ContextFactory pContextFactory,
    Function<PreparedOperationBuilderImpl, PreparedOperation> pOnComplete) {
    mContextFactory = pContextFactory;
    mOnComplete = pOnComplete;
    wants = new HashSet<>();
  }

  /**
   * @see com.diamondq.maply.api2.operationbuilder.PreparedOperationBuilder#build()
   */
  @Override
  public PreparedOperation build() {
    try (Context ctx = mContextFactory.newContext(PreparedOperationBuilderImpl.class, this)) {
      return mOnComplete.apply(this);
    }
  }

  /**
   * @see com.diamondq.maply.api2.operationbuilder.PreparedOperationBuilder#want()
   */
  @Override
  public XPathBuilder<PreparedOperationBuilder> want() {
    try (Context ctx = mContextFactory.newContext(PreparedOperationBuilderImpl.class, this)) {
      return new XPathBuilder<>(null, (str) -> {
        DQJXPathCompiledExpression expr = (DQJXPathCompiledExpression) JXPathContext.compile(str);
        DQJXPathCompiledExpression canon =
          new DQJXPathCompiledExpression(expr.getExpression().toString(), expr.getExpression());
        wants.add(canon);
        return this;
      });
    }
  }

  /**
   * @see com.diamondq.maply.api2.operationbuilder.PreparedOperationBuilder#want(java.lang.String)
   */
  @Override
  public PreparedOperationBuilder want(String pXPath) {
    try (Context ctx = mContextFactory.newContext(PreparedOperationBuilderImpl.class, this, pXPath)) {
      DQJXPathCompiledExpression expr = (DQJXPathCompiledExpression) JXPathContext.compile(pXPath);
      DQJXPathCompiledExpression canon =
        new DQJXPathCompiledExpression(expr.getExpression().toString(), expr.getExpression());
      wants.add(canon);
      return this;
    }
  }

}
