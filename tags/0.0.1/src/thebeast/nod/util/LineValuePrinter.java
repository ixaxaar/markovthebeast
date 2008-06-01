package thebeast.nod.util;

import thebeast.nod.value.*;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Sebastian Riedel
 */
public class LineValuePrinter implements ValueVisitor {

  private PrintStream out;


  public LineValuePrinter(PrintStream out) {
    this.out = out;
  }

  public static String toString(Value value){
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(os);
    value.acceptValueVisitor(new LineValuePrinter(out));
    return os.toString();
  }

  public void visitTuple(TupleValue tuple) {
    out.print("(");
    int index = 0;
    for (Value value : tuple.values()) {
      if (index++ > 0) out.print(",");
      value.acceptValueVisitor(this);
    }
    out.print(")");
  }

  public void visitRelation(RelationValue relation) {
    int index = 0;
    out.print("{");
    for (TupleValue tupleValue : relation) {
      if (index++ > 0) out.print(",");
      tupleValue.acceptValueVisitor(this);
    }
    out.print("}");
  }

  public void visitCategorical(CategoricalValue categoricalValue) {
    out.print(categoricalValue.representation());
  }

  public void visitArray(ArrayValue arrayValue) {
    int index = 0;
    out.print("[");
    for (Value tupleValue : arrayValue) {
      if (index++ > 0) out.print(",");
      tupleValue.acceptValueVisitor(this);
    }
    out.print("]");

  }

  public void visitInt(IntValue intValue) {
    out.print(intValue.getInt());
  }

  public void visitDouble(DoubleValue doubleValue) {
    out.print(doubleValue.getDouble());

  }

  public void visitBool(BoolValue boolValue) {
    out.print(boolValue.getBool());
  }

}
