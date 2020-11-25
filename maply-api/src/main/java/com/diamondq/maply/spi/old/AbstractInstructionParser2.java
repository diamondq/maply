package com.diamondq.maply.spi.old;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractInstructionParser2<P1, P2> extends BaseAbstractInstructionParser {

  private final String           mParam1;

  private final String           mParam2;

  private final @Nullable String mSetupKey;

  public AbstractInstructionParser2(String pSupportedType, String pParam1, String pParam2, @Nullable String pSetupKey) {
    super(pSupportedType);
    mParam1 = pParam1;
    mParam2 = pParam2;
    mSetupKey = pSetupKey;
  }

  /**
   * @see com.diamondq.maply.spi.old.InstructionParser#parseInstruction(java.lang.String, java.lang.String,
   *      java.lang.String, java.lang.String, java.util.Map, java.util.List, java.util.Map)
   */
  @Override
  public void parseInstruction(String pGlobalType, String pInstructionType, String pGlobalLocation, String pMapLocation,
    Map<String, Object> pMap, List<Instruction> pInstructions, Map<String, InstructionSetup> pSetups) {
    Object obj1 = pMap.get(mParam1);
    if (obj1 == null)
      throw buildError("Unable to find " + mParam1, pGlobalLocation, pMapLocation);
    Object obj2 = pMap.get(mParam2);
    if (obj2 == null)
      throw buildError("Unable to find " + mParam2, pGlobalLocation, pMapLocation);
    @SuppressWarnings("unchecked")
    P1 param1 = (P1) obj1;
    @SuppressWarnings("unchecked")
    P2 param2 = (P2) obj2;
    process(pGlobalLocation, pMapLocation, param1, param2, pInstructions, pSetups);

    String setupKey = mSetupKey;
    if (setupKey != null) {
      if (pSetups.containsKey(setupKey) == false) {
        InstructionSetup setup = getSetup();
        if (setup != null)
          pSetups.put(setupKey, setup);
      }
    }
  }

  protected @Nullable InstructionSetup getSetup() {
    return null;
  }

  protected IllegalArgumentException buildError(String pMessage, String pGlobalLocation, String pMapLocation) {
    return new IllegalArgumentException(pMessage + " within the " + pMapLocation + " inside " + pGlobalLocation);
  }

  protected abstract void process(String pGlobalLocation, String pMapLocation, P1 pParam1, P2 pParam2,
    List<Instruction> pInstructions, Map<String, InstructionSetup> pSetups);
}
