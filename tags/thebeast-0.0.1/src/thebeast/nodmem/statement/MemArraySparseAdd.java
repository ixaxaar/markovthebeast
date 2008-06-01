package thebeast.nodmem.statement;

import thebeast.nod.expression.DoubleExpression;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.ArraySparseAdd;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.variable.ArrayVariable;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 27-Jan-2007 Time: 18:48:06
 */
public class MemArraySparseAdd implements ArraySparseAdd {

  private ArrayVariable variable;
  private RelationExpression expression;
  private String indexAttribute, valueAttribute;
  private DoubleExpression scale;
  private Sign sign;


  public MemArraySparseAdd(ArrayVariable variable, RelationExpression expression, DoubleExpression scale,
                           String indexAttribute, String valueAttribute, Sign sign) {
    this.variable = variable;
    this.expression = expression;
    this.indexAttribute = indexAttribute;
    this.valueAttribute = valueAttribute;
    this.scale = scale;
    this.sign = sign;
  }

  public MemArraySparseAdd(ArrayVariable variable, RelationExpression expression, DoubleExpression scale,
                           String indexAttribute, String valueAttribute) {
    this(variable, expression, scale, indexAttribute, valueAttribute, Sign.FREE);
  }

  public ArrayVariable variable() {
    return variable;
  }

  public RelationExpression sparseVector() {
    return expression;
  }

  public String indexAttribute() {
    return indexAttribute;
  }

  public String valueAttribute() {
    return valueAttribute;
  }

  public DoubleExpression scale() {
    return scale;
  }

  public Sign sign() {
    return sign;
  }

  public void acceptStatementVisitor(StatementVisitor visitor) {
    visitor.visitArraySparseAdd(this);
  }
}
