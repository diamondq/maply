package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.DataType;
import com.diamondq.maply.api2.MapResult;
import com.diamondq.maply.api2.operationbuilder.ExecutionBuilder;

import java.util.HashMap;
import java.util.Map;

public class ExecutionBuilderImpl implements ExecutionBuilder {

  private final ContextFactory        mContextFactory;

  private final MappingServiceImpl    mMappingService;

  private final PreparedOperationImpl mPreparedMap;

  private final Map<String, Object>   mVars;

  private final Map<DataType, Object> mObjs;

  public ExecutionBuilderImpl(ContextFactory pContextFactory, MappingServiceImpl pMappingService,
    PreparedOperationImpl pPreparedMap) {
    mContextFactory = pContextFactory;
    mMappingService = pMappingService;
    mPreparedMap = pPreparedMap;
    mVars = new HashMap<>();
    mObjs = new HashMap<>();
  }

  /**
   * @see com.diamondq.maply.api2.operationbuilder.ExecutionBuilder#var(java.lang.String, java.lang.Object)
   */
  @Override
  public ExecutionBuilder var(String pVarName, Object pValue) {
    try (Context ctx = mContextFactory.newContext(ExecutionBuilderImpl.class, this, pVarName, pValue)) {
      mVars.put(pVarName, pValue);
    }
    return this;
  }

  /**
   * @see com.diamondq.maply.api2.operationbuilder.ExecutionBuilder#obj(java.lang.Object)
   */
  @Override
  public ExecutionBuilder obj(Object pObj) {
    try (Context ctx = mContextFactory.newContext(ExecutionBuilderImpl.class, this, pObj)) {
      // DataType dataType = mMappingService.dataType(pObj);
      // mObjs.put(dataType, pObj);
      return this;
    }
  }

  /**
   * @see com.diamondq.maply.api2.operationbuilder.ExecutionBuilder#run()
   */
  @Override
  public MapResult run() {
    try (Context ctx = mContextFactory.newContext(ExecutionBuilderImpl.class, this)) {
      return mPreparedMap.execute(mVars, mObjs);
    }
  }

}
