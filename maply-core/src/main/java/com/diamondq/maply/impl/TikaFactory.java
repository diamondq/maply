package com.diamondq.maply.impl;

import java.io.IOException;

import javax.inject.Singleton;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;

import io.micronaut.context.annotation.Factory;

@Factory
public class TikaFactory {

  @Singleton
  public Tika getTika() {
    try {
      TikaConfig config = new TikaConfig();
      return new Tika(config);
    }
    catch (IOException | TikaException ex) {
      throw new RuntimeException(ex);
    }
  }
}
