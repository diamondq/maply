package com.diamondq.maply.impl.instructions;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapObject;
import com.diamondq.maply.spi.ExecutionContext;
import com.diamondq.maply.spi.Instruction;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

public class JXPathVariableInstruction implements Instruction {

  private final CompiledExpression mSourceExpr;

  private final String             mSourceJXPath;

  private final String             mName;

  public JXPathVariableInstruction(CompiledExpression pSourceExpr, String pSourceJXPath, String pName) {
    mSourceExpr = pSourceExpr;
    mSourceJXPath = pSourceJXPath;
    mName = pName;
  }

  /**
   * @see com.diamondq.maply.spi.Instruction#execute(com.diamondq.maply.api.MapContext,
   *      com.diamondq.maply.spi.ExecutionContext, java.util.Map)
   */
  @Override
  public void execute(MapContext pContext, ExecutionContext pExecutionContext, Map<String, MapObject> pObjects) {
    JXPathContext jxPathContext =
      Objects.requireNonNull(pExecutionContext.get(JXPathConstants.sCONTEXT, JXPathContext.class));

    /* Evaluate the source expression */

    Pointer value = mSourceExpr.getPointer(jxPathContext, mSourceJXPath);
    if (value != null) {
      Object node = value.getNode();

      /* Store the result as a variable */

      jxPathContext.getVariables().declareVariable(mName, node);
    }
  }

}
