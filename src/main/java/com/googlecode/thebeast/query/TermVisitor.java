package com.googlecode.thebeast.query;

import com.googlecode.thebeast.world.Constant;

/**
 * @author Sebastian Riedel
 */
public interface TermVisitor {

    void visitConstant(Constant constant);

    void visitVariable(Variable variable);
}
