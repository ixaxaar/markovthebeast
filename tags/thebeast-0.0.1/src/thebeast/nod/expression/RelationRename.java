package thebeast.nod.expression;

import thebeast.nod.type.Type;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public interface RelationRename extends RelationExpression {
  List<NameIntroduction> nameIntroductions();
  RelationExpression relationExpression();
}
