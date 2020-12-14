package com.diamondq.maply.impl2;

import com.diamondq.maply.spi2.NamingAdapter;

import java.lang.reflect.Parameter;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DiamondQNamingAdapter implements NamingAdapter {

  @Inject
  public DiamondQNamingAdapter() {
  }

  /**
   * @see com.diamondq.maply.spi2.NamingAdapter#adaptConstructorParamName(java.lang.reflect.Parameter, java.lang.String)
   */
  @Override
  public Optional<NamingResult> adaptConstructorParamName(Parameter pParam, String pName) {
    if (pName.startsWith("p") == false)
      return Optional.empty();
    String name = pName.substring(1);
    char firstChar = name.charAt(0);
    if (Character.isUpperCase(firstChar) == false)
      return Optional.empty();
    String updatedName =
      new StringBuilder().append(Character.toLowerCase(firstChar)).append(name.substring(1)).toString();
    return Optional.of(new NamingResult(updatedName, 100));
  }

}
