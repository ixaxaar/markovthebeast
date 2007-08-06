package thebeast.nod.expression;

import thebeast.nod.type.BoolType;
import thebeast.nod.type.TupleType;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface Query extends RelationExpression {

  List<String> prefixes();
  List<RelationExpression> relations();
  RelationExpression relation(String prefix);
  BoolExpression where();
  TupleExpression select();
  boolean unify();

}
