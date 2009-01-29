package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.pml.pmtl.PMTLFormulaeBuilder;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Signature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public final class PMLFormula {

    private final Formula formula;
    private final ArrayList<Variable> indexVariables;
    private final Variable scaleVariable;
    private final Signature signature;

    public PMLFormula(Formula formula,
                      List<Variable> indexVariables,
                      Variable scaleVariable) {
        this.formula = formula;
        this.indexVariables = new ArrayList<Variable>(indexVariables);
        this.scaleVariable = scaleVariable;
        final HashSet<Signature> signatures = new HashSet<Signature>();
        formula.accept(new DepthFirstFormulaVisitor() {
            @Override
            protected void inAtomFormula(AtomFormula atomFormula) {
                signatures.add(atomFormula.getPredicate().getSignature());
            }
        });
        if (signatures.size() > 1) throw new IllegalArgumentException("The formula must not be constructed with " +
            "different signatures.");
        this.signature = signatures.iterator().next();
    }

    public Formula getFormula() {
        return formula;
    }

    public static PMLFormula createFormula(Signature signature, String pmtl) {
        return new PMTLFormulaeBuilder(signature).interpret(pmtl).get(0);
    }

    public List<Variable> getIndexVariables() {
        return Collections.unmodifiableList(indexVariables);
    }

    public Variable getScaleVariable() {
        return scaleVariable;
    }

    public String toString() {
        return getFormula() + "[" + (scaleVariable == null ? "" : scaleVariable + " ") + indexVariables + "]";
    }

    public Signature getSignature() {
        return signature;
    }
}
