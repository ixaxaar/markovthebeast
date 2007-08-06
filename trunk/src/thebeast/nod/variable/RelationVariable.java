package thebeast.nod.variable;

import thebeast.nod.value.RelationValue;
import thebeast.nod.type.RelationType;
import thebeast.nod.expression.RelationExpression;

/**
 * @author Sebastian Riedel
 */
public interface RelationVariable extends Variable<RelationValue,RelationType>, RelationExpression {

  void addTuple(Object ... args);

  //todo: this should probably be in the value class 
  public boolean contains(Object ...args);

  Index getIndex(String name);

  int[] getIntColumn(String attribute);

  double[] getDoubleColumn(String attribute);

  void assignByArray(int[] ints, double[] doubles);

  int byteSize();

  public boolean hasIndex(String name);

}
