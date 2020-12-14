package com.diamondq.maply.impl2.jxpath;

import com.diamondq.maply.api2.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.Expression;
import org.apache.commons.jxpath.ri.compiler.LocationPath;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.Path;
import org.apache.commons.jxpath.ri.compiler.Step;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LocationImpl implements Location {

  private final DQJXPathCompiledExpression mExpr;

  public LocationImpl(DQJXPathCompiledExpression pExpr) {
    mExpr = pExpr;
  }

  /**
   * @see com.diamondq.maply.api2.Location#getFirstStepClass()
   */
  @Override
  public Optional<Class<?>> getFirstStepClass() {
    Expression thisExpr = mExpr.getExpression();
    if (thisExpr instanceof Path) {
      Path thisPath = (Path) thisExpr;
      Step[] steps = thisPath.getSteps();
      Step firstStep = steps[0];
      NodeTest nodeTest = firstStep.getNodeTest();
      if (nodeTest instanceof NodeNameTest) {
        NodeNameTest nodeNameTest = (NodeNameTest) nodeTest;
        QName nodeName = nodeNameTest.getNodeName();
        String prefix = nodeName.getPrefix();
        if (prefix.equals("class")) {
          String className = nodeName.getName();
          try {
            return Optional.of(Class.forName(className));
          }
          catch (ClassNotFoundException ex) {
            throw new IllegalStateException();
          }
        }
      }
    }
    return Optional.empty();
  }

  /**
   * @see com.diamondq.maply.api2.Location#getFirstStep()
   */
  @Override
  public Location getFirstStep() {
    Expression thisExpr = mExpr.getExpression();
    if (thisExpr instanceof Path) {
      Path thisPath = (Path) thisExpr;
      Step[] steps = thisPath.getSteps();
      Step firstStep = steps[0];

      Step[] stepArray = new Step[] {firstStep};

      LocationPath locationPath = new LocationPath(true, stepArray);
      return new LocationImpl(new DQJXPathCompiledExpression(locationPath.toString(), locationPath));
    }
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.maply.api2.ToStringIndented#toStringIndented(java.lang.StringBuilder, java.lang.String, boolean)
   */
  @Override
  public StringBuilder toStringIndented(StringBuilder pSB, String pIndexStr, boolean pWithIndenting) {
    pSB.append(mExpr.getXPath());
    return pSB;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return toStringIndented(new StringBuilder(), "  ", true).toString();
  }

  /**
   * @see com.diamondq.maply.api2.Location#getXPath()
   */
  @Override
  public String getXPath() {
    return mExpr.getXPath();
  }

  /**
   * @see com.diamondq.maply.api2.Location#match(com.diamondq.maply.api2.Location)
   */
  @Override
  public Optional<Location> match(Location pAgainstWant) {
    LocationImpl againstWant = (LocationImpl) pAgainstWant;
    Expression thisExpr = mExpr.getExpression();
    Expression againstExpr = againstWant.mExpr.getExpression();
    if ((thisExpr instanceof Path) && (againstExpr instanceof Path)) {
      Path thisPath = (Path) thisExpr;
      Path againstPath = (Path) againstExpr;
      Step[] thisSteps = thisPath.getSteps();
      Step[] againstSteps = againstPath.getSteps();
      List<Step> matchSteps = new ArrayList<>();
      if (thisSteps.length == againstSteps.length) {
        for (int i = 0; i < thisSteps.length; i++) {
          Step thisStep = thisSteps[i];
          Step againstStep = againstSteps[i];
          if (thisStep.getAxis() != againstStep.getAxis()) {
            matchSteps.clear();
            break;
          }
          NodeTest thisNodeTest = thisStep.getNodeTest();
          NodeTest againstNodeTest = againstStep.getNodeTest();
          if (thisNodeTest.getClass().equals(againstNodeTest.getClass()) == false) {
            matchSteps.clear();
            break;
          }
          if (thisNodeTest instanceof NodeNameTest) {
            NodeNameTest thisNodeNameTest = (NodeNameTest) thisNodeTest;
            NodeNameTest againstNodeNameTest = (NodeNameTest) againstNodeTest;
            if (Objects.equals(thisNodeNameTest.getNamespaceURI(), againstNodeNameTest.getNamespaceURI()) == false) {
              matchSteps.clear();
              break;
            }
            if (againstNodeNameTest.isWildcard() == false) {
              if (Objects.equals(thisNodeNameTest.getNodeName(), againstNodeNameTest.getNodeName()) == false) {
                matchSteps.clear();
                break;
              }
            }

            /* This is a match. Create a new step with the appropriate Predicates */

            matchSteps.add(new DQStep(thisStep.getAxis(), thisNodeNameTest, thisStep.getPredicates()));
          }
          else {
            matchSteps.clear();
            break;
          }
        }
      }
      if (matchSteps.isEmpty() == false) {
        Step[] stepArray = matchSteps.toArray(new Step[0]);

        LocationPath locationPath = new LocationPath(true, stepArray);
        return Optional.of(new LocationImpl(new DQJXPathCompiledExpression(locationPath.toString(), locationPath)));
      }
    }
    return Optional.empty();
  }

  /**
   * @see com.diamondq.maply.api2.Location#mergePredicates(com.diamondq.maply.api2.Location)
   */
  @Override
  public Location mergePredicates(Location pWant) {
    LocationImpl want = (LocationImpl) pWant;
    Expression thisExpr = mExpr.getExpression();
    Expression wantExpr = want.mExpr.getExpression();
    if ((thisExpr instanceof Path) && (wantExpr instanceof Path)) {
      Path thisPath = (Path) thisExpr;
      Path wantPath = (Path) wantExpr;
      Step[] thisSteps = thisPath.getSteps();
      Step[] wantSteps = wantPath.getSteps();
      List<Step> matchSteps = new ArrayList<>();
      if (thisSteps.length == wantSteps.length) {
        for (int i = 0; i < thisSteps.length; i++) {
          Step thisStep = thisSteps[i];
          Step wantStep = wantSteps[i];

          /* This is a match. Create a new step with the appropriate Predicates */

          @NonNull
          Expression[] thisPredicates = thisStep.getPredicates();
          @NonNull
          Expression[] wantPredicates = wantStep.getPredicates();
          int count =
            (thisPredicates != null ? thisPredicates.length : 0) + (wantPredicates != null ? wantPredicates.length : 0);
          Expression[] mergedPredicates = new Expression[count];
          int o = 0;
          if ((thisPredicates != null) && (thisPredicates.length > 0))
            for (int i1 = 0; i1 < thisPredicates.length; i1++)
              mergedPredicates[o++] = thisPredicates[i1];
          if ((wantPredicates != null) && (wantPredicates.length > 0))
            for (int i2 = 0; i2 < wantPredicates.length; i2++)
              mergedPredicates[o++] = wantPredicates[i2];

          matchSteps.add(new DQStep(thisStep.getAxis(), thisStep.getNodeTest(), mergedPredicates));
        }
      }
      if (matchSteps.isEmpty() == false) {
        Step[] stepArray = matchSteps.toArray(new Step[0]);

        LocationPath locationPath = new LocationPath(true, stepArray);
        return new LocationImpl(new DQJXPathCompiledExpression(locationPath.toString(), locationPath));
      }
    }
    throw new UnsupportedOperationException();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(mExpr);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj)
      return true;
    if (pObj == null)
      return false;
    if (getClass() != pObj.getClass())
      return false;
    LocationImpl obj = (LocationImpl) pObj;
    return Objects.equals(mExpr, obj.mExpr);
  }

}
