package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Substitution;
import gnu.trove.TObjectDoubleHashMap;
import gnu.trove.TObjectDoubleProcedure;

import java.util.LinkedHashMap;

/**
 * @author Sebastian Riedel
 */
public class PMLVector {

    private LinkedHashMap<PMLFormula, TObjectDoubleHashMap>
        formula2Weights = new LinkedHashMap<PMLFormula, TObjectDoubleHashMap>();

    public double getValue(PMLFormula formula, FeatureIndex index) {
        TObjectDoubleHashMap weights = formula2Weights.get(formula);
        if (weights == null) return 0;
        return weights.get(index);
    }

    public void setValue(PMLFormula formula, double value) {
        setValue(formula, new FeatureIndex(new Substitution()), value);
    }

    public void setValue(PMLFormula formula, String substitutions, double value) {
        setValue(formula, new FeatureIndex(
            Substitution.createSubstitution(formula.getSignature(), substitutions)
        ), value);
    }


    public void setValue(PMLFormula formula, FeatureIndex index, double value) {
        TObjectDoubleHashMap weights = formula2Weights.get(formula);
        if (weights == null) {
            weights = new TObjectDoubleHashMap();
            formula2Weights.put(formula, weights);
        }
        weights.put(index, value);
    }

    public void addValue(PMLFormula formula, FeatureIndex index, double value) {
        setValue(formula, index, getValue(formula, index) + value);
    }

    public double dotProduct(final PMLVector vector) {
        double result = 0;
        for (final PMLFormula formula : formula2Weights.keySet()) {
            DotProductCalculator calculator = new DotProductCalculator(vector, formula);
            TObjectDoubleHashMap weights = formula2Weights.get(formula);
            weights.forEachEntry(calculator);
            result += calculator.result;
        }
        return result;
    }


    private static class DotProductCalculator implements TObjectDoubleProcedure {
        double result;
        private final PMLVector vector;
        private final PMLFormula formula;

        public DotProductCalculator(PMLVector vector, PMLFormula formula) {
            this.vector = vector;
            this.formula = formula;
            result = 0;
        }

        public boolean execute(Object o, double v) {
            result += vector.getValue(formula, (FeatureIndex) o) * v;
            return true;
        }
    }
}
