package thebeast.nod.type;

import thebeast.forml.nod.*;
import thebeast.nodmem.type.MemDoubleType;
import thebeast.nodmem.type.MemBoolType;

/**
 * @author Sebastian Riedel
 */
public interface TypeVisitor {
  void visitCategoricalType(CategoricalType categoricalType);

  void visitTupleType(TupleType tupleType);

  void visitRelationType(RelationType relationType);

  void visitArrayType(ArrayType arrayType);

  void visitIntType(IntType intType);

  void visitDoubleType(DoubleType doubleType);

  void visitBoolType(BoolType boolType);

}
