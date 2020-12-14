package com.diamondq.maply.impl2;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.maply.api2.Location;
import com.diamondq.maply.api2.MappingService;
import com.diamondq.maply.api2.PreparedOperation;
import com.diamondq.maply.api2.ToStringIndented;
import com.diamondq.maply.api2.operationbuilder.PreparedOperationBuilder;
import com.diamondq.maply.api2.operationbuilder.XPathBuilder;
import com.diamondq.maply.impl2.jxpath.DQJXPathCompiledExpression;
import com.diamondq.maply.impl2.jxpath.LocationImpl;
import com.diamondq.maply.spi.ContextBuilders;
import com.diamondq.maply.spi2.MapInstruction;
import com.diamondq.maply.spi2.MapInstructionProvider;
import com.diamondq.maply.spi2.MapInstructionProvider.NeedsResult;
import com.diamondq.maply.spi2.MappingProvider;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.math3.util.Combinations;
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
   * @see com.diamondq.maply.api2.MappingService#location()
   */
  @Override
  public XPathBuilder<Location> location() {
    return new XPathBuilder<Location>(null, (xpath) -> {
      return new LocationImpl((DQJXPathCompiledExpression) JXPathContext.compile(xpath));
    });
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#location(java.lang.String)
   */
  @Override
  public XPathBuilder<Location> location(String pPath) {
    return new XPathBuilder<Location>(pPath, (xpath) -> {
      return new LocationImpl((DQJXPathCompiledExpression) JXPathContext.compile(xpath));
    });
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#preparedOperation()
   */
  @Override
  public PreparedOperationBuilder preparedOperation() {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this)) {
      return new PreparedOperationBuilderImpl(mContextFactory, this::prepare);
    }
  }

  /**
   * This internal method is called by the MappingBuilderImpl when it's being built
   * 
   * @param pMappingBuilderImpl the mapping builder
   * @return the PreparedMap
   */
  private PreparedOperation prepare(PreparedOperationBuilderImpl pMappingBuilderImpl) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this, pMappingBuilderImpl)) {

      ImmutableSet.Builder<Location> lbuilder = ImmutableSet.builder();
      for (DQJXPathCompiledExpression expr : pMappingBuilderImpl.wants) {
        lbuilder.add(new LocationImpl(expr));
      }
      ImmutableSet<Location> wantList = lbuilder.build();

      // lbuilder = ImmutableSet.builder();
      // for (LocationBuilderImpl lb : pMappingBuilderImpl.withs) {
      // lbuilder.add(locationFromLocationBuilder(lb));
      // }
      // ImmutableList<Location> withList = lbuilder.build();

      return new PreparedOperationImpl(mContextFactory, this, wantList);
    }
  }

  /**
   * @see com.diamondq.maply.api2.MappingService#register(java.util.Set, int,
   *      com.diamondq.maply.spi2.MapInstructionProvider)
   */
  @Override
  public void register(Set<Location> pProvides, int pPriority, MapInstructionProvider pProvider) {
    try (Context ctx = mContextFactory.newContextWithMeta(MappingServiceImpl.class, this, pProvides,
      ContextBuilders.sNotIndentedCol, pPriority, null, pProvider, null)) {
      mRegistrations.add(new Registration(pProvides, pPriority, pProvider));
    }
  }

  public static class GenerateInfo {

    public final Set<Location> seenWantList;

    public final Set<Location> wantList;

    public final String        debugName;

    public GenerateInfo(Set<Location> pSeenWantList, Set<Location> pWantList, String pDebugName) {
      super();
      seenWantList = pSeenWantList;
      wantList = pWantList;
      debugName = pDebugName;
    }

  }

  public static class InstructionStage implements ToStringIndented {
    public final String                    name;

    public final @Nullable MapInstruction  mapInstruction;

    public final List<PossibleInstruction> nextInstructions;

    public InstructionStage(String pName, @Nullable MapInstruction pMapInstruction,
      List<PossibleInstruction> pNextInstructions) {
      name = pName;
      mapInstruction = pMapInstruction;
      nextInstructions = pNextInstructions;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return toStringIndented(new StringBuilder(), "  ", true).toString();
    }

    /**
     * @see com.diamondq.maply.api2.ToStringIndented#toStringIndented(java.lang.StringBuilder, java.lang.String,
     *      boolean)
     */
    @Override
    public StringBuilder toStringIndented(StringBuilder pSB, String pIndexStr, boolean pWithIndenting) {
      pSB = pSB.append("{\"name\": \"").append(name).append("\", ");
      if (pWithIndenting)
        pSB = pSB.append("\n").append(pIndexStr);
      pSB = pSB.append("\"next\": [");
      for (PossibleInstruction pi : nextInstructions)
        pSB = pi.toStringIndented(pSB, pIndexStr + "  ", pWithIndenting);
      pSB = pSB.append("]}");
      return pSB;
    }

  }

  public static class PossibleInstruction implements ToStringIndented {
    public final int                    priority;

    public final List<InstructionStage> instructions;

    public PossibleInstruction(int pPriority, List<InstructionStage> pInstructions) {
      priority = pPriority;
      instructions = pInstructions;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return toStringIndented(new StringBuilder(), "  ", true).toString();
    }

    /**
     * @see com.diamondq.maply.api2.ToStringIndented#toStringIndented(java.lang.StringBuilder, java.lang.String,
     *      boolean)
     */
    @Override
    public StringBuilder toStringIndented(StringBuilder pSB, String pIndexStr, boolean pWithIndenting) {
      pSB = pSB.append("{\"priority\": \"").append(priority).append("\", ");
      if (pWithIndenting)
        pSB = pSB.append("\n").append(pIndexStr);
      pSB = pSB.append("\"possible\": [");
      for (InstructionStage i : instructions)
        pSB = i.toStringIndented(pSB, pIndexStr + "  ", pWithIndenting);
      pSB = pSB.append("]}");
      return pSB;
    }

  }

  /**
   * This is the main class that attempts to generate instructions for the given Map with wants and withs. It does not
   * do any caching, as the PreparedMap takes care of that.
   * 
   * @param pPreparedMapImpl the PreparedMap
   * @param pWantList the wants
   * @return the instructions
   */
  public Instructions generateInstructions(PreparedOperationImpl pPreparedMapImpl, Set<Location> pWantList
  // , List<Location> pWithList
  ) {
    try (Context ctx = mContextFactory.newContextWithMeta(MappingServiceImpl.class, this, pPreparedMapImpl, null,
      pWantList, ContextBuilders.sNotIndentedCol)) {

      Set<Location> seenWantList = new HashSet<>();
      seenWantList.addAll(pWantList);
      List<PossibleInstruction> results = recursiveGenerate(new GenerateInfo(seenWantList, pWantList, ""));
      ctx.debug("Results: {}", results);

    }

    throw new IllegalStateException();
    // return new Instructions(instrs, requiredObjs);
  }

  private List<Set<Location>> permutations(Set<Location> pWants) {
    List<Set<Location>> results = new ArrayList<>();
    int size = pWants.size();
    for (int i = size; i > 0; i--) {
      Combinations combinations = new Combinations(size, i);
      for (int[] entry : combinations) {
        int offset = 0;
        int matchOffset = 0;
        Set<Location> match = new HashSet<>();
        for (Location w : pWants) {
          if (offset == entry[matchOffset]) {
            match.add(w);
            matchOffset++;
            if (matchOffset == i)
              break;
          }
          offset++;
        }
        results.add(match);
      }
    }

    return results;
  }

  public static class Remember {

    public final NeedsResult providerNeeds;

    public final Location    actualWant;

    public Remember(NeedsResult pProviderNeeds, Location pActualWant) {
      providerNeeds = pProviderNeeds;
      actualWant = pActualWant;
    }

  }

  private List<PossibleInstruction> recursiveGenerate(GenerateInfo pInfo) {
    try (Context ctx = mContextFactory.newContext(MappingServiceImpl.class, this)) {

      ctx.debug("Attempting to at {}", pInfo.debugName);

      /* Find any registered that is capable of supply the want */

      List<PossibleInstruction> generateResults = new ArrayList<>();

      List<Set<Location>> permutations = permutations(pInfo.wantList);
      for (Set<Location> permutation : permutations) {

        Set<Location> mutatableWants = new HashSet<>(permutation);

        List<Remember> remembered = new ArrayList<>();

        Set<Location> additionals = new HashSet<>();

        for (Iterator<Location> wantIterator = mutatableWants.iterator(); wantIterator.hasNext();) {
          Location testWant = wantIterator.next();
          if (additionals.contains(testWant)) {
            wantIterator.remove();
            continue;
          }

          /* Check to see if there is any registration that matches this */

          for (Registration reg : mRegistrations) {

            /* Does this registration provide the want? */

            for (Location regWant : reg.provides) {

              Optional<Location> matchOpt = testWant.match(regWant);
              if (matchOpt.isPresent() == true) {

                /* It does. What 'needs' does it require, and what other wants does it provide */

                Location actualWant = matchOpt.get();
                NeedsResult providerNeeds = reg.provider.evaluateNeeds(this, actualWant);

                /* After further analysis, the provider can't handle it */

                if (providerNeeds.instruction == null)
                  continue;

                /*
                 * If we've already seen the elements the providers needs, then we can't go back, so it can't be
                 * resolved at this point.
                 */
                boolean alreadyDone = false;
                for (Location newWant : providerNeeds.needs) {
                  if (pInfo.seenWantList.contains(newWant)) {
                    alreadyDone = true;
                    break;
                  }
                }
                if (alreadyDone == true)
                  continue;

                /* Ok, this is definitely a possibility. */

                /* Remove this want from the want list */

                wantIterator.remove();

                additionals.addAll(providerNeeds.additionalProvides);
                
                /* Remember this provider */

                remembered.add(new Remember(providerNeeds, actualWant));

              }
            }
          }
        }

        /* Were we able to handle all the wants? */

        if (mutatableWants.isEmpty() == false)
          continue;

        /* Build an updated want list */

        Set<Location> newWantList = new HashSet<>();

        for (Remember rem : remembered) {

          newWantList.addAll(rem.providerNeeds.needs);

        }

        /* Add the remaining wants that weren't handled in this pass */

        OLDWANTS: for (Location oldWant : pInfo.wantList) {

          /* If the existing want matches us, then skip it */

          if (permutation.contains(oldWant) == true)
            continue;

          /* If any of existing wants matches an additional provides, then clear those as well */

          for (Remember rem : remembered)
            if (rem.providerNeeds.additionalProvides.contains(oldWant) == true)
              continue OLDWANTS;

          newWantList.add(oldWant);
        }

        Set<Location> newSeenWantList = new HashSet<>(pInfo.seenWantList);
        newSeenWantList.addAll(permutation);
        for (Remember rem : remembered)
          newSeenWantList.add(rem.actualWant);

        /* Define a new debug name */

        StringBuilder sb = new StringBuilder();
        if (pInfo.debugName.isEmpty() == false) {
          sb.append(pInfo.debugName);
          sb.append(" -> ");
        }
        boolean first = true;
        for (Remember rem : remembered) {
          if (first == true)
            first = false;
          else
            sb.append(",");
          sb.append(rem.providerNeeds.name);
        }
        String newName = sb.toString();

        if (newWantList.isEmpty() == false) {

          /* We still have more wants to fulfill. Continue the recursion */

          List<PossibleInstruction> childResults =
            recursiveGenerate(new GenerateInfo(newSeenWantList, newWantList, newName));
          if (childResults.isEmpty() == true) {
            continue;
          }

          // generateResults.add(new InstructionPair(providerNeeds.name, providerNeeds.instruction,
          // providerNeeds.priority, childResults));
        }
        else {

          /* We no longer have a want. Return a success */

          // generateResults
          // .add(new InstructionStage(providerNeeds.name, providerNeeds.instruction, Collections.emptyList()));
        }

      }

      if (generateResults.isEmpty() == true)
        ctx.trace("<GEN> No match for {}", pInfo.debugName);

      return generateResults;
    }

  }

}
