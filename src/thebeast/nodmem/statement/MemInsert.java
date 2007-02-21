package thebeast.nodmem.statement;

import thebeast.nodmem.expression.MemVariableReference;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.expression.VariableReference;
import thebeast.nod.statement.Insert;
import thebeast.nod.statement.StatementVisitor;
import thebeast.nod.type.RelationType;
import thebeast.nod.variable.RelationVariable;

import javax.management.relation.Relation;

/**
 * @author Sebastian Riedel
 */
public class MemInsert implements Insert {

    private RelationVariable relationTarget;
    private RelationExpression relationExp;

    public MemInsert(RelationVariable relationTarget, RelationExpression relationExp) {
        this.relationTarget = relationTarget;
        this.relationExp = relationExp;
    }

    public RelationVariable relationTarget() {
        return relationTarget;
    }

    public RelationExpression relationExp() {
        return relationExp;
    }

    public void acceptStatementVisitor(StatementVisitor visitor) {
        visitor.visitInsert(this);
    }
}
