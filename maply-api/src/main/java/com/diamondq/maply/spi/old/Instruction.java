package com.diamondq.maply.spi.old;

import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapObject;

import java.util.Map;

public interface Instruction {

  public void execute(MapContext pContext, ExecutionContext pExecutionContext, Map<String, MapObject> pObjects);

}
