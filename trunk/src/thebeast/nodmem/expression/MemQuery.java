package thebeast.nodmem.expression;

import thebeast.nod.expression.*;
import thebeast.nod.type.RelationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 15-Jan-2007 Time: 17:47:29
 */
public class MemQuery extends AbstractMemExpression<RelationType> implements Query  {

  private ArrayList<String> prefixes;
  private ArrayList<RelationExpression> from;
  private BoolExpression where;
  private TupleExpression select;
  private HashMap<String,RelationExpression> prefix2relation = new HashMap<String, RelationExpression>();


  public MemQuery(RelationType type, List<String> prefixes, List<? extends RelationExpression> from,
                  BoolExpression where, TupleExpression select) {
    super(type);
    this.prefixes = new ArrayList<String>(prefixes);
    this.from = new ArrayList<RelationExpression>(from);
    this.where = where;
    this.select = select;
    Iterator<String> iterPrefix = prefixes.iterator();
    Iterator<? extends RelationExpression> iterFrom = from.iterator();
    //noinspection ForLoopReplaceableByForEach
    for (int index = 0; index < from.size(); ++index){
      prefix2relation.put(iterPrefix.next(), iterFrom.next());
    }
    if (from.size() == 0) throw new RuntimeException("Empty FROM list for query " + this);
  }

  protected MemQuery(RelationType type) {
    super(type);
  }

  public void acceptExpressionVisitor(ExpressionVisitor visitor) {
    visitor.visitQuery(this);
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

  public TupleExpression select() {
    return select;
  }
}
