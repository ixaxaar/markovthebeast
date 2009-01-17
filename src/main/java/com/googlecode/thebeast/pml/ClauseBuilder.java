package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.Atom;
import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.query.Variable;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.WorldUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sebastian Riedel
 */
public final class ClauseBuilder {

    private ArrayList<Atom> atoms = new ArrayList<Atom>();
    private ArrayList<Atom> body = new ArrayList<Atom>();
    private ArrayList<Atom> restriction = new ArrayList<Atom>();
    private Atom head;
    private Signature signature;
    private ArrayList<Variable> innerVariables = new ArrayList<Variable>();
    private Map<String, Variable> name2variable = new LinkedHashMap<String, Variable>();

    private QueryFactory factory;

    public ClauseBuilder(QueryFactory factory, Signature signature) {
        this.factory = factory;
        this.signature = signature;
    }

    public ClauseBuilder atom(Predicate pred, Object... args) {
        Atom atom = factory.createAtom(pred, WorldUtils.resolveArguments(pred,name2variable,args));
        atoms.add(atom);
        return this;
    }

    public ClauseBuilder body() {
        body.addAll(atoms);
        atoms.clear();
        return this;
    }

    public ClauseBuilder restrict() {
        restriction.addAll(atoms);
        atoms.clear();
        return this;
    }

    public ClauseBuilder head(UserPredicate pred, Object... args) {
        head = factory.createAtom(pred, WorldUtils.resolveArguments(pred,name2variable,args));
        return this;
    }

    public PMLClause clause(String ... indexVariable){
        return clause(new Exists(), resolveVariables(indexVariable), null);     
    }

    private List<Variable> resolveVariables(String ... variableNames){
        ArrayList<Variable> result = new ArrayList<Variable>(variableNames.length);
        for (String name : variableNames){
            result.add(name2variable.get(name));
        }
        return result;
    }

    public PMLClause clause(final FirstOrderOperator operator,
                            final List<Variable> indexVariables,
                            final Variable scaleVariable) {

        PMLClause result = new PMLClause(body, head, innerVariables, restriction,
            operator, indexVariables, scaleVariable);
        clearState();
        return result;
    }

    private void clearState() {
        body.clear();
        head = null;
        restriction.clear();
        atoms.clear();
        innerVariables.clear();
        name2variable.clear();
    }

    public PMLClause clause(FirstOrderOperator operator,
                            Variable indexVariable,
                            Variable scaleVariable) {

        return clause(operator, Collections.singletonList(indexVariable), scaleVariable);

    }

    public PMLClause clause(FirstOrderOperator operator,
                            String indexVariable,
                            String scaleVariable) {

        return clause(operator,
            new Variable(indexVariable, signature.getIntegerType()),
            new Variable(scaleVariable, signature.getDoubleType()));
    }


    public PMLClause clause() {
        return clause(new Exists(), new ArrayList<Variable>(), null);
    }

    public ClauseBuilder inner(String ... variables) {
        for (String var : variables){
            innerVariables.add(name2variable.get(var));
        }
        return this;
    }
}
