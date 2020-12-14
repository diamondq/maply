package com.diamondq.maply.impl2.jxpath;

import org.apache.commons.jxpath.ri.compiler.Expression;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.Step;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DQStep extends Step {

  public DQStep(int pAxis, NodeTest pNodeTest, @NonNull Expression @Nullable [] pPredicates) {
    super(pAxis, pNodeTest, pPredicates);
  }

}
