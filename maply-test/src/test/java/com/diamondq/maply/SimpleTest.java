package com.diamondq.maply;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.diamondq.common.injection.InjectionContext;
import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapInstructions;
import com.diamondq.maply.api.MapObject;
import com.diamondq.maply.api.MappingService;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SimpleTest {

  @SuppressWarnings("null")
  protected static @NonNull InjectionContext sAppContext;

  @BeforeAll
  public static void setupApplication() {
    sAppContext = InjectionContext.builder().buildAndStart();
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
    MappingService mappingService = sAppContext.findBean(MappingService.class, null).get();
    MapContext context = mappingService.createContext();
    Map<String, MapObject> sourceMap = mappingService.getInitialMapObjects(context);
    MapObject dest = Objects.requireNonNull(mappingService.loadMapObject(context, URI.create("file:///data/test.pdf")));
    MapObject data = Objects.requireNonNull(mappingService.loadMapObject(context, URI.create("file:///data/data.xml")));
    sourceMap.put("xml", data);

    List<MapInstructions> instructions = mappingService.loadMapInstructions(context, sourceMap, dest);
    assertTrue(instructions.isEmpty() == false);

    mappingService.map(context, sourceMap, dest, instructions);

    mappingService.saveMapObject(context, dest, URI.create("file:///data/output.pdf"));
  }
}
