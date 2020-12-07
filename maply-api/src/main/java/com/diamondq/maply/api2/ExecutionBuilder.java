package com.diamondq.maply.api2;

public interface ExecutionBuilder {

  public ExecutionBuilder var(String pVarName, Object pValue);

  public ExecutionBuilder obj(Object pObj);

  public MapResult run();

}
