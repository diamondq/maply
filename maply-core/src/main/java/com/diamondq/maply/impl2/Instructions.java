package com.diamondq.maply.impl2;

import com.diamondq.maply.api2.DataType;
import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.Variable;
import com.diamondq.maply.spi2.MapInstruction;

import java.util.List;
import java.util.Set;

public class Instructions {
  public static class MapInstructionDetails {
    public final MapInstruction instruction;

    public final List<Location> produces;

    public final List<Location> needs;

    public MapInstructionDetails(MapInstruction pInstruction, List<Location> pProduces, List<Location> pNeeds) {
      instruction = pInstruction;
      produces = pProduces;
      needs = pNeeds;
    }
  }

  public final List<MapInstructionDetails> instructions;

  public final Set<Variable>               variableSet;

  public final Set<DataType>               objSet;

  public Instructions(List<MapInstructionDetails> pInstructions, Set<Variable> pVariableSet, Set<DataType> pObjSet) {
    instructions = pInstructions;
    variableSet = pVariableSet;
    objSet = pObjSet;
  }

}