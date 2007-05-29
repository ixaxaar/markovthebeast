package thebeast.nod.test;

import junit.framework.TestCase;
import thebeast.nod.NoDServer;
import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.IntVariable;
import thebeast.nod.variable.DoubleVariable;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.statement.Interpreter;
import thebeast.nodmem.MemNoDServer;

import java.io.IOException;
import java.io.File;

/**
 * @author Sebastian Riedel
 */
public class TestFileDumping extends TestCase {

  private NoDServer server = new MemNoDServer();
  private Interpreter interpreter = server.interpreter();
  private ExpressionBuilder builder = server.expressionBuilder();

  public void testSimpleDump() throws IOException {
    builder.id("a").num(1).id("b").num(1.0).tupleForIds();
    builder.id("a").num(2).id("b").num(1.0).tupleForIds();
    builder.id("a").num(3).id("b").num(1.0).tupleForIds();
    builder.id("a").num(4).id("b").num(1.0).tupleForIds();
    builder.id("a").num(5).id("b").num(1.0).tupleForIds();
    builder.id("a").num(6).id("b").num(1.0).tupleForIds();
    RelationVariable var = interpreter.createRelationVariable(builder.relation().getRelation());
    assertTrue(var.contains(1, 1.0));
    File file = new File("dmp");
    file.delete();
    FileSink fileSink = server.createSink(file, 1024);
    FileSource fileSource = server.createSource(file, 1024);
    fileSink.write(var);
    fileSink.flush();
    interpreter.clear(var);
    assertFalse(var.contains(1, 1.0));
    fileSource.read(var);
    assertEquals(6, var.value().size());
    assertTrue(var.contains(1, 1.0));
    file.delete();
  }

  public void testDumpNumber() throws IOException {
    IntVariable var1 = interpreter.createIntVariable(builder.num(1).getInt());
    DoubleVariable var2 = interpreter.createDoubleVariable(builder.num(1.0).getDouble());
    File file = new File("dmp");
    file.delete();
    FileSink fileSink = server.createSink(file, 1024);
    FileSource fileSource = server.createSource(file, 1024);
    fileSink.write(var1);
    fileSink.write(var2);
    fileSink.flush();
    interpreter.assign(var1, builder.num(10).getInt());
    interpreter.assign(var2, builder.num(10.0).getDouble());
    fileSource.read(var1);
    fileSource.read(var2);
    assertEquals(1, var1.value().getInt());
    assertEquals(1.0, var2.value().getDouble());

    file.delete();

  }

  public void testDumpWithIndices() throws IOException {
    builder.id("a").num(1).id("b").num(1.0).tupleForIds();
    builder.id("a").num(2).id("b").num(2.0).tupleForIds();
    builder.id("a").num(3).id("b").num(3.0).tupleForIds();
    builder.id("a").num(1).id("b").num(4.0).tupleForIds();
    builder.id("a").num(2).id("b").num(5.0).tupleForIds();
    builder.id("a").num(3).id("b").num(6.0).tupleForIds();
    RelationVariable var = interpreter.createRelationVariable(builder.relation().getRelation());
    interpreter.addIndex(var, "index", Index.Type.HASH, "a");
    builder.expr(var).from("var1").expr(var).from("var2");
    builder.intAttribute("var1", "a").intAttribute("var2", "a").equality().where();
    builder.id("b1").doubleAttribute("var1", "b").id("b2").doubleAttribute("var2", "b").tupleForIds().select();
    RelationExpression query = builder.query().getRelation();
    RelationVariable result = interpreter.createRelationVariable(query);
    //System.out.println(result.value());
    assertEquals(12, result.value().size());
    assertTrue(result.contains(2.0, 5.0));
    File file = new File("tmp");
    file.delete();
    FileSink fileSink = server.createSink(file, 1024);
    FileSource fileSource = server.createSource(file, 1024);
    fileSink.write(var,false);
    fileSink.write(var,true);
    fileSink.flush();

    interpreter.clear(var);
    interpreter.assign(result, query);
    assertFalse(result.contains(2.0, 5.0));

    fileSource.read(var);
    interpreter.assign(result, query);
    assertEquals(12, result.value().size());
    assertTrue(result.contains(2.0, 5.0));

    fileSource.read(var);
    System.out.println(var.value());
    interpreter.assign(result, query);
    System.out.println(result.value());
    assertEquals(12, result.value().size());
    assertTrue(result.contains(2.0, 5.0));
    file.delete();
  }

  public void testDumpNestedRelation() throws IOException {
    int size = 10;
    for (int i = 0; i < size; ++i) {
      builder.id("a").num(i).id("rel");
      for (int j = 0; j < 100; ++j) builder.id("value").num(j).tuple(1);
      builder.relation(100).tuple(2);
    }
    builder.relation(size);
    RelationVariable var = interpreter.createRelationVariable(builder.getRelation());
    File file = new File("tmp");
    file.delete();
    FileSink fileSink = server.createSink(file, 1);
    FileSource fileSource = server.createSource(file, 1);
    fileSink.write(var);
    fileSink.flush();
    fileSource.read(var);
    System.out.println(var.value());
    file.delete();

  }

}
