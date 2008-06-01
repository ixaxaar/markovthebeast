package thebeast.nod.statement;

import thebeast.nodmem.statement.*;

/**
 * @author Sebastian Riedel
 */
public interface StatementVisitor {
  void visitInsert(Insert insert);

  void visitAssign(Assign assign);

  void visitCreateIndex(CreateIndex createIndex);

  void visitRelationUpdate(RelationUpdate relationUpdate);

  void visitArrayAppend(ArrayAppend arrayAppend);

  void visitClearRelationVariable(ClearRelationVariable clearRelationVariable);

  void visitArraySparseAdd(ArraySparseAdd arraySparseAdd);

  void visitArrayAdd(ArrayAdd arrayAdd);

  void visitRelationAppend(RelationAppend relationAppend);
}
