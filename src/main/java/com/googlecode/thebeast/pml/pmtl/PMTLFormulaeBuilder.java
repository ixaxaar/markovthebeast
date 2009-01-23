package com.googlecode.thebeast.pml.pmtl;

import com.googlecode.thebeast.pml.And;
import com.googlecode.thebeast.pml.AtomFormula;
import com.googlecode.thebeast.pml.BinaryOperatorFormula;
import com.googlecode.thebeast.pml.ComposableFormula;
import com.googlecode.thebeast.pml.Implies;
import com.googlecode.thebeast.pml.PMLFormula;
import com.googlecode.thebeast.pml.pmtl.analysis.DepthFirstAdapter;
import com.googlecode.thebeast.pml.pmtl.lexer.Lexer;
import com.googlecode.thebeast.pml.pmtl.lexer.LexerException;
import com.googlecode.thebeast.pml.pmtl.node.AAndComposable;
import com.googlecode.thebeast.pml.pmtl.node.AAtom;
import com.googlecode.thebeast.pml.pmtl.node.AConstantTerm;
import com.googlecode.thebeast.pml.pmtl.node.ADoubleTerm;
import com.googlecode.thebeast.pml.pmtl.node.AFormulaPmtl;
import com.googlecode.thebeast.pml.pmtl.node.AImpliesComposable;
import com.googlecode.thebeast.pml.pmtl.node.AIndexTerm;
import com.googlecode.thebeast.pml.pmtl.node.ALongTerm;
import com.googlecode.thebeast.pml.pmtl.node.APredicate;
import com.googlecode.thebeast.pml.pmtl.node.AScaleTerm;
import com.googlecode.thebeast.pml.pmtl.node.AStringTerm;
import com.googlecode.thebeast.pml.pmtl.node.AVariableTerm;
import com.googlecode.thebeast.pml.pmtl.node.Start;
import com.googlecode.thebeast.pml.pmtl.parser.Parser;
import com.googlecode.thebeast.pml.pmtl.parser.ParserException;
import com.googlecode.thebeast.pml.pmtl.typeinference.NodeTypeEquation;
import com.googlecode.thebeast.pml.pmtl.typeinference.NodeTypeExpressionUnifier;
import com.googlecode.thebeast.pml.pmtl.typeinference.NodeTypeSubstitution;
import com.googlecode.thebeast.pml.pmtl.typeinference.TypeEquationExtractor;
import com.googlecode.thebeast.query.Term;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Signature;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Sebastian Riedel
 */
public class PMTLFormulaeBuilder extends DepthFirstAdapter {

    private Stack<ComposableFormula> formulae = new Stack<ComposableFormula>();
    private NodeTypeSubstitution substitution;
    private Signature signature;
    private List<PMLFormula> pmlFormulae = new ArrayList<PMLFormula>();

    public PMTLFormulaeBuilder(Signature signature) {
        this.signature = signature;
    }

    /**
     * The index variables the interpreter collects while tree-walking in a clause subtree.
     */
    private ArrayList<Variable> indexVariables = new ArrayList<Variable>();

    /**
     * The scale variable the interpreter found while tree-walking in a clause subtree.
     */
    private Variable scaleVariable;

    /**
     * The terms the interpreter collects while tree-walking in a formula subtree.
     */
    private ArrayList<Term> terms = new ArrayList<Term>();

    public List<PMLFormula> interpret(String statement) {
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(statement), 1000)));
        try {
            Start start = parser.parse();
            start.apply(this);
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (LexerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<PMLFormula>(pmlFormulae);
    }

    @Override
    public void inAFormulaPmtl(AFormulaPmtl node) {
        List<NodeTypeEquation> typeEquationList = TypeEquationExtractor.extractEquations(signature, node);
        indexVariables.clear();
        scaleVariable = null;
        substitution = NodeTypeExpressionUnifier.unify(typeEquationList);
    }

    @Override
    public void outAFormulaPmtl(AFormulaPmtl node) {
        pmlFormulae.add(new PMLFormula(formulae.pop(),indexVariables, scaleVariable));
    }

    @Override
    public void inAAtom(AAtom node) {
        terms.clear();
    }

    @Override
    public void outAAtom(AAtom node) {
        String predName = ((APredicate) node.getPredicate()).getName().getText();
        if (predName.equals("=")) {

        } else {
            Predicate predicate = signature.getPredicate(predName);
            formulae.push(new AtomFormula(predicate, terms));
        }
    }

    @Override
    public void outAAndComposable(AAndComposable node) {
        ComposableFormula arg2 = formulae.pop();
        ComposableFormula arg1 = formulae.pop();
        formulae.push(new BinaryOperatorFormula(And.AND, arg1, arg2));
    }

    @Override
    public void outAImpliesComposable(AImpliesComposable node) {
        ComposableFormula arg2 = formulae.pop();
        ComposableFormula arg1 = formulae.pop();
        formulae.push(new BinaryOperatorFormula(Implies.IMPLIES, arg1, arg2));
    }

    @Override
    public void outAVariableTerm(AVariableTerm node) {
        terms.add(new Variable(node.getName().getText(), substitution.getType(node)));
    }

    @Override
    public void outALongTerm(ALongTerm node) {
        terms.add(substitution.getType(node).getConstant(node.getValue().getText()));
    }

    @Override
    public void outAConstantTerm(AConstantTerm node) {
        terms.add(substitution.getType(node).getConstant(node.getName().getText()));
    }

    @Override
    public void outADoubleTerm(ADoubleTerm node) {
        terms.add(substitution.getType(node).getConstant(node.getValue().getText()));
    }

    @Override
    public void outAStringTerm(AStringTerm node) {
        terms.add(substitution.getType(node).getConstant(node.getValue().getText()));
    }

    @Override
    public void outAIndexTerm(AIndexTerm node) {
        Variable indexVariable = new Variable(node.getName().getText(), substitution.getType(node));
        if (!indexVariables.contains(indexVariable))
            indexVariables.add(indexVariable);
        terms.add(indexVariable);
    }

    @Override
    public void outAScaleTerm(AScaleTerm node) {
        Variable newScaleVariable = new Variable(node.getName().getText(), substitution.getType(node));
        if (scaleVariable != null && !scaleVariable.equals(newScaleVariable)) {
            throw new RuntimeException("There must not be more than one scale variable");
        }
        scaleVariable = newScaleVariable;
        terms.add(scaleVariable);
    }


}
