package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.DoubleType;
import com.googlecode.thebeast.world.Signature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A PMLClause maps a possible world to a feature vector. This mapping is defined through the following attributes of a
 * PMLClause:
 *
 * <ul> <li>a list of <b>body</b> atoms</li> <li>a <b>head</b> atom</li> <li>a list of <b>restriction</b> atoms</li>
 * <li>a <b>First Order Operator</b></li> <li>a list of <b>inner</b> variables</li> < li>a list of <b>index</b>
 * variables</li> <li>a <b>scale</b> variable (optional)</li> </ul>
 *
 * These attributes define a feature vector f(y) for the possible world y as follows. Let the set of <b>outer</b>
 * variables be all variables appearing in the body and head of the clause but not in the set of inner variables. Now
 * let i be an assignment for the index variables of the clause, then the feature vector f(y) has a component f_i(y)
 * with the value:
 *
 * <pre>
 * f_i(y) = sum_{o : assignment of outer variables so that body/o is true in y} (1 - FOL(heads)) * o(scaleVariable)
 * </pre>
 *
 * todo: deal with heads, simplify, no scale variable todo: start with most simple version (head, body), then (inner
 * variables), then fol, and present extensions later.
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

    /**
     * Checks wether this PMLClause object describes a valid PML Clause.
     *
     * @throws ConstructionException if the PMLClause definition is inconsistent or invalid.
     */
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

    /**
     * Extracts sets of variables mentioned in the clause based on their position within the clause.
     */
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

    /**
     * Returns the signature that the symbols of the clause belong to. Note that a clause cannot be created with symbols
     * from different signatures.
     *
     * @return the common signature of all symbols in the clause.
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Checks whether all symbols in the clause belong to the same signature and determines this signature.
     *
     * @return the common signature of all symbols in the clause.
     * @throws ConstructionException if symbols have different signatures.
     */
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


    /**
     * Returns the body of this clause.
     *
     * @return the body of this clause.
     */
    public List<Atom> getBody() {
        return Collections.unmodifiableList(body);
    }

    /**
     * Returns the restriction of this clause.
     *
     * @return the restriction of this clause.
     */
    public List<Atom> getRestriction() {
        return Collections.unmodifiableList(restriction);
    }

    /**
     * Returns the head atom of this clause.
     *
     * @return the head atom of this clause.
     */
    public Atom getHead() {
        return head;
    }

    /**
     * Returns the scale variable of this clause.
     *
     * @return the scale variable of this clause, of type {@link com.googlecode.thebeast.world.DoubleType}.
     */
    public Variable getScaleVariable() {
        return scaleVariable;
    }

    /**
     * Returns the index variables of this clause.
     *
     * @return the index variables of this clause.
     */
    public List<Variable> getIndexVariables() {
        return Collections.unmodifiableList(indexVariables);
    }


    /**
     * Returns the inner variables of this clause.
     *
     * @return the list of inner variables of this clause.
     */
    public List<Variable> getInnerVariables() {
        return Collections.unmodifiableList(innerVariables);
    }

    /**
     * Returns all variables mentioned in this clause.
     *
     * @return the set of all variables in this clause.
     */
    public List<Variable> getAllVariables() {
        return Collections.unmodifiableList(allVariables);
    }

    /**
     * Returns the list of outer variables (all variables which are not inner variables).
     *
     * @return the list of outer variables.
     */
    public List<Variable> getOuterVariables() {
        return Collections.unmodifiableList(outerVariables);
    }

    /**
     * Returns the FirstOrderOperator to be applied to the collection of ground atoms defined by the head atom and the
     * restriction atoms.
     *
     * @return the FirstOrderOperator of this clause.
     */
    public FirstOrderOperator getFirstOrderOperator() {
        return firstOrderOperator;
    }

    /**
     * Returns the list of restriction atoms of this clause.
     *
     * @return the list of restriction atoms of this clause.
     */
    public List<Variable> getRestrictionVariables() {
        return Collections.unmodifiableList(restrictionVariables);
    }

    /**
     * A ConstructionException is thrown if clients try to create invalid PMLClause objects.
     */
    public static class ConstructionException extends RuntimeException {
        /**
         * Creates a new ConstructionException with the given message.
         *
         * @param message the message of the exception.
         */
        public ConstructionException(String message) {
            super(message);
        }
    }
}
