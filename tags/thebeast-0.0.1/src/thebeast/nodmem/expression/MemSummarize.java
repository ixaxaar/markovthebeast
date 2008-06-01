package thebeast.nodmem.expression;

import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.Summarize;
import thebeast.nod.expression.ExpressionVisitor;
import thebeast.nod.expression.ScalarExpression;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.Attribute;
import thebeast.nodmem.type.*;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class MemSummarize extends AbstractMemExpression<RelationType> implements Summarize {

  private RelationExpression relation;
  private ArrayList<String> by;
  private ArrayList<String> as;
  private ArrayList<Spec> specs;
  private ArrayList<ScalarExpression> add;


  public MemSummarize(RelationExpression relation, List<String> by,
                      List<ScalarExpression> add, List<Spec> specs, List<String> as) {
    super(determineType(relation, by, add, specs, as));
    this.relation = relation;
    this.by = new ArrayList<String>(by);
    this.add = new ArrayList<ScalarExpression>(add);
    this.specs = new ArrayList<Spec>(specs);
    this.as = new ArrayList<String>(as);
  }

  private static RelationType determineType(RelationExpression relation, List<String> by,
                                            List<ScalarExpression> add, List<Spec> specs, List<String> as) {
    LinkedList<Attribute> resultAttributes = new LinkedList<Attribute>();
    for (String att: by){
      resultAttributes.add(relation.type().heading().attribute(att));
    }
    for (int i = 0; i < add.size(); ++i){
      switch (specs.get(i)){
        case INT_SUM:
        case INT_COUNT:
          resultAttributes.add(new MemAttribute(as.get(i), MemIntType.INT));
          break;
        case DOUBLE_SUM:
        case DOUBLE_COUNT:
          resultAttributes.add(new MemAttribute(as.get(i), MemDoubleType.DOUBLE));
          break;
      }
    }
    return new MemRelationType(new MemHeading(resultAttributes, 0));
  }


  protected MemSummarize(RelationType type) {
    super(type);
  }


  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitSummarize(this);
  }

  public RelationExpression relation() {
    return relation;
  }

  public List<String> by() {
    return by;
  }

  public List<ScalarExpression> add() {
    return add;
  }

  public List<Spec> specs() {
    return specs;
  }

  public List<String> as() {
    return as;
  }
}
