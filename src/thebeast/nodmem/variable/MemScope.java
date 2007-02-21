package thebeast.nodmem.variable;

import thebeast.forml.nod.SimpleName;
import thebeast.nod.exception.NoDScopeException;
import thebeast.nod.identifier.Identifier;
import thebeast.nod.identifier.IdentifierVisitor;
import thebeast.nod.identifier.Name;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.RelationType;
import thebeast.nod.variable.*;
import thebeast.nodmem.statement.MemInterpreter;
import thebeast.nodmem.type.MemRelationType;
import thebeast.nodmem.value.MemRelation;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemScope implements Scope {

    private LinkedList<RelationVariable> relVars = new LinkedList<RelationVariable>();
    private HashMap<Name, RelationVariable> id2relVar = new HashMap<Name, RelationVariable>();
    private LinkedList<TupleVariable> tupleVars = new LinkedList<TupleVariable>();
    private HashMap<Name, TupleVariable> id2tupleVar = new HashMap<Name, TupleVariable>();
    private LinkedList<ArrayVariable> arrayVars = new LinkedList<ArrayVariable>();
    private HashMap<Name, ArrayVariable> id2arrayVar = new HashMap<Name, ArrayVariable>();
    private LinkedList<Scope> scopes = new LinkedList<Scope>();
    private HashMap<Name, Scope> id2scope = new HashMap<Name, Scope>();
    private LinkedList<Variable> vars = new LinkedList<Variable>();

    private MemResolver resolver = new MemResolver();


    private HashMap<Name,Variable> id2var = new HashMap<Name, Variable>();

    private MemInterpreter interpreter;

    private int defaultRelationCapacity = 10;

    public MemScope() {
        interpreter = null;
    }

    public RelationVariable createRelationVariable(Name name, RelationType type) {
        if (name.qualifier()!=null){
            throw new NoDScopeException("Trying to create qualified variable " + name.name());
        }
////        RelationVariable variable = new MemRelationVariable(
////                new MemRelation((MemRelationType) type, defaultRelationCapacity));
//        relVars.add(variable);
//        id2relVar.put(name, variable);
//        id2var.put(name,variable);
//        return variable;
      return null;
    }

    public Scope addSubScope(Name name) {
        MemScope scope = new MemScope();
        id2scope.put(name, scope);
        scopes.add(scope);
        return scope;
    }

    public Scope getSubScope(Name name) {
        return id2scope.get(name);
    }

    public Interpreter interpreter() {
        return interpreter;
    }

    public void destroy() {
        for (Variable var : vars){
            ((AbstractMemVariable)var).destroy();
        }
    }

    public List<RelationVariable> relationVariables() {
        return Collections.unmodifiableList(relVars);
    }

    public List<Scope> scopes() {
        return Collections.unmodifiableList(scopes);
    }

    public Variable variable(Identifier identifier) {
        return resolver.resolve(identifier);
    }

    public RelationVariable relationVariable(Identifier identifier) {
        return (RelationVariable) resolver.resolve(identifier);
    }

    public RelationVariable relationVariable(String identifier) {
        return id2relVar.get(new SimpleName(identifier));
    }

    public void addTupleVariable(Name name, TupleVariable variable) {
        tupleVars.add(variable);
        id2tupleVar.put(name,variable);
        vars.add(variable);
    }

    public List<TupleVariable> tupleVariables() {
        return tupleVars;
    }

    public TupleVariable tupleVariable(Name name) {
        return id2tupleVar.get(name);
    }

    public TupleVariable tupleVariable(String identifier) {
        return tupleVariable(new SimpleName(identifier));
    }

    public void addArrayVariable(Name name, ArrayVariable variable) {
        arrayVars.add(variable);
        id2arrayVar.put(name,variable);
        vars.add(variable);
    }

    public List<ArrayVariable> arrayVariables() {
        return arrayVars;
    }

    public ArrayVariable arrayVariable(Name name) {
        return id2arrayVar.get(name);
    }

    public ArrayVariable arrayVariable(String identifier) {
        return arrayVariable(new SimpleName(identifier));
    }

    private class MemResolver implements IdentifierVisitor {

        private Variable result;

        public Variable resolve(Identifier identifier){
            identifier.acceptIdentifierVisitor(this);
            return result;
        }

        public void visitName(Name name) {
            result = id2var.get(name);
        }
    }

}
