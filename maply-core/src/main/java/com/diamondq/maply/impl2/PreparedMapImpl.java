package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.DataType;
import com.diamondq.maply.api2.ExecutionBuilder;
import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.MapResult;
import com.diamondq.maply.api2.PreparedMap;
import com.diamondq.maply.api2.Variable;
import com.diamondq.maply.impl2.Instructions.MapInstructionDetails;
import com.diamondq.maply.spi.ContextBuilders;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparedMapImpl implements PreparedMap {

  private final ContextFactory     mContextFactory;

  private final MappingServiceImpl mMappingService;

  private final List<Location>     mWantList;

  private final List<Location>     mWithList;

  public PreparedMapImpl(ContextFactory pContextFactory, MappingServiceImpl pMappingService, List<Location> pWantList,
    List<Location> pWithList) {
    mContextFactory = pContextFactory;
    mMappingService = pMappingService;
    mWantList = ImmutableList.copyOf(pWantList);
    mWithList = ImmutableList.copyOf(pWithList);
  }

  /**
   * @see com.diamondq.maply.api2.PreparedMap#begin()
   */
  @Override
  public ExecutionBuilder begin() {
    try (Context ctx = mContextFactory.newContext(PreparedMapImpl.class, this)) {
      return new ExecutionBuilderImpl(mContextFactory, mMappingService, this);
    }
  }

  public MapResult execute(Map<String, Object> pVars, Map<DataType, Object> pObjs) {
    try (Context ctx = mContextFactory.newContextWithMeta(PreparedMapImpl.class, this, pVars, null, pObjs,
      ContextBuilders.sNotIndentedMapKey)) {

      /* Generate the instruction list (if we haven't already) */

      Instructions instructions = generateInstructions();

      /* Match the required variables and required DataTypes to the provided data */

      Map<String, Object> variables = new HashMap<>();
      for (Variable var : instructions.variableSet) {
        Object obj = pVars.get(var.name);
        if (obj == null)
          throw new IllegalStateException("Unable to find the required variable " + var.name);
        variables.put(var.name, obj);
      }

      Map<DataType, Object> dataTypes = new HashMap<>();
      for (DataType dt : instructions.objSet) {
        Object obj = pObjs.get(dt);
        if (obj == null)
          throw new IllegalStateException("Unable to find the required variable " + dt.toString());
        dataTypes.put(dt, obj);
      }

      /* Now execute the instructions */

      Map<Location, Object> data = new HashMap<>();
      for (MapInstructionDetails details : instructions.instructions) {
        data = details.instruction.execute(data, variables, dataTypes, details.produces, details.needs);
      }

      return new MapResultImpl(mContextFactory, mMappingService, data);
    }
  }

  private Instructions generateInstructions() {
    try (Context ctx = mContextFactory.newContext(PreparedMapImpl.class, this)) {
      synchronized (this) {
        return mMappingService.generateInstructions(this, mWantList, mWithList);
      }
    }
  }

}
