package com.diamondq.maply.spi;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapObject;

import java.util.Map;

public interface InstructionSetup {
  public void apply(MapContext pContext, ExecutionContext pExecutionContext, Map<String, MapObject> pObjs);
}