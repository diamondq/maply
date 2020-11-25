package com.diamondq.maply.api;

import java.util.function.Function;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Responsible for performing mapping between objects
 */
public interface MappingService {

  /**
   * Performs a mapping from source to destination
   * 
   * @param <S> the source type
   * @param <D> the destination type
   * @param pDestMediaType the destination media type
   * @param pSourceIdentifier the optional source identifier
   * @param pSource the source
   * @param pWith any optional extra data
   * @return the destination object
   */
  public <S, D> D map(MediaType pDestMediaType, @Nullable String pSourceIdentifier, @NonNull S pSource,
    @NonNull Object @Nullable... pWith);

  /**
   * Gets a function that will map between <S> and <D>
   * 
   * @param <S> the source type
   * @param <D> the destination type
   * @param pDestMediaType the destination media type
   * @param pSourceIdentifier the optional source identifier
   * @param pSourceMediaType the source media type
   * @param pWith any additional sources
   * @return the mapping function
   */
  public <S, D> Function<@NonNull S, @NonNull D> getMappingFunction(MediaType pDestMediaType,
    @Nullable String pSourceIdentifier, MediaType pSourceMediaType, @NonNull Object @Nullable... pWith);

  /**
   * Gets a function that will map between <S> and <D>
   * 
   * @param <S> the source type
   * @param <D> the destination type
   * @param pDestClass the destination class
   * @param pSourceIdentifier the optional source identifier
   * @param pSourceClass the source class
   * @param pWith any additional sources
   * @return the mapping function
   */
  public <S, D> Function<@NonNull S, @NonNull D> getMappingFunction(Class<D> pDestClass,
    @Nullable String pSourceIdentifier, Class<S> pSourceClass, @NonNull Object @Nullable... pWith);

}
