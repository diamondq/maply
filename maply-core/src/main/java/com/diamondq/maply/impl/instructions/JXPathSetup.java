package com.diamondq.maply.impl.instructions;

import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapObject;
import com.diamondq.maply.spi.ExecutionContext;
import com.diamondq.maply.spi.InstructionSetup;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;

@Singleton
public class JXPathSetup implements InstructionSetup {

  @Inject
  public JXPathSetup(List<NodePointerFactory> pAdditionalFactories) {
    for (NodePointerFactory f : pAdditionalFactories)
      JXPathContextReferenceImpl.addNodePointerFactory(f);
  }

  /**
   * @see com.diamondq.maply.spi.InstructionSetup#apply(com.diamondq.maply.api.MapContext,
   *      com.diamondq.maply.spi.ExecutionContext, java.util.Map)
   */
  @Override
  public void apply(MapContext pContext, ExecutionContext pExecutionContext, Map<String, MapObject> pObjs) {
    JXPathContext jxPathContext = JXPathContext.newContext(pObjs);
    pExecutionContext.set(JXPathConstants.sCONTEXT, jxPathContext);
  }

}
