package thebeast.nod.test;

import thebeast.nod.expression.*;
import thebeast.nod.value.ArrayValue;
import thebeast.nod.value.IntValue;
import thebeast.nod.value.RelationValue;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.*;
import thebeast.nod.type.IntType;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.Heading;
import thebeast.nod.statement.RelationAppend;
import thebeast.nod.util.TypeBuilder;

import java.util.HashSet;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public class TestInterpreter extends NoDTest {

  public void testAssignIntVariable() {
    exprBuilder.integer(1);
    IntVariable var = interpreter.createIntVariable(exprBuilder.getInt());
    assertEquals(1, var.value().getInt());
    exprBuilder.integer(2);
    interpreter.assign(var, exprBuilder.getInt());
    assertEquals(2, var.value().getInt());
  }

  public void testDoubleVariable() {
    DoubleVariable var = interpreter.createDoubleVariable(exprBuilder.doubleValue(1.5).getDouble());
    assertEquals(1.5, var.value().getDouble());

  }

  public void testDoubleArithmetics() {
    exprBuilder.num(5).doubleCast().num(2.0).doubleTimes().num(10.0).doubleAdd();
    assertEquals(20.0, interpreter.evaluateDouble(exprBuilder.getDouble()).getDouble());
  }

  public void testDoubleAbs() {
    exprBuilder.num(5).doubleCast().num(2.0).doubleTimes().num(-30.0).doubleAdd().doubleAbs();
    assertEquals(20.0, interpreter.evaluateDouble(exprBuilder.getDouble()).getDouble());
  }


  public void testBoolVariable() {
    BoolVariable var = interpreter.createBoolVariable(exprBuilder.bool(true).getBool());
    assertEquals(true, var.value().getBool());
  }

  public void testLoadRelation() throws IOException {
    exprBuilder.id("a").num(1).id("b").num(2.0).tupleForIds().relation(1);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    String file = "" +
            "12 0.4\n" +
            "3 -12.3\n" +
            "32 13.2\n";
    interpreter.load(var, new ByteArrayInputStream(file.getBytes()));
    System.out.println(var.value());
    assertEquals(3, var.value().size());
    assertTrue(var.contains(12, 0.4));
    assertTrue(var.contains(3, -12.3));
    assertTrue(var.contains(32, 13.2));
  }

  public void testAnd() {
    exprBuilder.bool(true).bool(true).bool(true).and(3);
    BoolVariable var = interpreter.createBoolVariable(exprBuilder.getBool());
    assertEquals(true, var.value().getBool());
    interpreter.assign(var, exprBuilder.bool(true).bool(false).bool(true).and(3).getBool());
    assertEquals(false, var.value().getBool());
  }

  public void testIntBins() {
    assertEquals(0, interpreter.evaluateInt(exprBuilder.num(2).bins(3, 6, 9).getInt()).getInt());
    assertEquals(1, interpreter.evaluateInt(exprBuilder.num(4).bins(3, 6, 9).getInt()).getInt());
    assertEquals(1, interpreter.evaluateInt(exprBuilder.num(6).bins(3, 6, 9).getInt()).getInt());
    assertEquals(2, interpreter.evaluateInt(exprBuilder.num(9).bins(3, 6, 9).getInt()).getInt());
    assertEquals(3, interpreter.evaluateInt(exprBuilder.num(20).bins(3, 6, 9).getInt()).getInt());
  }

  public void testPostInc() {
    IntVariable sequence = interpreter.createIntVariable(exprBuilder.integer(0).getInt());
    IntExpression postInc = exprBuilder.expr(sequence).intPostInc().getInt();
    IntVariable var = interpreter.createIntVariable(postInc);
    assertEquals(1, sequence.value().getInt());
    assertEquals(0, var.value().getInt());
    interpreter.assign(var, postInc);
    assertEquals(2, sequence.value().getInt());
  }

  public void testGetColumn() {
    exprBuilder.id("arg1").num(5).id("arg2").num(5.0).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(2).id("arg2").num(2.0).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(4).id("arg2").num(4.0).id("index").num(3).tupleForIds();
    exprBuilder.relation(3);

    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    int[] arg1 = var.getIntColumn("arg1");
    double[] arg2 = var.getDoubleColumn("arg2");
    int[] index = var.getIntColumn("index");

    System.out.println(Arrays.toString(arg1));
    System.out.println(Arrays.toString(arg2));
    System.out.println(Arrays.toString(index));

    assertEquals(5, arg1[0]);
    assertEquals(2, arg1[1]);
    assertEquals(4, arg1[2]);
    assertEquals(5.0, arg2[0]);
    assertEquals(2.0, arg2[1]);
    assertEquals(4.0, arg2[2]);
    assertEquals(1, index[0]);
    assertEquals(2, index[1]);
    assertEquals(3, index[2]);


  }

  public void testAddTuple() {
    exprBuilder.id("arg1").num(5).id("arg2").num(5.0).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(2).id("arg2").num(2.0).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(4).id("arg2").num(4.0).id("index").num(3).tupleForIds();
    exprBuilder.relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());
    var1.addTuple(100, 1.0, 30);
    assertEquals(4, var1.value().size());
    assertTrue(var1.contains(100, 1.0, 30));
    assertFalse(var1.contains(400, 1.0, 30));

    exprBuilder.id("arg1").num(5).id("arg2").id("index").num(1).tuple(1).relation(1).id("arg3").num(1).tupleForIds();
    exprBuilder.relation(1);
    RelationVariable var2 = interpreter.createRelationVariable(exprBuilder.getRelation());
    System.out.println(var2.value());
    var2.addTuple(5, new Object[]{new Object[]{1}, new Object[]{2}}, 1);
    System.out.println(var2.value());
    assertEquals(2, var2.value().size());
    assertTrue(var2.contains(5, new Object[]{new Object[]{1}, new Object[]{2}}, 1));
  }

  public void testIntAssigning() {
    IntVariable var1 = interpreter.createIntVariable(exprBuilder.integer(10).getInt());
    IntVariable var2 = interpreter.createIntVariable(exprBuilder.integer(20).getInt());
    IntVariable var3 = interpreter.createIntVariable(exprBuilder.integer(30).getInt());

    interpreter.assign(var1, var2);
    interpreter.assign(var3, exprBuilder.expr(var2).intPostInc().getInt());

    assertEquals(20, var1.value().getInt());
    assertEquals(21, var2.value().getInt());
    assertEquals(20, var3.value().getInt());
  }

  public void testRelationAssigning() {
    exprBuilder.id("arg1").num(5).id("arg2").num(5).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(2).id("arg2").num(2).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(4).id("arg2").num(4).id("index").num(3).tupleForIds();
    exprBuilder.relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());
    RelationVariable var2 = interpreter.createRelationVariable(var1);
    interpreter.clear(var1);
    assertEquals(0, var1.value().size());
    assertEquals(3, var2.value().size());
    interpreter.assign(var1, var2);
    exprBuilder.id("arg1").num(40).id("arg2").num(40).id("index").num(30).tupleForIds().relation(1);
    interpreter.insert(var2, exprBuilder.getRelation());
    assertEquals(4, var2.value().size());
    assertEquals(3, var1.value().size());
    RelationVariable var3 = interpreter.createRelationVariable(var2);
    interpreter.assign(var2, var1);
    assertEquals(4, var3.value().size());

    RelationVariable var4 = interpreter.createRelationVariable(var3);

    //lets var4 = var1 and change var4
    interpreter.assign(var4, var1);
    exprBuilder.id("arg1").num(1).id("arg2").num(1).id("index").num(4).tupleForIds();
    exprBuilder.id("arg1").num(1).id("arg2").num(2).id("index").num(5).tupleForIds();
    exprBuilder.id("arg1").num(1).id("arg2").num(3).id("index").num(6).tupleForIds();
    exprBuilder.relation(3);

    RelationExpression relation = exprBuilder.getRelation();
    interpreter.assign(var4, relation);
    //var1 should be the same
    assertTrue(var1.contains(5, 5, 1));
    assertTrue(var1.contains(2, 2, 2));
    assertTrue(var1.contains(4, 4, 3));
    //var 4 should as defined
    assertTrue(var4.contains(1, 1, 4));
    assertTrue(var4.contains(1, 2, 5));
    assertTrue(var4.contains(1, 3, 6));

    //lets var4 = var1 and change var1
    interpreter.assign(var4, var1);
    interpreter.assign(var1, relation);
    //var1 should have changed
    assertTrue(var1.contains(1, 1, 4));
    assertTrue(var1.contains(1, 2, 5));
    assertTrue(var1.contains(1, 3, 6));
    //var 4 should remain the same
    assertTrue(var4.contains(5, 5, 1));
    assertTrue(var4.contains(2, 2, 2));
    assertTrue(var4.contains(4, 4, 3));

    System.out.println(var4.value());
    System.out.println(var1.value());

  }

  public void testTupleFrom() {
    exprBuilder.integer(1).integer(2).tuple(2).relation(1).tupleFrom();
    TupleVariable var = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(1, var.value().intElement(0).getInt());
    assertEquals(2, var.value().intElement(1).getInt());
  }

  public void testAssignCategoricalVariable() {
    exprBuilder.categorical("Label", "NP");
    CategoricalVariable var = interpreter.createCategoricalVariable(exprBuilder.getCategorical());
    assertEquals("NP", var.value().representation());
  }

  public void testAllConstants() {
    RelationValue rel = interpreter.evaluateRelation(exprBuilder.allConstants(wordType).getRelation());
    assertEquals(4, rel.size());
    HashSet<String> names = new HashSet<String>();
    for (TupleValue tuple : rel)
      names.add(tuple.categoricalElement(0).representation());
    assertTrue(names.contains("man"));
    assertTrue(names.contains("likes"));
    assertTrue(names.contains("the"));
    assertTrue(names.contains("boat"));
  }

  public void testTupleSelector() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    TupleVariable var = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(1, ((IntValue) var.value().element(0)).getInt());
    assertEquals(2, ((IntValue) var.value().element(1)).getInt());
    assertEquals(3, ((IntValue) var.value().element(2)).getInt());
  }

  public void testIntComponentExtractor() {
    exprBuilder.id("a").integer(1).id("b").integer(2).tuple(2).intExtractComponent("b");
    IntVariable var = interpreter.createIntVariable(exprBuilder.getInt());
    assertEquals(2, var.value().getInt());
  }

  public void testArrayCreator() {
    ArrayVariable var = interpreter.createArrayVariable(typeFactory.intType());
    exprBuilder.integer(1).integer(2).integer(3).integer(1).array(4);
    interpreter.assign(var, exprBuilder.getArray());
    ArrayValue array = var.value();
    assertEquals(4, array.size());
    assertEquals(1, array.intElement(0).getInt());
    assertEquals(2, array.intElement(1).getInt());
    assertEquals(3, array.intElement(2).getInt());
    assertEquals(1, array.intElement(3).getInt());
  }


  public void testArrayClear() {
    ArrayVariable var = interpreter.createArrayVariable(typeFactory.intType());
    exprBuilder.integer(1).integer(2).integer(3).integer(1).array(4);
    interpreter.assign(var, exprBuilder.getArray());
    interpreter.clear(var);
    ArrayValue array = var.value();
    assertEquals(0, array.size());
  }

  public void testIndexedSum() {
    exprBuilder.doubleValue(1.0).doubleValue(2.0).doubleValue(3.0).doubleValue(5.0).array(4);
    ArrayVariable array = interpreter.createArrayVariable(exprBuilder.getArray());
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable indexRelation = interpreter.createRelationVariable(exprBuilder.getRelation());
    exprBuilder.expr(array).expr(indexRelation).indexedSum("c");
    DoubleVariable result = interpreter.createDoubleVariable(exprBuilder.getDouble());
    assertEquals(10.0, result.value().getDouble());
  }

  public void testArraySparseAdd() {
    exprBuilder.doubleValue(1.0).doubleValue(2.0).doubleValue(3.0).doubleValue(5.0).array(4);
    ArrayVariable array = interpreter.createArrayVariable(exprBuilder.getArray());
    exprBuilder.id("index").num(0).id("value").num(4.5).tupleForIds();
    exprBuilder.id("index").num(3).id("value").num(2.5).tupleForIds();
    exprBuilder.relation(2);
    RelationVariable sparse = interpreter.createRelationVariable(exprBuilder.getRelation());
    interpreter.sparseAdd(array, sparse, exprBuilder.num(2.0).getDouble(), "index", "value");
    System.out.println(array.value());
    assertEquals(10.0, array.value().doubleElement(0).getDouble());
    assertEquals(2.0, array.value().doubleElement(1).getDouble());
    assertEquals(3.0, array.value().doubleElement(2).getDouble());
    assertEquals(10.0, array.value().doubleElement(3).getDouble());


  }

  public void testScaledIndexedSum() {
    exprBuilder.doubleValue(1.0).doubleValue(2.0).doubleValue(3.0).doubleValue(5.0).array(4);
    ArrayVariable array = interpreter.createArrayVariable(exprBuilder.getArray());
    exprBuilder.id("a").integer(1).id("b").integer(2).id("scale").num(3.0).tuple(3);
    exprBuilder.id("a").integer(1).id("b").integer(2).id("scale").num(2.0).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("scale").num(1.0).tuple(3);
    exprBuilder.relation(3);
    RelationVariable indexRelation = interpreter.createRelationVariable(exprBuilder.getRelation());
    exprBuilder.expr(array).expr(indexRelation).indexedSum("a", "scale");
    DoubleVariable result = interpreter.createDoubleVariable(exprBuilder.getDouble());
    assertEquals(15.0, result.value().getDouble());
  }


  public void testGet() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(5).id("b").integer(2).id("c").integer(20).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable relation = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(5).id("b").integer(2).tupleForIds().id("c").integer(9).tupleForIds().getPut();
    TupleVariable t1 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(20, t1.value().intElement(0).getInt());

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(50).id("b").integer(2).tupleForIds().id("c").integer(9).tupleForIds().getPut();
    TupleVariable t2 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(9, t2.value().intElement(0).getInt());

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(50).id("b").integer(2).tupleForIds().id("c").integer(15).tupleForIds().getPut();
    TupleVariable t3 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(9, t3.value().intElement(0).getInt());
    assertEquals(4, relation.value().size());

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(3).id("b").integer(2).tupleForIds().id("c").integer(15).tupleForIds().getPut();
    TupleVariable t4 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(1, t4.value().intElement(0).getInt());


  }


  public void testInsertWithCounting(){
    TypeBuilder typeBuilder = new TypeBuilder(server);
    typeBuilder.intType().att("arg0").catType("B","x","y","z").att("arg1").intType().att("count").relationType(3);
    Heading heading = typeBuilder.buildRelationType().heading();
    RelationVariable var = interpreter.createRelationVariable(heading, "count");
    var.addTuple(1,"x",1);
    var.addTuple(2,"x",2);
    var.addTuple(3,"x",3);
    System.out.println(var.value());
    RelationVariable src = interpreter.createRelationVariable(heading);
    src.addTuple(1,"x",1);
    src.addTuple(2,"x",2);
    src.addTuple(3,"x",3);
    src.addTuple(4,"x",3);

    interpreter.insert(var,src);
    assertEquals(4,var.value().size());
    assertTrue(var.contains(1,"x",2));
    assertTrue(var.contains(2,"x",4));
    assertTrue(var.contains(3,"x",6));
    assertTrue(var.contains(4,"x",3));

    interpreter.insert(var,src);
    assertEquals(4,var.value().size());
    assertTrue(var.contains(1,"x",3));
    assertTrue(var.contains(2,"x",6));
    assertTrue(var.contains(3,"x",9));
    assertTrue(var.contains(4,"x",6));

    interpreter.insert(var,var);
    assertEquals(4,var.value().size());
    assertTrue(var.contains(1,"x",6));
    assertTrue(var.contains(2,"x",12));
    assertTrue(var.contains(3,"x",18));
    assertTrue(var.contains(4,"x",12));

  }

  public void testGetWithIndex() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(5).id("b").integer(2).id("c").integer(20).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable relation = interpreter.createRelationVariable(exprBuilder.getRelation());
    interpreter.addIndex(relation, "index", Index.Type.HASH, "a");

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(5).id("b").integer(2).tupleForIds().id("c").integer(9).tupleForIds().getPut();
    TupleVariable t1 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(20, t1.value().intElement(0).getInt());

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(50).id("b").integer(2).tupleForIds().id("c").integer(9).tupleForIds().getPut();
    TupleVariable t2 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(9, t2.value().intElement(0).getInt());
    interpreter.addIndex(relation, "index2", Index.Type.HASH, "a", "b");

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(50).id("b").integer(2).tupleForIds().id("c").integer(15).tupleForIds().getPut();
    TupleVariable t3 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(9, t3.value().intElement(0).getInt());
    assertEquals(4, relation.value().size());

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(3).id("b").integer(2).tupleForIds().id("c").integer(15).tupleForIds().getPut();
    TupleVariable t4 = interpreter.createTupleVariable(exprBuilder.getTuple());
    assertEquals(1, t4.value().intElement(0).getInt());

  }

  public void testGetTwice() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(5).id("b").integer(2).id("c").integer(20).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable relation = interpreter.createRelationVariable(exprBuilder.getRelation());
    interpreter.addIndex(relation, "index", Index.Type.HASH, "a");

    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(99).id("b").integer(99).tupleForIds().id("c").integer(99).tupleForIds().getPut();
    exprBuilder.expr(relation);
    exprBuilder.id("a").integer(99).id("b").integer(99).tupleForIds().id("c").integer(50).tupleForIds().getPut();
    exprBuilder.relation(2);
    RelationVariable rel = interpreter.createRelationVariable(exprBuilder.getRelation());
    System.out.println(rel.value());
    assertTrue(rel.contains(50));
    assertEquals(1, rel.value().size());

  }

  public void testArrayAccess() {
    exprBuilder.integer(1).integer(2).integer(3).integer(1).array(4).integer(1).intArrayElement();
    IntVariable var1 = interpreter.createIntVariable(exprBuilder.getInt());
    assertEquals(2, var1.value().getInt());
    exprBuilder.clear();
    exprBuilder.doubleValue(1.5).doubleValue(2.5).array(2).integer(1).doubleArrayElement();
    DoubleVariable var = interpreter.createDoubleVariable(exprBuilder.getDouble());
    assertEquals(2.5, var.value().getDouble());
  }

  public void testArrayAdd() {
    ArrayVariable var = interpreter.createArrayVariable(typeFactory.doubleType(), 4);
    exprBuilder.num(1.0).num(2.0).num(3.0).num(4.0).array(4);
    ArrayExpression argument = exprBuilder.getArray();
    interpreter.add(var, argument, exprBuilder.num(1.0).getDouble());
    ArrayValue array = var.value();
    assertEquals(1.0, array.doubleElement(0).getDouble());
    assertEquals(2.0, array.doubleElement(1).getDouble());
    assertEquals(3.0, array.doubleElement(2).getDouble());
    assertEquals(4.0, array.doubleElement(3).getDouble());
    System.out.println(var.value());
    interpreter.add(var, argument, exprBuilder.num(2.0).getDouble());
    System.out.println(var.value());
    assertEquals(3.0, array.doubleElement(0).getDouble());
    assertEquals(6.0, array.doubleElement(1).getDouble());
    assertEquals(9.0, array.doubleElement(2).getDouble());
    assertEquals(12.0, array.doubleElement(3).getDouble());

  }

  public void testArrayAppend() {
    ArrayVariable var = interpreter.createArrayVariable(typeFactory.intType());
    exprBuilder.integer(1).integer(2).integer(3).integer(1).array(4);
    ArrayExpression arrayExpression = exprBuilder.getArray();
    interpreter.assign(var, arrayExpression);
    interpreter.append(var, arrayExpression);
    ArrayValue array = var.value();
    assertEquals(8, array.size());
    assertEquals(1, array.intElement(0).getInt());
    assertEquals(2, array.intElement(1).getInt());
    assertEquals(3, array.intElement(2).getInt());
    assertEquals(1, array.intElement(3).getInt());
    assertEquals(1, array.intElement(4).getInt());
    assertEquals(2, array.intElement(5).getInt());
    assertEquals(3, array.intElement(6).getInt());
    assertEquals(1, array.intElement(7).getInt());

    interpreter.append(var, 8, 0);

    assertEquals(16, array.size());
    for (int i = 8; i < 16; ++i)
      assertEquals(0, array.intElement(i).getInt());

  }

  public void testRestrict() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    exprBuilder.expr(var).intAttribute("a").integer(1).equality().restrict();
    RelationVariable result = interpreter.createRelationVariable(exprBuilder.getRelation());
    assertEquals(2, result.value().size());

    exprBuilder.clear().expr(result).id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3).contains();
    BoolVariable contains = interpreter.createBoolVariable(exprBuilder.getBool());
    assertTrue(contains.value().getBool());

    exprBuilder.clear().expr(result).id("a").integer(1).id("b").integer(2).id("c").integer(2).tuple(3).contains();
    interpreter.assign(contains, exprBuilder.getBool());
    assertTrue(contains.value().getBool());

    exprBuilder.clear().expr(result).id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3).contains();
    interpreter.assign(contains, exprBuilder.getBool());
    assertFalse(contains.value().getBool());

  }

  public void testInequality() {
    assertTrue(interpreter.evaluateBool(exprBuilder.num(5).num(6).inequality().getBool()).getBool());
    assertFalse(interpreter.evaluateBool(exprBuilder.num(5).num(1).intAdd().num(6).inequality().getBool()).getBool());
  }

  public void testGroup() {
    exprBuilder.id("a").integer(1).id("b").doubleValue(1.0).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(1).id("b").doubleValue(2.0).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").doubleValue(3.0).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.expr(var).group("bc", "b", "c");
    RelationVariable result = interpreter.createRelationVariable(exprBuilder.getRelation());

    assertEquals(2, result.value().size());
    for (TupleValue tuple : result.value()) {
      if (tuple.intElement(0).getInt() == 1) {
        assertEquals(2, tuple.relationElement(1).size());
      } else if (tuple.intElement(0).getInt() == 3) {
        assertEquals(1, tuple.relationElement(1).size());
        TupleValue tupleValue = tuple.relationElement(1).tuples().next();
        assertEquals(3.0, tupleValue.doubleElement(0).getDouble());
        assertEquals(1, tupleValue.intElement(1).getInt());
      }
    }
  }

  public void testSummarize() {
    exprBuilder.id("index").num(0).id("value").num(0.0).tupleForIds();
    exprBuilder.id("index").num(2).id("value").num(2.0).tupleForIds();
    exprBuilder.id("index").num(4).id("value").num(4.0).tupleForIds();
    exprBuilder.id("index").num(2).id("value").num(6.0).tupleForIds();
    exprBuilder.id("index").num(2).id("value").num(5.0).tupleForIds();
    exprBuilder.id("index").num(4).id("value").num(6.0).tupleForIds();
    RelationVariable rel = interpreter.createRelationVariable(exprBuilder.relation(6).getRelation());

    exprBuilder.expr(rel).by("index").doubleAttribute("value").summarizeAs("sum", Summarize.Spec.DOUBLE_SUM);
    RelationVariable sum = interpreter.createRelationVariable(exprBuilder.summarize().getRelation());
    System.out.println(sum.value());

    assertEquals(3, sum.value().size());
    assertTrue(sum.contains(0, 0.0));
    assertTrue(sum.contains(2, 13.0));
    assertTrue(sum.contains(4, 10.0));

    exprBuilder.id("index").num(0).id("value").num(1.0).tupleForIds();
    exprBuilder.id("index").num(2).id("value").num(1.0).tupleForIds();
    exprBuilder.id("index").num(4).id("value").num(1.0).tupleForIds();
    exprBuilder.id("index").num(2).id("value").num(2.0).tupleForIds();
    exprBuilder.id("index").num(2).id("value").num(3.0).tupleForIds();
    exprBuilder.id("index").num(4).id("value").num(2.0).tupleForIds();
    interpreter.assign(rel, exprBuilder.relation(6).getRelation());

    exprBuilder.expr(rel).by("index").doubleAttribute("value").summarizeAs("sum", Summarize.Spec.DOUBLE_SUM);
    interpreter.assign(sum, exprBuilder.summarize().getRelation());

    System.out.println(sum.value());


    assertEquals(3, sum.value().size());
    assertTrue(sum.contains(0, 1.0));
    assertTrue(sum.contains(2, 6.0));
    assertTrue(sum.contains(4, 3.0));


  }

  public void testSparseAdd() {
    exprBuilder.id("index").num(0).id("value").num(0.0).tupleForIds();
    exprBuilder.id("index").num(2).id("value").num(2.0).tupleForIds();
    exprBuilder.id("index").num(4).id("value").num(4.0).tupleForIds();
    exprBuilder.id("index").num(6).id("value").num(6.0).tupleForIds();
    exprBuilder.relation(4);

    RelationVariable vector1 = interpreter.createRelationVariable(exprBuilder.getRelation());
    interpreter.addIndex(vector1, "index", Index.Type.HASH, "index");

    exprBuilder.id("index").num(1).id("value").num(1.0).tupleForIds();
    exprBuilder.id("index").num(3).id("value").num(3.0).tupleForIds();
    exprBuilder.id("index").num(4).id("value").num(4.0).tupleForIds();
    exprBuilder.id("index").num(6).id("value").num(6.0).tupleForIds();
    exprBuilder.relation(4);

    RelationVariable vector2 = interpreter.createRelationVariable(exprBuilder.getRelation());
    interpreter.addIndex(vector2, "index", Index.Type.HASH, "index");

    exprBuilder.expr(vector1).num(2.0).expr(vector2).sparseAdd("index", "value");

    RelationVariable sum = interpreter.createRelationVariable(exprBuilder.getRelation());
    assertEquals(6, sum.value().size());
    assertTrue(sum.contains(0, 0.0));
    assertTrue(sum.contains(1, 2.0));
    assertTrue(sum.contains(2, 2.0));
    assertTrue(sum.contains(3, 6.0));
    assertTrue(sum.contains(4, 12.0));
    assertTrue(sum.contains(6, 18.0));

    System.out.println(sum.value());

  }


  public void testCycles() {
    exprBuilder.id("arg1").num(5).id("arg2").num(6).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(6).id("arg2").num(20).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(20).id("arg2").num(5).id("index").num(3).tupleForIds();
    exprBuilder.id("arg1").num(50).id("arg2").num(60).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(60).id("arg2").num(200).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(200).id("arg2").num(50).id("index").num(3).tupleForIds();
    exprBuilder.id("arg1").num(1).id("arg2").num(1).id("index").num(3).tupleForIds();
    exprBuilder.relation(7);
    RelationVariable graph = interpreter.createRelationVariable(exprBuilder.getRelation());
    graph.setLabel("graph");
    RelationExpression cyclesExpr = exprBuilder.expr(graph).cycles("arg1", "arg2").getRelation();
    RelationVariable cycles = interpreter.createRelationVariable(cyclesExpr);
    System.out.println(cycles.value());
    assertEquals(3, cycles.value().size());
  }

  public void testCount() {
    exprBuilder.id("arg1").num(5).id("arg2").num(6).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(6).id("arg2").num(20).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(20).id("arg2").num(5).id("index").num(3).tupleForIds();
    exprBuilder.relation(3).count();
    assertEquals(3, interpreter.evaluateInt(exprBuilder.getInt()).getInt());
  }

  public void testClear() {
    exprBuilder.id("arg1").num(5).id("arg2").num(6).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(6).id("arg2").num(20).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(20).id("arg2").num(5).id("index").num(3).tupleForIds();
    exprBuilder.relation(3);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    interpreter.clear(var);
    assertEquals(0, var.value().size());
  }


  public void testUpdate() {
    exprBuilder.id("a").integer(1).id("b").doubleValue(1.0).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(1).id("b").doubleValue(2.0).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").doubleValue(3.0).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());

    interpreter.update(var, statementFactory.createAttributeAssign("a", exprBuilder.intAttribute("c").getInt()));

    exprBuilder.clear().expr(var).id("a").integer(3).id("b").doubleValue(1.0).id("c").integer(3).tuple(3).contains();
    BoolVariable contains = interpreter.createBoolVariable(exprBuilder.getBool());
    assertTrue(contains.value().getBool());

    exprBuilder.clear().expr(var).id("a").integer(2).id("b").doubleValue(2.0).id("c").integer(2).tuple(3).contains();
    interpreter.assign(contains, exprBuilder.getBool());
    assertTrue(contains.value().getBool());

    exprBuilder.clear().expr(var).id("a").integer(1).id("b").doubleValue(3.0).id("c").integer(1).tuple(3).contains();
    interpreter.assign(contains, exprBuilder.getBool());
    assertTrue(contains.value().getBool());

  }

  public void testOperationInvocation() {
    IntVariable x = interpreter.createIntVariable();
    IntVariable y = interpreter.createIntVariable();
    exprBuilder.expr(x).expr(y).intAdd();
    Operator<IntType> add = expressionFactory.createOperator("add", exprBuilder.getInt(), x, y);
    int result = interpreter.evaluateInt(exprBuilder.integer(6).integer(2).invokeIntOp(add).getInt()).getInt();
    assertEquals(8, result);
    System.out.println(result);
  }

  public void testOperationInvocationQuery() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(1).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(4).tuple(3);
    exprBuilder.relation();
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    //SELECT x = var.a, result = (SELECT d=nested.c FROM nested WHERE nested.b = var.a) FROM var
    IntVariable x = interpreter.createIntVariable();
    exprBuilder.expr(var).from("var").intAttribute("var", "b").expr(x).equality().where();
    exprBuilder.id("d").intAttribute("var", "c").tuple(1).select().query();
    Operator<RelationType> search = expressionFactory.createOperator("search", exprBuilder.getRelation(), x);
    exprBuilder.expr(var).from("var").id("result").intAttribute("var", "a").invokeRelOp(search);
    exprBuilder.id("x").intAttribute("var", "a").tuple(2).select().query();

    RelationExpression query = exprBuilder.getRelation();
    RelationVariable result = interpreter.createRelationVariable(query);
    System.out.println(result.value());
    assertEquals(3, result.value().size());
    assertTrue(result.contains(new Object[]{}, 1));
    assertTrue(result.contains(new Object[]{new Object[]{1}, new Object[]{2}}, 2));
    assertTrue(result.contains(new Object[]{new Object[]{3}, new Object[]{4}}, 3));

    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(1).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(4).tuple(3);
    exprBuilder.relation();

    interpreter.assign(var,exprBuilder.getRelation());
    interpreter.assign(result, query);
    System.out.println(result.value());

  }

  public void testRelationSelector() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(4, true);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    assertEquals(3, var.value().size());
    HashSet<Integer> ints = new HashSet<Integer>();
    for (TupleValue tuple : var.value())
      ints.add(((IntValue) tuple.element(0)).getInt());
    assertTrue(ints.contains(1));
    assertTrue(ints.contains(2));
    assertTrue(ints.contains(3));

    exprBuilder.clear().expr(var).id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3).contains();
    BoolVariable contains = interpreter.createBoolVariable(exprBuilder.getBool());
    assertTrue(contains.value().getBool());

    exprBuilder.clear().expr(var).id("a").integer(4).id("b").integer(4).id("c").integer(4).tuple(3).contains();
    interpreter.assign(contains, exprBuilder.getBool());
    assertFalse(contains.value().getBool());


  }

  public void testRelationInsert() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    interpreter.insert(var, exprBuilder.getRelation());
    assertEquals(5, var.value().size());
  }

  public void testRelationAppend() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    RelationExpression expression = exprBuilder.getRelation();
    RelationAppend append = statementFactory.createRelationAppend(var, expression);
    interpreter.interpret(append);
    System.out.println(var.value());
    assertEquals(6, var.value().size());
    interpreter.interpret(append);
    assertEquals(9, var.value().size());
  }


  public void testRelationUnion() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    exprBuilder.union(3);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    System.out.println(var.value());
    assertEquals(5, var.value().size());
    assertTrue(var.contains(1, 2, 3));
    assertTrue(var.contains(2, 2, 2));
    assertTrue(var.contains(3, 2, 1));
    assertTrue(var.contains(1, 1, 1));
    assertTrue(var.contains(3, 3, 3));
  }


  public void testQuery1() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var2 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.
            expr(var1).from("var1").expr(var2).from("var2").
            intAttribute("var1", "a").intAttribute("var2", "b").equality().where().
            id("newA").intAttribute("var1", "a").id("newB").intAttribute("var2", "b").tuple(2).select().
            query();

    RelationVariable var3 = interpreter.createRelationVariable(exprBuilder.getRelation());

    System.out.println(var3.value().size());
    for (TupleValue t : var3.value()) {
      System.out.println(t.element(0) + " " + t.element(1));
      assertEquals(t.element(0), t.element(1));
    }
  }

  public void testMinMax() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var2 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.
            expr(var1).from("var1").expr(var2).from("var2").
            intAttribute("var1", "a").intAttribute("var2", "b").equality().where().
            id("min").intAttribute("var1", "b").intAttribute("var2", "a").intMin().
            id("max").intAttribute("var1", "b").intAttribute("var2", "a").intMax().tuple(2).select().
            query();

    RelationVariable var3 = interpreter.createRelationVariable(exprBuilder.getRelation());
    assertEquals(3,var3.value().size());
    assertTrue(var3.contains(2,1));
    assertTrue(var3.contains(2,2));
    assertTrue(var3.contains(3,2));
    System.out.println(var3.value());

  }

  public void testQuery2() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var2 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.
            expr(var1).from("var1").expr(var2).from("var2").
            intAttribute("var1", "a").intAttribute("var2", "b").equality().
            intAttribute("var1", "a").intAttribute("var1", "c").equality().
            and(2).where().
            id("newA").intAttribute("var1", "a").id("newB").intAttribute("var2", "b").tuple(2).select().
            query();

    RelationVariable var3 = interpreter.createRelationVariable(exprBuilder.getRelation());

    assertEquals(1, var3.value().size());
    assertEquals(var3.value().tuples().next().intElement(0), var3.value().tuples().next().intElement(1));
    System.out.println(var3.value());
  }

  public void testQueryInsert() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var2 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.
            expr(var1).from("var1").expr(var2).from("var2").
            intAttribute("var1", "a").intAttribute("var2", "b").equality().
            intAttribute("var1", "a").intAttribute("var1", "c").equality().
            and(2).where().
            id("newA").intAttribute("var1", "a").id("newB").intAttribute("var2", "b").tuple(2).
            id("newA").intAttribute("var1", "a").num(1).intAdd().id("newB").intAttribute("var2", "b").tuple(2).
            relation(2).
            insert().
            queryInsert();

    RelationVariable var3 = interpreter.createRelationVariable(exprBuilder.getRelation());

    assertEquals(2, var3.value().size());
    //assertEquals(var3.value().tuples().next().intElement(0),var3.value().tuples().next().intElement(1));
    exprBuilder.clear().expr(var3).id("newA").integer(2).id("newB").integer(2).tuple(2).contains();
    assertTrue(interpreter.evaluateBool(exprBuilder.getBool()).getBool());
    exprBuilder.clear().expr(var3).id("newA").integer(3).id("newB").integer(2).tuple(2).contains();
    assertTrue(interpreter.evaluateBool(exprBuilder.getBool()).getBool());

    System.out.println(var3.value());
  }

  public void testGetInQuery() {
    exprBuilder.id("arg1").num(5).id("arg2").num(5).id("index").num(1).tupleForIds();
    exprBuilder.id("arg1").num(2).id("arg2").num(2).id("index").num(2).tupleForIds();
    exprBuilder.id("arg1").num(4).id("arg2").num(4).id("index").num(3).tupleForIds();
    exprBuilder.relation(3);
    RelationVariable index = interpreter.createRelationVariable(exprBuilder.getRelation());
    index.setLabel("index");

    exprBuilder.id("var1").num(5).id("var2").num(2).tupleForIds();
    exprBuilder.id("var1").num(4).id("var2").num(5).tupleForIds();
    exprBuilder.relation(2);
    RelationVariable vars = interpreter.createRelationVariable(exprBuilder.getRelation());

    System.out.println(vars.value());

    exprBuilder.expr(vars).from("vars");
    exprBuilder.id("lb").num(Double.NEGATIVE_INFINITY);
    exprBuilder.id("values").
            id("index").expr(index).
            id("arg1").intAttribute("vars", "var1").id("arg2").intAttribute("vars", "var1").tuple(2).
            id("index").num(99).tuple(1).get().intExtractComponent("index").
            id("weight").num(1.0).tuple(2).
            id("index").expr(index).
            id("arg1").intAttribute("vars", "var2").id("arg2").intAttribute("vars", "var2").tuple(2).
            id("index").num(99).tuple(1).get().intExtractComponent("index").
            id("weight").num(1.0).tuple(2).
            relation(2);
    exprBuilder.id("ub").num(1.0);
    exprBuilder.tuple(3).select();
    exprBuilder.query();

    RelationExpression expr = exprBuilder.getRelation();

    System.out.println(expr);

    RelationVariable var = interpreter.createRelationVariable(expr);

    System.out.println(var.value());

    assertEquals(2, var.value().size());

    exprBuilder.id("lb").num(Double.NEGATIVE_INFINITY).id("values").
            id("index").num(1).id("weight").num(1.0).tuple(2).id("index").num(2).id("weight").num(1.0).tuple(2).
            relation(2).id("ub").num(1.0).tuple(3);

    TupleExpression tuple1 = exprBuilder.getTuple();

    exprBuilder.id("lb").num(Double.NEGATIVE_INFINITY).id("values").
            id("index").num(3).id("weight").num(1.0).tuple(2).id("index").num(1).id("weight").num(1.0).tuple(2).
            relation(2).id("ub").num(1.0).tuple(3);

    TupleExpression tuple2 = exprBuilder.getTuple();

    exprBuilder.id("lb").num(Double.NEGATIVE_INFINITY).id("values").
            id("index").num(100).id("weight").num(1.0).tuple(2).id("index").num(50).id("weight").num(1.0).tuple(2).
            relation(2).id("ub").num(1.0).tuple(3);

    TupleExpression tuple3 = exprBuilder.getTuple();

    assertTrue(interpreter.evaluateBool(exprBuilder.expr(var).expr(tuple1).contains().getBool()).getBool());
    assertTrue(interpreter.evaluateBool(exprBuilder.expr(var).expr(tuple2).contains().getBool()).getBool());
    assertFalse(interpreter.evaluateBool(exprBuilder.expr(var).expr(tuple3).contains().getBool()).getBool());

  }

  public void testRelationMinus() {
    exprBuilder.id("lb").num(Double.NEGATIVE_INFINITY).id("values").
            id("index").num(1).id("weight").num(1.0).tuple(2).id("index").num(2).id("weight").num(1.0).tuple(2).
            relation(2).id("ub").num(1.0).tuple(3);
    TupleVariable t1 = interpreter.createTupleVariable(exprBuilder.getTuple());
    exprBuilder.id("lb").num(0.0).id("values").
            id("index").num(1).id("weight").num(1.0).tuple(2).id("index").num(2).id("weight").num(1.0).tuple(2).
            relation(2).id("ub").num(1.0).tuple(3);
    TupleVariable t2 = interpreter.createTupleVariable(exprBuilder.getTuple());
    exprBuilder.id("lb").num(Double.NEGATIVE_INFINITY).id("values").
            id("index").num(1).id("weight").num(1.0).tuple(2).id("index").num(2).id("weight").num(1.0).tuple(2).
            relation(2).id("ub").num(0.0).tuple(3);
    TupleVariable t3 = interpreter.createTupleVariable(exprBuilder.getTuple());

    exprBuilder.expr(t1).expr(t2).expr(t3).relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.id("lb").num(0.0).id("values").
            id("index").num(1).id("weight").num(1.0).tuple(2).id("index").num(2).id("weight").num(1.0).tuple(2).
            relation(2).id("ub").num(3.0).tuple(3);
    TupleVariable t4 = interpreter.createTupleVariable(exprBuilder.getTuple());

    exprBuilder.expr(t1).expr(t2).expr(t4).relation(3);
    RelationVariable var2 = interpreter.createRelationVariable(exprBuilder.getRelation());

    exprBuilder.expr(var1).expr(var2).relationMinus();
    RelationVariable var1minus2 = interpreter.createRelationVariable(exprBuilder.getRelation());
    System.out.println(var1minus2.value());

    exprBuilder.expr(var2).expr(var1).relationMinus();
    RelationVariable var2minus1 = interpreter.createRelationVariable(exprBuilder.getRelation());
    System.out.println(var2minus1.value());

    assertTrue(interpreter.evaluateBool(
            exprBuilder.expr(var1minus2).tupleFrom().expr(t3).equality().getBool()).getBool());


  }

  public void testIndexCollector() {
    int size = 10;
    for (int i = 0; i < size; ++i) {
      exprBuilder.id("a").num(i).id("rel");
      for (int j = 0; j < 10; ++j) exprBuilder.id("value").num(j).id("scale").num(1.0).tuple(2);
      exprBuilder.relation(10).tuple(2);
    }
    exprBuilder.relation(size);
    RelationVariable var = interpreter.createRelationVariable(exprBuilder.getRelation());
    exprBuilder.expr(var).collect("rel", "index", "value");
    RelationExpression collector = exprBuilder.getRelation();
    RelationVariable collected = interpreter.createRelationVariable(collector);
    System.out.println(collected.value());

    assertEquals(10, collected.value().size());
    for (int i = 0; i < 10; ++i)
      assertTrue(collected.contains(i, 10.0));

    interpreter.assign(collected, collector);
    System.out.println(collected.value());
  }


  public void testIndexedQuery() {
    exprBuilder.id("a").integer(1).id("b").integer(2).id("c").integer(3).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(2).id("c").integer(1).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var1 = interpreter.createRelationVariable(exprBuilder.getRelation());


    exprBuilder.id("a").integer(1).id("b").integer(1).id("c").integer(1).tuple(3);
    exprBuilder.id("a").integer(2).id("b").integer(2).id("c").integer(2).tuple(3);
    exprBuilder.id("a").integer(3).id("b").integer(3).id("c").integer(3).tuple(3);
    exprBuilder.relation(3);
    RelationVariable var2 = interpreter.createRelationVariable(exprBuilder.getRelation());

    interpreter.addIndex(var2, "my_index", Index.Type.HASH, "b");

    exprBuilder.
            expr(var1).from("var1").expr(var2).from("var2").
            intAttribute("var1", "a").intAttribute("var2", "b").equality().where().
            id("newA").intAttribute("var1", "a").id("newB").intAttribute("var2", "b").tuple(2).select().
            query();

    RelationVariable var3 = interpreter.createRelationVariable(exprBuilder.getRelation());

    System.out.println(var3.value().size());
    for (TupleValue t : var3.value()) {
      System.out.println(t.element(0) + " " + t.element(1));
      assertEquals(t.element(0), t.element(1));
    }
  }


}
