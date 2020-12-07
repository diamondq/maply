package com.diamondq.maply.api2;

import com.diamondq.maply.spi2.MapInstructionProvider;

import java.util.List;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Responsible for performing mapping between objects
 */
public interface MappingService {

  public static final MediaType JAVA_CLASS = MediaType.application("javaclass");

  /**
   * Start a new PreparedMap builder
   * 
   * @return the builder
   */
  public PreparedMapBuilder preparedMap();

  /**
   * Start a Location builder based on a Java class
   * 
   * @param pClass the Java class the Location represents
   * @return the builder
   */
  public LocationBuilder location(Class<?> pClass);

  public Variable var(String pName);

  public Location location(DataType pDataType, @Nullable MediaType pFormat, List<Where> pWhere);

  public DataType dataType(String pNamespace, String pName, @Nullable String pChild, @Nullable Object pData);

  public void register(List<Location> pProvides, int pPriority, MapInstructionProvider pProvider);

}
