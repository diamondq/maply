package com.diamondq.maply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.injection.InjectionContext;
import com.diamondq.maply.api2.BaseObjectToObjectMapping;
import com.diamondq.maply.api2.MappingService;
import com.diamondq.maply.api2.PreparedOperation;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleTest {

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

  @Singleton
  public static class TestMap extends BaseObjectToObjectMapping {

    @Inject
    public TestMap() {
      super(to(SimpleData.class) //
        /* Simple map from SimpleInfo to SimpleData */
        .from(SimpleInfo.class) //
        .eq("id") //
        .eq("firstName") //
        .eq("lastName") //
        .build()
      //
      );
    }
  }

  @Test
  public void test() {
    try (Context ctx = mContextFactory.newContext(SimpleTest.class, this)) {
      MappingService ms = sAppContext.findBean(MappingService.class, null).get();

      /* Build an operation */

      PreparedOperation operationTemplate =
        ms.preparedOperation().want().step(SimpleData.class).whereEq("id", "vid").build().build();

      /* Execute a map */

      SimpleInfo info = new SimpleInfo("mike", "Mike", "Mansell");
      SimpleData data = operationTemplate.begin().var("vid", "mike").obj(info).run().get(SimpleData.class);

      /* Verify */

      assertNotNull(data);
      assertEquals("Mike", data.firstName);
      assertEquals("Mansell", data.firstName);

      //
      // mappingService.map(MediaType.PDF, null, URI.create("file:///data/data.xml"),
      // URI.create("file:///data/test.pdf"));
      // MapObject dest = Objects.requireNonNull(mappingService.loadMapObject(context,
      // URI.create("file:///data/test.pdf")));
      // MapObject data = Objects.requireNonNull(mappingService.loadMapObject(context,
      // URI.create("file:///data/data.xml")));
      // sourceMap.put("xml", data);
      //
      // List<MapInstructions> instructions = mappingService.loadMapInstructions(context, sourceMap, dest);
      // assertTrue(instructions.isEmpty() == false);
      //
      // mappingService.map(context, sourceMap, dest, instructions);
      //
      // mappingService.saveMapObject(context, dest, URI.create("file:///data/output.pdf"));
    }
  }
}
