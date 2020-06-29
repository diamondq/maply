package com.diamondq.maply.impl.instructions;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapObject;
import com.diamondq.maply.spi.ExecutionContext;
import com.diamondq.maply.spi.Instruction;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;

public class JXPathMapInstruction implements Instruction {

  private final CompiledExpression mSourceExpr;

  private final CompiledExpression mTargetExpr;

  public JXPathMapInstruction(CompiledExpression pSourceExpr, CompiledExpression pTargetExpr) {
    mSourceExpr = pSourceExpr;
    mTargetExpr = pTargetExpr;
  }

  /**
   * @see com.diamondq.maply.spi.Instruction#execute(com.diamondq.maply.api.MapContext,
   *      com.diamondq.maply.spi.ExecutionContext, java.util.Map)
   */
  @Override
  public void execute(MapContext pContext, ExecutionContext pExecutionContext, Map<String, MapObject> pObjects) {
    JXPathContext jxPathContext =
      Objects.requireNonNull(pExecutionContext.get(JXPathConstants.sCONTEXT, JXPathContext.class));

    /* Evaluate the source to a value */

    Object value = mSourceExpr.getValue(jxPathContext);

    /* Store the value into the target */

    mTargetExpr.setValue(jxPathContext, value);
  }
}
