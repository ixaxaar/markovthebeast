package com.googlecode.thebeast.pml;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public final class GroundFactor {

    private List<GroundNode> body;
    private List<GroundNode> head;

    private PMLClause clause;
    private FeatureIndex index;
    private double scale;

    GroundFactor(List<GroundNode> body, List<GroundNode> head,
                 PMLClause clause, FeatureIndex index, double scale) {
        this.body = body;
        this.head = head;
        this.clause = clause;
        this.index = index;
        this.scale = scale;
    }

    public List<GroundNode> getBody() {
        return body;
    }

    public List<GroundNode> getHead() {
        return head;
    }

    public PMLClause getClause() {
        return clause;
    }

    public FeatureIndex getIndex() {
        return index;
    }

    public double getScale() {
        return scale;
    }

    public double evaluate(Assignment assignment) {
        for (GroundNode node : body)
            if (assignment.getValue(node) == 0.0) return 1.0;
        return clause.getFirstOrderOperator().evaluate(head, assignment);
    }

}
