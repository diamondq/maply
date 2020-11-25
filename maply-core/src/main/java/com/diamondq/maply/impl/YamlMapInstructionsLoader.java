package com.diamondq.maply.impl;

import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapInstructions;
import com.diamondq.maply.spi.old.Instruction;
import com.diamondq.maply.spi.old.InstructionParser;
import com.diamondq.maply.spi.old.InstructionSetup;
import com.diamondq.maply.spi.old.MapBytesData;
import com.diamondq.maply.spi.old.MapInstructionsLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tika.mime.MediaType;
import org.yaml.snakeyaml.Yaml;

@Singleton
public class YamlMapInstructionsLoader implements MapInstructionsLoader {

  private static final Set<MediaType> sSUPPORTED_MEDIA_TYPES;

  private static final Set<String>    sSUPPORTED_FILE_EXTENSIONS;

  static {
    sSUPPORTED_MEDIA_TYPES = ImmutableSet.of(MediaType.application("x-yaml"));
    sSUPPORTED_FILE_EXTENSIONS = ImmutableSet.<String> builder().add("yaml").add("yml").build();
  }

  private final ConcurrentMap<String, InstructionParser> mParsers;

  @Inject
  public YamlMapInstructionsLoader(List<InstructionParser> pParsers) {
    mParsers = new ConcurrentHashMap<>();
    for (InstructionParser parser : pParsers) {
      for (String type : parser.getSupportedTypes()) {
        int offset = type.indexOf('-');
        String overall;
        if (offset == -1)
          overall = "global";
        else {
          overall = type.substring(0, offset);
          type = type.substring(offset + 1);
        }
        String fullType = overall + "-" + type;
        mParsers.put(fullType, parser);
      }
    }
  }

  /**
   * @see com.diamondq.maply.spi.old.MapInstructionsLoader#getSupportedFileExtensions()
   */
  @Override
  public Set<String> getSupportedFileExtensions() {
    return sSUPPORTED_FILE_EXTENSIONS;
  }

  /**
   * @see com.diamondq.maply.spi.old.MapInstructionsLoader#getSupportedMediaTypes()
   */
  @Override
  public Set<MediaType> getSupportedMediaTypes() {
    return sSUPPORTED_MEDIA_TYPES;
  }

  /**
   * @see com.diamondq.maply.spi.old.MapInstructionsLoader#supportsContent(boolean, org.apache.tika.mime.MediaType)
   */
  @Override
  public boolean supportsContent(boolean pIsLoad, MediaType pContentType) {
    return true;
  }

  /**
   * @see com.diamondq.maply.spi.old.MapInstructionsLoader#load(com.diamondq.maply.advapi.MapContext,
   *      com.diamondq.maply.spi.old.MapBytesData)
   */
  @Override
  public MapInstructions load(MapContext pContext, MapBytesData pBytesData) {
    Yaml yaml = new Yaml();
    try (ByteArrayInputStream bais = new ByteArrayInputStream(pBytesData.data)) {
      Map<String, Object> rootData = yaml.load(bais);
      String overallType = (String) rootData.get("type");
      if (overallType == null)
        throw new IllegalArgumentException(
          "The type must be set at the root of a Yaml instruction file. Found in " + pBytesData.location);
      if ("jxpath".equals(overallType) == false)
        throw new IllegalArgumentException(
          "Only jxpath is supported as a type. Found " + overallType + " in " + pBytesData.location);
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> list = (List<Map<String, Object>>) rootData.get("list");
      if (list == null)
        throw new IllegalArgumentException(
          "There must be a list at the root of the Yaml instruction file. Found in " + pBytesData.location);
      List<Instruction> instrList = new ArrayList<>();
      Map<String, InstructionSetup> setupList = new LinkedHashMap<>();
      int listOffset = 0;
      for (Map<String, Object> map : list) {
        listOffset++;
        /* Is there a type */
        String instructionType = (String) map.get("type");
        if (instructionType == null)
          instructionType = "map";
        String type = overallType + "-" + instructionType;
        InstructionParser parser = mParsers.get(type);
        String location = String.valueOf(listOffset) + " list element";
        if (parser == null) {

          /* See if there is a global entry */

          parser = mParsers.get("global-" + instructionType);
          if (parser == null)
            throw new IllegalArgumentException("Unable to find an InstructionParser for " + instructionType + " at "
              + location + " of " + pBytesData.location);
          mParsers.put(type, parser);
        }
        parser.parseInstruction(overallType, type, pBytesData.location, location, map, instrList, setupList);
      }
      return new YamlMapInstructions(ImmutableList.copyOf(setupList.values()), instrList);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
