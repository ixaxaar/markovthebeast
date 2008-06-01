package thebeast.nod.util;

import thebeast.nod.value.*;
import thebeast.nod.type.Attribute;

import java.io.PrintStream;

/**
 * @author Sebastian Riedel
 */
public class TabularValuePrinter implements ValueVisitor {

  private PrintStream out;
  private boolean inTuple = false;
  private boolean header = true;
  private boolean cutoffDoubles = true;


  public TabularValuePrinter(PrintStream out, boolean header) {
    this.out = out;
    this.header = header;
  }

  public TabularValuePrinter(PrintStream out) {
    this.out = out;
  }


  public void visitTuple(TupleValue tuple) {
    inTuple = true;
    for (Value value : tuple.values()) {
      value.acceptValueVisitor(this);
    }
    inTuple = false;
  }

  public void visitRelation(RelationValue relation) {
    if (inTuple) {
      out.printf("%-15s ", LineValuePrinter.toString(relation));
    } else {

      if (header) {
        for (Attribute attribute : relation.type().heading().attributes())
          out.printf("%-15s ", attribute.name());
        out.println();
        out.println();

      }
//        for (int i = 0; i < relation.type().heading().attributes().size() * 15;++i)
//            out.print("=");
      for (TupleValue tupleValue : relation) {
        tupleValue.acceptValueVisitor(this);
        out.println();
      }
    }
  }

  public void visitCategorical(CategoricalValue categoricalValue) {
    out.printf("%-15s ", categoricalValue.representation());
  }

  public void visitArray(ArrayValue arrayValue) {
    out.printf("%-15s ", LineValuePrinter.toString(arrayValue));
  }

  public void visitInt(IntValue intValue) {
    out.printf("%-15d ", intValue.getInt());
  }

  public void visitDouble(DoubleValue doubleValue) {
    if (cutoffDoubles) out.printf("%-15f ", doubleValue.getDouble());
    else out.print(doubleValue.getDouble());
  }

  public void visitBool(BoolValue boolValue) {
    out.printf("%-6s ", boolValue.getBool());
  }


}
