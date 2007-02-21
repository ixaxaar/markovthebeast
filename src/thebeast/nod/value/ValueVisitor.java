package thebeast.nod.value;

import thebeast.nod.value.TupleValue;
import thebeast.nod.value.RelationValue;

/**
 * @author Sebastian Riedel
 */
public interface ValueVisitor {

  void visitTuple(TupleValue tuple);

  void visitRelation(RelationValue relation);

  void visitCategorical(CategoricalValue categoricalValue);

  void visitArray(ArrayValue arrayValue);

  void visitInt(IntValue intValue);

  void visitDouble(DoubleValue doubleValue);

  void visitBool(BoolValue boolValue);
}
