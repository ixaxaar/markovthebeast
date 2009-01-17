package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.pml.pmtl.analysis.DepthFirstAdapter;
import com.googlecode.thebeast.pml.pmtl.node.AAtom;
import com.googlecode.thebeast.pml.pmtl.node.AClause;
import com.googlecode.thebeast.pml.pmtl.node.AConstantTerm;
import com.googlecode.thebeast.pml.pmtl.node.ADoubleTerm;
import com.googlecode.thebeast.pml.pmtl.node.ALongTerm;
import com.googlecode.thebeast.pml.pmtl.node.AStringTerm;
import com.googlecode.thebeast.pml.pmtl.node.AVariableTerm;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Signature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
*/
public class TypeEquationExtractor extends DepthFirstAdapter {
    private List<NodeTypeEquation> equations = new ArrayList<NodeTypeEquation>();
    private NodeTypeList argumentTypeExpressions = new NodeTypeList();
    private Signature signature;

    public static List<NodeTypeEquation> extractEquations(Signature signature, AClause clause){
        final TypeEquationExtractor extractor = new TypeEquationExtractor(signature);
        clause.apply(extractor);
        return extractor.equations;
    }

    public TypeEquationExtractor(Signature signature) {
        this.signature = signature;
    }

    @Override
    public void inAAtom(AAtom node) {
        argumentTypeExpressions.clear();
    }

    @Override
    public void outAAtom(AAtom node) {
        final String predicateName = node.getPredicate().toString();
        Predicate pred = signature.getPredicate(predicateName);
        if (pred != null) {
            //the type of the predicate is given
            equations.add(new NodeTypeEquation(
                new NodeTypeVariable(node.getPredicate()),
                new PredicateNodeType(pred.getArgumentTypes())
            ));
            for (int argIndex = 0; argIndex < pred.getArgumentTypes().size(); ++argIndex){
                equations.add(new NodeTypeEquation(
                    new NodeTypeVariable(node.getArgs().get(argIndex)),
                    new TermNodeType(pred.getArgumentTypes().get(argIndex))
                ));
            }
        } else if (predicateName.equals("=")){
            //we know that both nodes have to have the same type, but don't know which.
            equations.add(new NodeTypeEquation(argumentTypeExpressions.get(0),argumentTypeExpressions.get(1)));
            equations.add(new NodeTypeEquation(
                new NodeTypeVariable(node.getPredicate()),
                argumentTypeExpressions));
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
        argumentTypeExpressions.add(new TermNodeType(signature.getType(node.getName().getText())));
    }

    @Override
    public void outAStringTerm(AStringTerm node) {
        argumentTypeExpressions.add(new TermNodeType(signature.getType(node.getValue().getText())));
    }
}
