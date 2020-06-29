package com.diamondq.maply.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InstructionParser {

  /**
   * The set of supported types. If there is a hyphen in the type, then the first part indicates the overall type. The
   * second (or only) is the type of the individual instruction.
   * 
   * @return the set of supported types
   */
  public Set<String> getSupportedTypes();

  /**
   * Parses a given instruction
   * 
   * @param pGlobalType the global type
   * @param pInstructionType the specific instruction type
   * @param pGlobalLocation the location of the file containing the instructions
   * @param pMapLocation the specific location within the file for this specific instruction
   * @param pMap the map representing the instruction data
   * @param pInstructions the instructions list. New instructions can be added to the list
   * @param pSetups the list of setups to perform. New setups can be added to the list
   */
  public void parseInstruction(String pGlobalType, String pInstructionType, String pGlobalLocation, String pMapLocation,
    Map<String, Object> pMap, List<Instruction> pInstructions, Map<String, InstructionSetup> pSetups);
}
