package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Substitution;
import gnu.trove.TObjectDoubleHashMap;
import gnu.trove.TObjectDoubleProcedure;

import java.util.LinkedHashMap;

/**
 * @author Sebastian Riedel
 */
public class PMLVector {

  private LinkedHashMap<PMLClause, TObjectDoubleHashMap>
    clause2Weights = new LinkedHashMap<PMLClause, TObjectDoubleHashMap>();

  public double getValue(PMLClause clause, FeatureIndex index) {
    TObjectDoubleHashMap weights = clause2Weights.get(clause);
    if (weights == null) return 0;
    return weights.get(index);
  }

  public void setValue(PMLClause clause, double value) {
    setValue(clause, new FeatureIndex(new Substitution()), value);
  }

  public void setValue(PMLClause clause, String substitutions, double value) {
    setValue(clause, new FeatureIndex(
      Substitution.createSubstitution(clause.getSignature(), substitutions)
    ), value);
  }


  public void setValue(PMLClause clause, FeatureIndex index, double value) {
    TObjectDoubleHashMap weights = clause2Weights.get(clause);
    if (weights == null) {
      weights = new TObjectDoubleHashMap();
      clause2Weights.put(clause, weights);
    }
    weights.put(index, value);
  }

  public void addValue(PMLClause clause, FeatureIndex index, double value) {
    setValue(clause, index, getValue(clause, index) + value);
  }

  public double dotProduct(final PMLVector vector) {
    double result = 0;
    for (final PMLClause clause : clause2Weights.keySet()) {
      DotProductCalculator calculator = new DotProductCalculator(vector, clause);
      TObjectDoubleHashMap weights = clause2Weights.get(clause);
      weights.forEachEntry(calculator);
      result += calculator.result;
    }
    return result;
  }


  private static class DotProductCalculator implements TObjectDoubleProcedure {
    double result;
    private final PMLVector vector;
    private final PMLClause clause;

    public DotProductCalculator(PMLVector vector, PMLClause clause) {
      this.vector = vector;
      this.clause = clause;
      result = 0;
    }

    public boolean execute(Object o, double v) {
      result += vector.getValue(clause, (FeatureIndex) o) * v;
      return true;
    }
  }
}
