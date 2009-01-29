package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.pml.pmtl.analysis.DepthFirstAdapter;
import com.googlecode.thebeast.pml.pmtl.node.*;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.UserConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * A TypeEquationExtractor extracts a list of type expression equations from an Abstract Syntax Tree of a PMTL
 * expression.
 *
 * @author Sebastian Riedel
 */
public class TypeEquationExtractor extends DepthFirstAdapter {
    private List<NodeTypeEquation> equations = new ArrayList<NodeTypeEquation>();
    private NodeTypeList argumentTypeExpressions = new NodeTypeList();
    private Signature signature;

    /**
     * Extract a list of type expression equations from the given clause node.
     *
     * @param signature the signature from which to take the types.
     * @param node    the syntax tree node describing the clause.
     * @return the set of type expression constraints defined by the clause syntax node.
     */
    public static List<NodeTypeEquation> extractEquations(Signature signature, Node node) {
        final TypeEquationExtractor extractor = new TypeEquationExtractor(signature);
        node.apply(extractor);
        return extractor.equations;
    }

    /**
     * Create a new TypeEquationExtractor using the given signature in order to access the types of constants and
     * predicates mentioned in the syntax tree.
     *
     * @param signature the signature to use.
     */
    public TypeEquationExtractor(Signature signature) {
        this.signature = signature;
    }

    @Override
    public void inAAtom(AAtom node) {
        argumentTypeExpressions.clear();
    }

    @Override
    public void outAAtom(AAtom node) {
        final String predicateName = ((APredicate) node.getPredicate()).getName().getText();
        Predicate pred = signature.getPredicate(predicateName);
        if (pred != null) {
            //the type of the predicate is given
            equations.add(new NodeTypeEquation(
                new NodeTypeVariable(node.getPredicate()),
                new PredicateNodeType(pred.getArgumentTypes())
            ));
            for (int argIndex = 0; argIndex < pred.getArgumentTypes().size(); ++argIndex) {
                equations.add(new NodeTypeEquation(
                    new NodeTypeVariable(node.getArgs().get(argIndex)),
                    new TermNodeType(pred.getArgumentTypes().get(argIndex))
                ));
            }
        } else if (predicateName.equals("=")) {
            //we know that both nodes have to have the same type, but don't know which.
            equations.add(new NodeTypeEquation(argumentTypeExpressions.get(0), argumentTypeExpressions.get(1)));
            equations.add(new NodeTypeEquation(
                new NodeTypeVariable(node.getPredicate()),
                argumentTypeExpressions));
        } else {
            throw new IllegalArgumentException("The predicate " + predicateName + " cannot be resolved");
        }
    }

    @Override
    public void outAVariableTerm(AVariableTerm node) {
        argumentTypeExpressions.add(new NodeTypeVariable(node));
    }

    @Override
    public void outALongTerm(ALongTerm node) {
        argumentTypeExpressions.add(new TermNodeType(signature.getIntegerType()));
    }

    @Override
    public void outADoubleTerm(ADoubleTerm node) {
        argumentTypeExpressions.add(new TermNodeType(signature.getDoubleType()));
    }

    @Override
    public void outAConstantTerm(AConstantTerm node) {
        final UserConstant constant = (UserConstant) signature.getSymbol(node.getName().getText());
        argumentTypeExpressions.add(new TermNodeType(constant.getType()));
    }

    @Override
    public void outAStringTerm(AStringTerm node) {
        final UserConstant constant = (UserConstant) signature.getSymbol(node.getValue().getText());
        argumentTypeExpressions.add(new TermNodeType(constant.getType()));
    }
}
