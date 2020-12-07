package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.DataType;
import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.LocationBuilder;
import com.diamondq.maply.api2.MappingService;
import com.diamondq.maply.api2.PreparedMap;
import com.diamondq.maply.api2.PreparedMapBuilder;
import com.diamondq.maply.api2.Variable;
import com.diamondq.maply.api2.Where;
import com.diamondq.maply.impl2.Instructions.MapInstructionDetails;
import com.diamondq.maply.spi.ContextBuilders;
import com.diamondq.maply.spi2.MapInstructionProvider;
import com.diamondq.maply.spi2.MappingProvider;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.tika.mime.MediaType;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class MappingServiceImpl implements MappingService {

  private final ContextFactory     mContextFactory;

  private final List<Registration> mRegistrations;

  @Inject
  public MappingServiceImpl(ContextFactory pContextFactory, List<MappingProvider> pProviders) {
    mContextFactory = pContextFactory;
    mRegistrations = new CopyOnWriteArrayList<>();
    for (MappingProvider provider : pProviders)
      provider.setup(this);
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#var(java.lang.String)
   */
  @Override
  public Variable var(String pName) {
    return new Variable(pName);
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#preparedMap()
   */
  @Override
  public PreparedMapBuilder preparedMap() {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this)) {
      return new MappingBuilderImpl(mContextFactory, this);
    }
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#location(java.lang.Class)
   */
  @Override
  public LocationBuilder location(Class<?> pClass) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this, pClass)) {
      LocationBuilder builder = new LocationBuilderImpl(mContextFactory);
      builder = builder.classEq(pClass);
      return builder;
    }
  }

  public Location locationFromClass(Class<?> pClass) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this, pClass)) {
      Location result = locationFromLocationBuilder(new LocationBuilderImpl(mContextFactory).classEq(pClass));
      return ctx.exit(result, ContextBuilders.sNotIndented);
    }
  }

  private Location locationFromLocationBuilder(LocationBuilderImpl pBuilder) {
    DataType dataType = pBuilder.getDataType();
    List<Where> wheres = pBuilder.wheres;
    MediaType format = pBuilder.getFormat();
    return new Location(dataType, format, ImmutableList.copyOf(wheres));
  }

  /**
   * This internal method is called by the MappingBuilderImpl when it's being built
   * 
   * @param pMappingBuilderImpl the mapping builder
   * @return the PreparedMap
   */
  public PreparedMap prepare(MappingBuilderImpl pMappingBuilderImpl) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this, pMappingBuilderImpl)) {

      ImmutableList.Builder<Location> lbuilder = ImmutableList.builder();
      for (LocationBuilderImpl lb : pMappingBuilderImpl.wants) {
        lbuilder.add(locationFromLocationBuilder(lb));
      }
      ImmutableList<Location> wantList = lbuilder.build();

      lbuilder = ImmutableList.builder();
      for (LocationBuilderImpl lb : pMappingBuilderImpl.withs) {
        lbuilder.add(locationFromLocationBuilder(lb));
      }
      ImmutableList<Location> withList = lbuilder.build();

      return new PreparedMapImpl(mContextFactory, this, wantList, withList);
    }
  }

  @SuppressWarnings("unused")
  private void recursiveSearch(LocationBuilderImpl pLB) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this, pLB)) {
      DataType dataType = pLB.getDataType();

      /* Search through all the providers to see if anyone can provide the given data type */

    }
  }

  public DataType dataType(Object pObj) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this, pObj)) {
      return locationFromClass(pObj.getClass()).dataType;
    }
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#dataType(java.lang.String, java.lang.String, java.lang.String,
   *      java.lang.Object)
   */
  @Override
  public DataType dataType(String pNamespace, String pName, @Nullable String pChild, @Nullable Object pData) {
    return new DataType(pNamespace, pName, pChild, pData);
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#location(com.diamondq.maply.api2.DataType,
   *      org.apache.tika.mime.MediaType, java.util.List)
   */
  @Override
  public Location location(DataType pDataType, @Nullable MediaType pFormat, List<Where> pWhere) {
    return new Location(pDataType, pFormat, pWhere);
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#register(java.util.List, int,
   *      com.diamondq.maply.spi2.MapInstructionProvider)
   */
  @Override
  public void register(List<Location> pProvides, int pPriority, MapInstructionProvider pProvider) {
    try (Context ctx = mContextFactory.newContextWithMeta(MappingServiceImpl.class, this, pProvides,
      ContextBuilders.sNotIndentedCol, ContextBuilders.sNotIndentedCol, pPriority, null, pProvider, null)) {
      mRegistrations.add(new Registration(pProvides, pPriority, pProvider));
    }
  }

  private static class A {

    public final List<Location>              pendingWants;

    public final List<Location>              withs;

    public final List<MapInstructionDetails> instructions;

    public A(List<Location> pWantList, List<Location> pWithList, List<MapInstructionDetails> pInstructions) {
      pendingWants = new ArrayList<>(pWantList);
      withs = new ArrayList<>(pWithList);
      instructions = new ArrayList<>(pInstructions);
    }

  }

  /**
   * This is the main class that attempts to generate instructions for the given Map with wants and withs. It does not
   * do any caching, as the PreparedMap takes care of that.
   * 
   * @param pPreparedMapImpl the PreparedMap
   * @param pWantList the wants
   * @param pWithList the withs
   * @return the instructions
   */
  public Instructions generateInstructions(PreparedMapImpl pPreparedMapImpl, List<Location> pWantList,
    List<Location> pWithList) {
    try (Context ctx = mContextFactory.newContextWithMeta(MappingServiceImpl.class, this, pPreparedMapImpl, null,
      pWantList, ContextBuilders.sNotIndentedCol, pWithList, ContextBuilders.sNotIndentedCol)) {

      List<MapInstructionDetails> instrs = new ArrayList<>();
      Set<Variable> requiredVariables = new HashSet<>();
      Set<DataType> requiredObjs = new HashSet<>();

      Stack<A> stack = new Stack<>();
      A work = new A(pWantList, pWithList, Collections.emptyList());

      while (work.pendingWants.isEmpty() == false) {

        /* Take the top want */

        Location testWant = work.pendingWants.get(0);

        /* Find any registered that is capable of supply the want */

        int highestPriority = Integer.MIN_VALUE;
        Registration highestReg = null;
        Location highestLoc = null;
        for (Registration reg : mRegistrations) {

          /* Does this registration provide the want? */

          for (Location regWant : reg.provides) {
            Optional<Location> matchOpt = wantMatching(regWant, testWant);
            if (matchOpt.isPresent() == true) {

              /* It does. What 'needs' does it require */

              Location actualWant = matchOpt.get();
              List<Location> actualNeeds = reg.provider.evaluateNeeds(this, actualWant);

              /* Can its needs be met? */

              List<Location> list = new ArrayList<>();
              boolean isFirst = true;
              for (Location l : work.pendingWants) {
                if (isFirst == true)
                  isFirst = false;
                else
                  list.add(l);
              }
              for (Location l : actualNeeds) {
                list.add(0, l);
              }

              A newWork = new A(list, work.withs, work.instructions);
              stack.push(newWork);
              
              // for (Location testNeed : reg.needs) {
              // generateInstr(testNeed, pWithList);
              // }

              if (highestPriority < reg.priority) {
                highestPriority = reg.priority;
                highestReg = reg;
                highestLoc = actualWant;
                break;
              }
            }
          }
        }

        if ((highestReg == null) || (highestLoc == null))
          throw new IllegalStateException("No such path to generate " + testWant.toString() + " with " + pWithList);

      }
      // for (Location want : pWantList) {
      // MapInstructionDetails instr = generateInstr(want, pWithList);
      // instrs.add(instr);
      // }
      return new Instructions(instrs, requiredVariables, requiredObjs);
    }
  }

  private MapInstructionDetails generateInstr(Location pWant, List<Location> pWithList) {
    try (Context ctx = mContextFactory.newContextWithMeta(MappingServiceImpl.class, this, pWant,
      ContextBuilders.sNotIndented, pWithList, ContextBuilders.sNotIndentedCol)) {

      /* Find any registered that is capable of supply the want */

      int highestPriority = Integer.MIN_VALUE;
      Registration highestReg = null;
      Location highestLoc = null;
      for (Registration reg : mRegistrations) {

        /* Does this registration provide the want? */

        for (Location testWant : reg.provides) {
          Optional<Location> matchOpt = wantMatching(testWant, pWant);
          if (matchOpt.isPresent() == true) {

            /* It does. What 'needs' does it require */

            Location actualWant = matchOpt.get();
            List<Location> actualNeeds = reg.provider.evaluateNeeds(this, actualWant);

            /* Can its needs be met? */

            // for (Location testNeed : reg.needs) {
            // generateInstr(testNeed, pWithList);
            // }

            if (highestPriority < reg.priority) {
              highestPriority = reg.priority;
              highestReg = reg;
              highestLoc = actualWant;
              break;
            }
          }
        }
      }

      if ((highestReg == null) || (highestLoc == null))
        throw new IllegalStateException("No such path to generate " + pWant.toString() + " with " + pWithList);

      return new MapInstructionDetails(highestReg.provider, Collections.singletonList(highestLoc),
        Collections.emptyList());
    }
  }

  private Optional<Location> wantMatching(Location pThisWant, Location pThatWant) {
    if (pThisWant == pThatWant)
      return Optional.empty();
    MediaType thisFormat = pThisWant.format;
    MediaType thatFormat = pThatWant.format;
    if (((thisFormat == null) && (thatFormat != null)) || ((thisFormat != null) && (thatFormat == null)))
      return Optional.empty();
    MediaType matchFormat;
    if ((thisFormat != null) && (thatFormat != null)) {
      String thisType = thisFormat.getType();
      String thatType = thatFormat.getType();
      if ((thisType.equals("*") == false) && (thatType.equals("*") == false)) {
        if (thisType.equals(thatType) == false)
          return Optional.empty();
        String thisSubType = thisFormat.getSubtype();
        String thatSubType = thatFormat.getSubtype();
        if ((thisSubType.equals("*") == false) && (thatSubType.equals("*") == false)) {
          if (thisSubType.equals(thatSubType) == false)
            return Optional.empty();
          matchFormat = thisFormat;
        }
        else {
          if (thisSubType.equals("*")) {
            if (thatSubType.equals("*"))
              throw new IllegalStateException(
                "Unable to match against sub-type wildcards in both the 'this' and 'that'");
            matchFormat = thatFormat;
          }
          else
            matchFormat = thisFormat;
        }
      }
      else {
        if (thisType.equals("*")) {
          if (thatType.equals("*"))
            throw new IllegalStateException("Unable to match against type wildcards in both the 'this' and 'that'");
          matchFormat = thatFormat;
        }
        else
          matchFormat = thisFormat;
      }
    }
    else
      matchFormat = null;

    DataType matchDataType;
    if (pThisWant.dataType.namespace.equals(pThatWant.dataType.namespace) == false)
      return Optional.empty();
    if ((pThisWant.dataType.name.equals("*") == false) && (pThatWant.dataType.name.equals("*") == false)) {
      if (pThisWant.dataType.name.equals(pThatWant.dataType.name) == false)
        return Optional.empty();
      if (pThisWant.dataType.name.equals("*"))
        matchDataType = pThatWant.dataType;
      else
        matchDataType = pThisWant.dataType;
    }
    else {
      if (pThisWant.dataType.name.equals("*")) {
        if (pThatWant.dataType.name.equals("*"))
          throw new IllegalStateException(
            "Unable to match against a data type name with wildcards in both the 'this' and 'that'");
        matchDataType = pThatWant.dataType;
      }
      else
        matchDataType = pThisWant.dataType;
    }
    if (("*".equals(pThisWant.dataType.child) == false) && ("*".equals(pThatWant.dataType.child) == false)) {
      if (Objects.equal(pThisWant.dataType.child, pThatWant.dataType.child) == false)
        return Optional.empty();
      if ("*".equals(pThisWant.dataType.child))
        matchDataType =
          new DataType(matchDataType.namespace, matchDataType.name, pThatWant.dataType.child, matchDataType.data);
      else if ("*".equals(pThatWant.dataType.child))
        matchDataType =
          new DataType(matchDataType.namespace, matchDataType.name, pThisWant.dataType.child, matchDataType.data);
    }
    else {
      if ("*".equals(pThisWant.dataType.child)) {
        if ("*".equals(pThatWant.dataType.child))
          throw new IllegalStateException(
            "Unable to match against a data type child with wildcards in both the 'this' and 'that'");
        matchDataType =
          new DataType(matchDataType.namespace, matchDataType.name, pThatWant.dataType.child, matchDataType.data);
      }
      else if ("*".equals(pThatWant.dataType.child))
        matchDataType =
          new DataType(matchDataType.namespace, matchDataType.name, pThisWant.dataType.child, matchDataType.data);
    }

    return Optional.of(new Location(matchDataType, matchFormat, pThisWant.where));
  }

}
