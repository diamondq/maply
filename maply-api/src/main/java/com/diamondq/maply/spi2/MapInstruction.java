package com.diamondq.maply.spi2;

import com.diamondq.maply.api2.DataType;
import com.diamondq.maply.api2.Location;

import java.util.List;
import java.util.Map;

public interface MapInstruction {

  public Map<Location, Object> execute(Map<Location, Object> pData, Map<String, Object> pVars,
    Map<DataType, Object> pObjs, List<Location> pWants, List<Location> pWiths);
}
