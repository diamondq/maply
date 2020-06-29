package com.diamondq.maply.spi;

import java.util.Collections;
import java.util.Set;

public abstract class BaseAbstractInstructionParser implements InstructionParser {

  private Set<String> mSupportedTypes;

  public BaseAbstractInstructionParser(String pSupportedType) {
    mSupportedTypes = Collections.singleton(pSupportedType);
  }

  /**
   * @see com.diamondq.maply.spi.InstructionParser#getSupportedTypes()
   */
  @Override
  public Set<String> getSupportedTypes() {
    return mSupportedTypes;
  }

}
