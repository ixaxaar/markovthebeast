package thebeast.nod.expression;

import thebeast.nod.type.IntType;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface IntBins extends IntExpression {
  List<Integer> bins();
  IntExpression argument();

}
