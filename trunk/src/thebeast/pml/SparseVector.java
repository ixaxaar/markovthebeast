package thebeast.pml;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.TypeFactory;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.DoubleVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.Dump;

import java.util.LinkedList;
import java.io.IOException;

/**
 * A SparseVector based by a database table with index and value column.
 *
 * @author Sebastian Riedel
 */
public class SparseVector {

  private static Heading heading;
  private static Heading headingIndex;


  static {
    TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
    Attribute index = factory.createAttribute("index", factory.intType());
    Attribute value = factory.createAttribute("value", factory.doubleType());
    LinkedList<Attribute> attributes = new LinkedList<Attribute>();
    attributes.add(index);
    attributes.add(value);
    heading = factory.createHeadingFromAttributes(attributes);

    LinkedList<Attribute> attributesIndex = new LinkedList<Attribute>();
    attributesIndex.add(index);
    headingIndex = factory.createHeadingFromAttributes(attributesIndex);
  }

  private RelationVariable values, otherValues;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private RelationExpression sparseAdd;
  private DoubleVariable scale;
  private ExpressionBuilder builder;


  public SparseVector() {
    values = interpreter.createRelationVariable(heading);
    interpreter.addIndex(values, "index", Index.Type.HASH, "index");
    otherValues = interpreter.createRelationVariable(heading);
    interpreter.addIndex(otherValues, "index", Index.Type.HASH, "index");
    builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    scale = interpreter.createDoubleVariable(builder.num(1.0).getDouble());

    sparseAdd = builder.expr(values).expr(scale).expr(otherValues).sparseAdd("index", "value").getRelation();
    //builder.id("index").
    //distinctOthers = builder.expr(other).expr(values).relationMinus().getRelation();

  }

  public void addValue(int index, double value) {
    values.addTuple(index, value);
  }

  public boolean contains(int index, double value) {
    return values.contains(index, value);
  }

  public SparseVector add(double scale, SparseVector other) {
    SparseVector result = new SparseVector();
    interpreter.assign(this.otherValues, other.values);
    interpreter.assign(this.scale, builder.num(scale).getDouble());
    interpreter.assign(result.values, sparseAdd);
    return result;
  }

  public void addInPlace(double scale, SparseVector other) {
    interpreter.assign(this.otherValues, other.values);
    interpreter.assign(this.scale, builder.num(scale).getDouble());
    interpreter.assign(values, sparseAdd);
  }

  public RelationVariable getValues() {
    return values;
  }

  public void load(SparseVector sparseVector) {
    interpreter.assign(values, sparseVector.values);
  }

  public int size() {
    return values.value().size();
  }

  public int getMemoryUsage() {
    return values.byteSize();
  }

  public SparseVector copy() {
    SparseVector result = new SparseVector();
    result.load(this);
    return result;
  }

  public String toString() {
    return values.value().toString();
  }

  public void write(Dump dump) throws IOException {
    dump.write(values);
  }

  public void read(Dump dump) throws IOException {
    dump.read(values);
  }
}
