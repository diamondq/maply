package com.diamondq.maply.api2;

public interface PreparedMapBuilder {

  public PreparedMap build();

  public PreparedMapBuilder want(LocationBuilder pLocation);

  public PreparedMapBuilder with(LocationBuilder pLocation);

}
