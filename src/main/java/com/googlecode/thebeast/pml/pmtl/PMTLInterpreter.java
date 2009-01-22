package com.googlecode.thebeast.pml.pmtl;

import com.googlecode.thebeast.pml.Exists;
import com.googlecode.thebeast.pml.PMLClause;
import com.googlecode.thebeast.pml.Formula;
import com.googlecode.thebeast.pml.pmtl.analysis.DepthFirstAdapter;
import com.googlecode.thebeast.pml.pmtl.lexer.Lexer;
import com.googlecode.thebeast.pml.pmtl.lexer.LexerException;
import com.googlecode.thebeast.pml.pmtl.node.AAtom;
import com.googlecode.thebeast.pml.pmtl.node.AClause;
import com.googlecode.thebeast.pml.pmtl.node.AConstantTerm;
import com.googlecode.thebeast.pml.pmtl.node.ADoubleTerm;
import com.googlecode.thebeast.pml.pmtl.node.AIndexTerm;
import com.googlecode.thebeast.pml.pmtl.node.ALongTerm;
import com.googlecode.thebeast.pml.pmtl.node.APredicate;
import com.googlecode.thebeast.pml.pmtl.node.AScaleTerm;
import com.googlecode.thebeast.pml.pmtl.node.AStringTerm;
import com.googlecode.thebeast.pml.pmtl.node.AVariableTerm;
import com.googlecode.thebeast.pml.pmtl.node.PAtom;
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
 * A PMTLInterpreter generates PMLCLauses for given PMTL clause expressions.
 *
 * @author Sebastian Riedel
 */
public final class PMTLInterpreter extends DepthFirstAdapter {

    /**
     * The terms the interpreter collects while tree-walking in a clause subtree.
     */
    private ArrayList<Term> terms = new ArrayList<Term>();
    /**
     * The atoms the interpreter collects while tree-walking in a clause subtree.
     */
    private ArrayList<Atom> atoms = new ArrayList<Atom>();
    /**
     * The index variables the interpreter collects while tree-walking in a clause subtree.
     */
    private ArrayList<Variable> indexVariables = new ArrayList<Variable>();

    /**
     * The scale variable the interpreter found while tree-walking in a clause subtree.
     */
    private Variable scaleVariable;

    /**
     * The signature the interpreter uses to create/get symbols.
     */
    private Signature signature;

    /**
     * The factory to use for constructing atoms.
     */
    private QueryFactory queryFactory = QueryFactory.getInstance();

    /**
     * The type substitution (that provides the right type for the nodes of the AST).
     */
    private NodeTypeSubstitution substitution;

    /**
     * The clauses generated while tree-walking the complete pmtl statement/programm.
     */
    private ArrayList<PMLClause> clauses = new ArrayList<PMLClause>();


    /**
     * Creates a new interpreter that uses the given signature in order to create/get the symbols mentioned in the each
     * PMTL statement.
     *
     * @param signature the signature to use for creating/getting the symbols mentioned in each pmtl statement.
     */
    public PMTLInterpreter(Signature signature) {
        this.signature = signature;
    }

    /**
     * Returns all clauses described in the given PMTL statement/program.
     *
     * @param statement the PMTL statement to interpret.
     * @return the PMLClause objects described in the statement/statements.
     */
    public List<PMLClause> interpret(String statement) {
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(statement), 1000)));
        clauses.clear();
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
        return new ArrayList<PMLClause>(clauses);
    }
    

    @Override
    public void caseAClause(AClause node) {
        List<NodeTypeEquation> typeEquationList = TypeEquationExtractor.extractEquations(signature, node);
        substitution = NodeTypeExpressionUnifier.unify(typeEquationList);
        node.getHead().apply(this);
        Atom head = atoms.get(0);
        atoms.clear();
        for (PAtom atom : node.getBody())
            atom.apply(this);
        List<Atom> body = new ArrayList<Atom>(atoms);
        PMLClause clause = new PMLClause(body, head, new ArrayList<Variable>(), new ArrayList<Atom>(),
            new Exists(), indexVariables, scaleVariable);
        clauses.add(clause);

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
            atoms.add(queryFactory.createAtom(predicate, terms));
        }
    }


}


