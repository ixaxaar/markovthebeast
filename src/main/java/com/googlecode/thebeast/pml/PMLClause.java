package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.DoubleType;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.Signature;

import java.util.*;

/**
 * A PMLClause maps a possible world to a feature vector. todo: fixing <p/> Let m be a PMLClause with query q, scale
 * variable s and index variable i, and w a possible world, and let r be the result of applying the query q to the world
 * w. Then m maps w to a feature vector f(w) that contains one component for each possible binding b of the index
 * variable i and the value f_b(w) is defined as follows. For each nested substitution s in r for which the outer
 * substitution is consistent with b we add 1.0 to f_b(w) if the outer conjunction of q is false in w when applied to s,
 * and the value of the first order operator applied to the set of ground atoms defined by the target atom and the inner
 * substitutions in s.
 *
 * @author Sebastian Riedel
 */
public class PMLClause {

    /**
     * The scale variable.
     */
    private final Variable scaleVariable;

    /**
     * The index variable.
     */
    private final List<Variable> indexVariables;

    private final List<Atom> body = new ArrayList<Atom>();
    private final List<Atom> restriction = new ArrayList<Atom>();
    private final Atom head;

    private final FirstOrderOperator firstOrderOperator;
    private Signature signature;
    private final List<Variable> innerVariables = new ArrayList<Variable>();
    private List<Variable> bodyVariables;
    private List<Variable> headVariables;
    private List<Variable> restrictionVariables;
    private List<Variable> allVariables;
    private List<Variable> outerVariables;

    public PMLClause(final List<Atom> body,
                     final Atom head,
                     final List<Variable> innerVariables,
                     final List<Atom> restriction,
                     final FirstOrderOperator operator,
                     final List<Variable> indexVariables,
                     final Variable scaleVariable
    ) throws ConstructionException {



        this.body.addAll(body);
        this.head = head;
        this.restriction.addAll(restriction);
        this.firstOrderOperator = operator;
        this.indexVariables = indexVariables;
        this.scaleVariable = scaleVariable;
        this.innerVariables.addAll(innerVariables);

        extractVariables();
        sanityCheck();

        signature = checkSignature();

    }

    private void sanityCheck() throws ConstructionException {
        //check that inner variables are not contained in body.
        for (Variable innerVariable : innerVariables) {
            if (bodyVariables.contains(innerVariable))
                throw new ConstructionException("Inner variable " + innerVariable + " must not be contained" +
                    " in body of clause");
        }
        //check that inner variables are in head.
        for (Variable innerVariable : innerVariables) {
            if (!headVariables.contains(innerVariable))
                throw new ConstructionException("Inner variable " + innerVariable + " must be contained" +
                    " in head of clause");
        }

        //check scale variables have the right type.
        if (scaleVariable != null && !(scaleVariable.getType() instanceof DoubleType))
            throw new ConstructionException("Scale variable not double");

        //check whether scale variable actually appear in the body
        if (scaleVariable != null && !bodyVariables.contains(scaleVariable))
            throw new ConstructionException("Scale variable not contained in body");
    }

    private void extractVariables() {
        //variables in the body of the clause
        bodyVariables = Variable.getAllVariables(body);
        //variables in head
        headVariables = Variable.getAllVariables(Collections.singleton(head));
        //variables in restriction
        restrictionVariables = Variable.getAllVariables(restriction);
        //all variables
        LinkedHashSet<Variable> allVariablesSet = new LinkedHashSet<Variable>();
        allVariablesSet.addAll(bodyVariables);
        allVariablesSet.addAll(headVariables);
        allVariablesSet.addAll(restrictionVariables);
        allVariables = new ArrayList<Variable>(allVariablesSet);

        //outer variables
        outerVariables = new ArrayList<Variable>(allVariables);
        outerVariables.removeAll(innerVariables);
    }

    public Signature getSignature() {
        return signature;
    }

    private Signature checkSignature() throws ConstructionException {
        Signature result = null;

        Collection<Atom> allAtoms = new ArrayList<Atom>();
        allAtoms.addAll(body);
        allAtoms.addAll(restriction);
        if (head != null) allAtoms.add(head);

        for (Atom atom : allAtoms) {
            Signature signature = atom.getPredicate().getSignature();
            if (signature != null) {
                if (result != null && signature != result)
                    throw new ConstructionException("Signatures of used " +
                        "symbols do not match (" + atom.getPredicate() + ")");
                else
                    result = signature;
            }
        }
        return result;
    }


    public List<Atom> getBody() {
        return body;
    }

    public List<Atom> getRestriction() {
        return restriction;
    }

    public Atom getHead() {
        return head;
    }

    public Variable getScaleVariable() {
        return scaleVariable;
    }

    public List<Variable> getIndexVariables() {
        return Collections.unmodifiableList(indexVariables);
    }


    public List<Variable> getInnerVariables() {
        return Collections.unmodifiableList(innerVariables);
    }

    public List<Variable> getAllVariables() {
        return Collections.unmodifiableList(allVariables);
    }

    public List<Variable> getOuterVariables() {
        return Collections.unmodifiableList(outerVariables);
    }

    public FirstOrderOperator getFirstOrderOperator() {
        return firstOrderOperator;
    }

    public List<Variable> getRestrictionVariables() {
        return Collections.unmodifiableList(restrictionVariables);
    }

    public static class ConstructionException extends RuntimeException {
        public ConstructionException(String message) {
            super(message);
        }
    }
}
