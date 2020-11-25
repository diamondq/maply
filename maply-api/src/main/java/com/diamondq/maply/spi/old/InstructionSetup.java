package com.diamondq.maply.spi.old;

import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapObject;

import java.util.Map;

public interface InstructionSetup {
  public void apply(MapContext pContext, ExecutionContext pExecutionContext, Map<String, MapObject> pObjs);
}