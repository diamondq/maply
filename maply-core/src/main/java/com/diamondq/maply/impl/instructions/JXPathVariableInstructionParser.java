package com.diamondq.maply.impl.instructions;

import com.diamondq.maply.spi.old.AbstractInstructionParser2;
import com.diamondq.maply.spi.old.Instruction;
import com.diamondq.maply.spi.old.InstructionSetup;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class JXPathVariableInstructionParser extends AbstractInstructionParser2<String, String> {

  private final JXPathSetup mSetup;

  @Inject
  public JXPathVariableInstructionParser(JXPathSetup pSetup) {
    super("jxpath-variable", "source", "name", JXPathConstants.sSETUP_KEY);
    mSetup = pSetup;
  }

  /**
   * @see com.diamondq.maply.spi.old.AbstractInstructionParser2#getSetup()
   */
  @Override
  protected @Nullable InstructionSetup getSetup() {
    return mSetup;
  }

  /**
   * @see com.diamondq.maply.spi.old.AbstractInstructionParser2#process(java.lang.String, java.lang.String,
   *      java.lang.Object, java.lang.Object, java.util.List, java.util.Map)
   */
  @Override
  protected void process(String pGlobalLocation, String pMapLocation, String pSourceJXPath, String pName,
    List<Instruction> pInstructions, Map<String, InstructionSetup> pSetups) {
    CompiledExpression sourceExpr = JXPathContext.compile(pSourceJXPath);
    pInstructions.add(new JXPathVariableInstruction(sourceExpr, pSourceJXPath, pName));
  }
}
