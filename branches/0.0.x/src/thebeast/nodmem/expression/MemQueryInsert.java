package thebeast.nodmem.expression;

import thebeast.nod.type.RelationType;
import thebeast.nod.expression.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 17:47:29
 */
public class MemQueryInsert extends AbstractMemExpression<RelationType> implements QueryInsert {

  private ArrayList<String> prefixes;
  private ArrayList<RelationExpression> from;
  private BoolExpression where;
  private RelationExpression insert;
  private HashMap<String,RelationExpression> prefix2relation = new HashMap<String, RelationExpression>();


  public MemQueryInsert(RelationType type, List<String> prefixes, List<? extends RelationExpression> from,
                  BoolExpression where, RelationExpression insert) {
    super(type);
    this.prefixes = new ArrayList<String>(prefixes);
    this.from = new ArrayList<RelationExpression>(from);
    this.where = where;
    this.insert = insert;
    Iterator<String> iterPrefix = prefixes.iterator();
    Iterator<? extends RelationExpression> iterFrom = from.iterator();
    //noinspection ForLoopReplaceableByForEach
    for (int index = 0; index < from.size(); ++index){
      prefix2relation.put(iterPrefix.next(), iterFrom.next());
    }
  }

  protected MemQueryInsert(RelationType type) {
    super(type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitQueryInsert(this);
  }

  public List<String> prefixes() {
    return prefixes;
  }

  public List<RelationExpression> relations() {
    return from;
  }

  public RelationExpression relation(String prefix) {
    return prefix2relation.get(prefix);
  }

  public BoolExpression where() {
    return where;
  }

  public RelationExpression insert() {
    return insert;
  }
}
