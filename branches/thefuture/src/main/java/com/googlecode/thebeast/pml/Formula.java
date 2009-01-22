package com.googlecode.thebeast.pml;

/**
 * @author Sebastian Riedel
 */
public abstract class Formula {

    public abstract void accept(FormulaVisitor visitor);
}
