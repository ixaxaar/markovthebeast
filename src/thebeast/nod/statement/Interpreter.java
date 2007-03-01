package thebeast.nod.statement;

import thebeast.nod.expression.*;
import thebeast.nod.type.Heading;
import thebeast.nod.type.Type;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.variable.*;
import thebeast.nod.value.BoolValue;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.DoubleValue;
import thebeast.nod.value.RelationValue;
import thebeast.nod.util.ExpressionBuilder;

import java.util.List;
import java.util.Collection;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public interface Interpreter {

  void interpret(Statement statement);

  void assign(BoolVariable variable, BoolExpression expression);

  void assign(RelationVariable variable, RelationExpression expression);

  void load(RelationVariable variable, InputStream inputStream) throws IOException;

  void assign(IntVariable variable, IntExpression expression);

  void assign(DoubleVariable variable, DoubleExpression expression);

  void assign(TupleVariable variable, TupleExpression expression);

  void assign(ArrayVariable variable, ArrayExpression expression);

  void insert(RelationVariable var, RelationExpression relation);

  void addIndex(RelationVariable var, String name, Index.Type type, Collection<String> attributes);

  void addIndex(RelationVariable var, String name, Index.Type type, String... attributes);

  void clear(RelationVariable var);

  DoubleVariable createDoubleVariable(DoubleExpression expr);

  IntVariable createIntVariable(IntExpression expr);

  IntVariable createIntVariable();

  TupleVariable createTupleVariable(TupleExpression expr);

  ArrayVariable createArrayVariable(Type instanceType);

  ArrayVariable createArrayVariable(Type instanceType, int size);

  ArrayVariable createDoubleArrayVariable(int size);

  ArrayVariable createArrayVariable(ArrayExpression expr);

  RelationVariable createRelationVariable(RelationExpression expr);

  RelationVariable createRelationVariable(Heading heading);

  CategoricalVariable createCategoricalVariable(CategoricalExpression categorical);

  BoolVariable createBoolVariable();

  BoolVariable createBoolVariable(BoolExpression expr);

  Variable createVariable(Type type);

  void append(ArrayVariable arrayVariable, ArrayExpression expression);

  void update(RelationVariable relationVariable, BoolExpression where, List<AttributeAssign> assigns);

  void update(RelationVariable relationVariable, List<AttributeAssign> assigns);

  void update(RelationVariable relationVariable, AttributeAssign assign);


  BoolValue evaluateBool(BoolExpression expr);

  IntValue evaluateInt(IntExpression expr);

  DoubleValue evaluateDouble(DoubleExpression expr);

  RelationValue evaluateRelation(RelationExpression expr);

  void sparseAdd(ArrayVariable var, RelationExpression sparse, DoubleExpression scale,
                        String indexAttribute, String valueAttribute);

  DoubleVariable createDoubleVariable();

  CategoricalVariable createCategoricalVariable(CategoricalType type);

  void append(ArrayVariable arrayVariable, int howmany, Object constant);

  void add(ArrayVariable arrayVariable, ArrayExpression argument, DoubleExpression scale);

  void scale(ArrayVariable arrayVariable, DoubleExpression scale);


  void add(ArrayVariable arrayVariable, ArrayExpression argument, double scale);

  void scale(ArrayVariable arrayVariable, double scale);
}
