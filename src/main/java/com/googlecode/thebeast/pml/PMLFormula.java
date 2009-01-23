package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public final class PMLFormula {

    private Formula formula;
    private ArrayList<Variable> indexVariables;
    private Variable scaleVariable;

    public PMLFormula(Formula formula,
                      List<Variable> indexVariables,
                      Variable scaleVariable) {
        this.formula = formula;
        this.indexVariables = new ArrayList<Variable>(indexVariables);
        this.scaleVariable = scaleVariable;
    }

    public Formula getFormula() {
        return formula;
    }

    public List<Variable> getIndexVariables() {
        return Collections.unmodifiableList(indexVariables);
    }

    public Variable getScaleVariable() {
        return scaleVariable;
    }

    public String toString(){
        return getFormula() + "[" + (scaleVariable == null ? "" : scaleVariable + " ") + indexVariables + "]";
    }
}
