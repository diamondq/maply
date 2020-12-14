package com.diamondq.maply;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.injection.InjectionContext;

import java.io.IOException;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleTest2 {

  @SuppressWarnings("null")
  protected static @NonNull InjectionContext sAppContext;

  @BeforeAll
  public static void setupApplication() {
    sAppContext = InjectionContext.builder().buildAndStart();
  }

  @SuppressWarnings("null")
  private ContextFactory mContextFactory;

  @BeforeEach
  public void setupInstance() {
    mContextFactory = sAppContext.findBean(ContextFactory.class, null).get();
  }

  @SuppressWarnings("null")
  @AfterAll
  public static void shutdownApplication() {
    if (sAppContext != null) {
      try {
        sAppContext.close();
      }
      catch (final IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Test
  public void test() {
    try (Context ctx = mContextFactory.newContext(SimpleTest2.class, this)) {
      CompiledExpression expression = JXPathContext.compile("JavaClass/UserData[id=$var]/firstName");
      System.out.println(expression);
    }
  }
}
