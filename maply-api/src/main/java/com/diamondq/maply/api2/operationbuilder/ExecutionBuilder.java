package com.diamondq.maply.api2.operationbuilder;

import com.diamondq.maply.api2.MapResult;

public interface ExecutionBuilder {

  public ExecutionBuilder var(String pVarName, Object pValue);

  public ExecutionBuilder obj(Object pObj);

  public MapResult run();

}
