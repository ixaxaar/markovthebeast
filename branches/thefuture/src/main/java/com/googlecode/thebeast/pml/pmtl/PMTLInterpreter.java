package com.googlecode.thebeast.pml.pmtl;

import com.googlecode.thebeast.pml.PMLClause;
import com.googlecode.thebeast.pml.pmtl.analysis.DepthFirstAdapter;
import com.googlecode.thebeast.pml.pmtl.lexer.Lexer;
import com.googlecode.thebeast.pml.pmtl.lexer.LexerException;
import com.googlecode.thebeast.pml.pmtl.node.AAtom;
import com.googlecode.thebeast.pml.pmtl.node.AClause;
import com.googlecode.thebeast.pml.pmtl.node.ADoubleTerm;
import com.googlecode.thebeast.pml.pmtl.node.AIndexTerm;
import com.googlecode.thebeast.pml.pmtl.node.ALongTerm;
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
import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.query.Term;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Signature;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class PMTLInterpreter extends DepthFirstAdapter {

    private ArrayList<Term> terms = new ArrayList<Term>();
    private ArrayList<Atom> atoms = new ArrayList<Atom>();
    private ArrayList<Variable> indexVariables = new ArrayList<Variable>();
    private Variable scaleVariable;
    private ArrayList<Atom> body = new ArrayList<Atom>();
    private Atom head;
    private Signature signature;
    private ArrayList<String> termStrings = new ArrayList<String>();
    private QueryFactory queryFactory = QueryFactory.getInstance();
    private NodeTypeSubstitution substitution;

    public PMTLInterpreter(Signature signature) {
        this.signature = signature;
    }

    public PMLClause interpret(String statement) {
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(statement))));

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
        return null;
    }

    @Override
    public void inAClause(AClause node) {
        List<NodeTypeEquation> typeEquationList = TypeEquationExtractor.extractEquations(signature, node);
        substitution = NodeTypeExpressionUnifier.unify(typeEquationList);
    }

    @Override
    public void outAVariableTerm(AVariableTerm node) {
        terms.add(substitution.getType(node).getConstant(node.getName().getText()));
    }

    @Override
    public void outALongTerm(ALongTerm node) {
        terms.add(substitution.getType(node).getConstant(node.getValue().getText()));
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
        if (scaleVariable != null && !scaleVariable.equals(newScaleVariable)){
            throw new RuntimeException("There must not be more than one scale variable");
        }
        scaleVariable = newScaleVariable;
        terms.add(scaleVariable);
    }

    @Override
    public void inAAtom(AAtom node) {
        terms.clear();
    }

    @Override
    public void outAAtom(AAtom node) {
        String predName = node.getPredicate().toString();
        if (predName.equals("=")){

        } else {
            Predicate predicate = signature.getPredicate(predName);
            atoms.add(queryFactory.createAtom(predicate, terms));
        }
    }


}


