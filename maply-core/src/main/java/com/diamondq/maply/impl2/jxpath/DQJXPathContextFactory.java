package com.diamondq.maply.impl2.jxpath;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DQJXPathContextFactory extends JXPathContextFactory {

  /**
   * Create a new JXPathContextFactoryReferenceImpl.
   */
  public DQJXPathContextFactory() {
  }

  /**
   * @see org.apache.commons.jxpath.JXPathContextFactory#newContext(org.apache.commons.jxpath.JXPathContext,
   *      java.lang.Object)
   */
  @Override
  public JXPathContext newContext(JXPathContext parentContext, @Nullable Object contextBean) {
    return new DQJXPathContextReferenceImpl(parentContext, contextBean);
  }
}
