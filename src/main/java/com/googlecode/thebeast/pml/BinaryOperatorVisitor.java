package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public interface BinaryOperatorVisitor {

    void visitAnd(And and);

    void visitOr(Or or);

    void visitImplies(Implies implies);
}
