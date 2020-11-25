package com.diamondq.maply.spi;

import com.diamondq.maply.advapi.MapInstructions;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An instruction loader will attempt to load an instruction for a given source/destination/with variant
 */
public interface InstructionLoader {

  /**
   * Attempt to load an instruction for the given variant
   * 
   * @param pSourceMediaType the source media type
   * @param pSourceIdentifier the optional source identifier
   * @param pDestMediaType the destination media type
   * @param pDestIdentifier the optional destination identifier
   * @param pWithMediaTypes the optional with media types
   * @return the MapInstruction or null if there isn't a match
   */
  public @Nullable MapInstructions loadInstruction(MediaType pSourceMediaType, @Nullable String pSourceIdentifier,
    MediaType pDestMediaType, @Nullable String pDestIdentifier, @NonNull MediaType[] pWithMediaTypes);

}
