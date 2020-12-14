package com.diamondq.maply.impl;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.NamingAdapterService;
import com.diamondq.maply.spi2.NamingAdapter;
import com.diamondq.maply.spi2.NamingAdapter.NamingResult;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NamingAdapterServiceImpl implements NamingAdapterService {

  private final ContextFactory      mContextFactory;

  private final List<NamingAdapter> mAdapters;

  @Inject
  public NamingAdapterServiceImpl(ContextFactory pContextFactory, List<NamingAdapter> pAdapters) {
    mContextFactory = pContextFactory;
    mAdapters = pAdapters;
  }

  /**
   * @see com.diamondq.maply.api2.NamingAdapterService#adaptConstructorParamName(java.lang.reflect.Parameter,
   *      java.lang.String)
   */
  @Override
  public String adaptConstructorParamName(Parameter pParam, String pName) {
    try (Context ctx = mContextFactory.newContext(NamingAdapterServiceImpl.class, this, pParam, pName)) {
      int highestPriority = Integer.MIN_VALUE;
      String highestName = pName;
      for (NamingAdapter na : mAdapters) {
        Optional<NamingResult> result = na.adaptConstructorParamName(pParam, pName);
        if (result.isPresent() == false)
          continue;
        NamingResult namingResult = result.get();
        if (namingResult.priority > highestPriority) {
          highestPriority = namingResult.priority;
          highestName = namingResult.name;
        }
      }
      return ctx.exit(highestName);
    }
  }

}
