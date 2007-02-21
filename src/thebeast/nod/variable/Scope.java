package thebeast.nod.variable;

import thebeast.nod.identifier.Name;
import thebeast.nod.identifier.Identifier;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.RelationType;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface Scope {

    List<Scope> scopes();

    Variable variable(Identifier identifier);

    RelationVariable createRelationVariable(Name name, RelationType type);
    List<RelationVariable> relationVariables();
    RelationVariable relationVariable(Identifier identifier);
    RelationVariable relationVariable(String identifier);

    void addTupleVariable(Name name, TupleVariable variable);
    List<TupleVariable> tupleVariables();
    TupleVariable tupleVariable(Name name);
    TupleVariable tupleVariable(String identifier);

    void addArrayVariable(Name name, ArrayVariable variable);
    List<ArrayVariable> arrayVariables();
    ArrayVariable arrayVariable(Name name);
    ArrayVariable arrayVariable(String identifier);

    Scope addSubScope(Name name);
    Scope getSubScope(Name name);

    Interpreter interpreter();

    void destroy();

}
