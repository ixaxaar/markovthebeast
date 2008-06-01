package thebeast.nod.expression;

import thebeast.nod.type.RelationType;
import thebeast.nod.type.Attribute;
import thebeast.nod.identifier.Name;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface Join extends Expression<RelationType> {
  List<RelationExpression> relations();
  List<Attribute> joinAttributes();
  List<Attribute> joinAttributesFor(int index);
  List<Integer> joinRelationsFor(Attribute attribute);


}
