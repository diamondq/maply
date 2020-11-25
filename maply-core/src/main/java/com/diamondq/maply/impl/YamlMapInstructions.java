package com.diamondq.maply.impl;

import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapInstructions;
import com.diamondq.maply.advapi.MapObject;
import com.diamondq.maply.spi.old.Instruction;
import com.diamondq.maply.spi.old.InstructionSetup;
import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class YamlMapInstructions implements MapInstructions {

  @SuppressWarnings("unused")
  private final List<InstructionSetup> mSetups;

  @SuppressWarnings("unused")
  private final List<Instruction>      mInstructions;

  public YamlMapInstructions(List<InstructionSetup> pSetupList, List<Instruction> pInstructions) {
    mSetups = ImmutableList.copyOf(pSetupList);
    mInstructions = ImmutableList.copyOf(pInstructions);
  }

  /**
   * @see com.diamondq.maply.advapi.MapInstructions#map(com.diamondq.maply.advapi.MapContext,
   *      com.diamondq.maply.advapi.MapObject, com.diamondq.maply.advapi.MapObject,
   *      com.diamondq.maply.advapi.MapObject[])
   */
  @Override
  public void map(MapContext pMapContext, MapObject pDestMapObject, MapObject pSrcMapObject,
    @NonNull MapObject @Nullable [] pWithArray) {

    /* Initialize the execution context */

    // ExecutionContext executionContext = new ExecutionContextImpl();
    // for (InstructionSetup setup : mSetups)
    // setup.apply(pMapContext, executionContext, pObjects);
    //
    // for (Instruction instruction : mInstructions) {
    // instruction.execute(pMapContext, executionContext, pObjects);
    // }
    throw new IllegalStateException();
  }

}
