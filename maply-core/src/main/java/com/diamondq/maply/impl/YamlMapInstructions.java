package com.diamondq.maply.impl;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapInstructions;
import com.diamondq.maply.api.MapObject;
import com.diamondq.maply.spi.ExecutionContext;
import com.diamondq.maply.spi.Instruction;
import com.diamondq.maply.spi.InstructionSetup;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public class YamlMapInstructions implements MapInstructions {

  private final List<InstructionSetup> mSetups;

  private final List<Instruction>      mInstructions;

  public YamlMapInstructions(List<InstructionSetup> pSetupList, List<Instruction> pInstructions) {
    mSetups = ImmutableList.copyOf(pSetupList);
    mInstructions = ImmutableList.copyOf(pInstructions);
  }

  /**
   * @see com.diamondq.maply.api.MapInstructions#map(com.diamondq.maply.api.MapContext, java.util.Map)
   */
  @Override
  public void map(MapContext pContext, Map<String, MapObject> pObjects) {

    /* Initialize the execution context */

    ExecutionContext executionContext = new ExecutionContextImpl();
    for (InstructionSetup setup : mSetups)
      setup.apply(pContext, executionContext, pObjects);

    for (Instruction instruction : mInstructions) {
      instruction.execute(pContext, executionContext, pObjects);
    }
  }

}
