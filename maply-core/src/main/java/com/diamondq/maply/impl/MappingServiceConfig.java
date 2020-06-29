package com.diamondq.maply.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties(value = MappingServiceImpl.PREFIX)
public class MappingServiceConfig {

  public Optional<List<String>> initialUris = Optional.empty();

  public List<String>           uris        = new ArrayList<>();
}
