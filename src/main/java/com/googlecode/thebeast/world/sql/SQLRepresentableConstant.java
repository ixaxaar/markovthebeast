package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.query.TermVisitor;
import com.googlecode.thebeast.world.AbstractSymbol;
import com.googlecode.thebeast.world.Constant;

/**
 * An SQLRepresentableConstant is a constant which can be represented as a value in an SQL table cell.
 *
 * @author Sebastian Riedel
 */
abstract class SQLRepresentableConstant extends AbstractSymbol
    implements Constant {


    private final SQLRepresentableType type;

    /**
     * Creates an SQLRepresentableConstant with the given name and signature.
     *
     * @param name the name of the symbol.
     * @param type type of the constant.
     */
    protected SQLRepresentableConstant(final String name,
                                       final SQLRepresentableType type) {
        super(name, type.getSignature());
        this.type = type;
    }

    /**
     * Return an SQL representation of this constant.
     *
     * @return an Object that can be used with {@link java.sql.ResultSet#updateObject(int,Object)}
     * @see SQLRepresentableType
     */
    abstract Object asSQLConstant();

    /**
     * Returns true because a constant is ground.
     *
     * @return true because a constant is ground.
     * @see com.googlecode.thebeast.query.Term#isGround()
     */
    public boolean isGround() {
        return true;
    }

    /**
     * Calls the visitConstant method of the visitor.
     *
     * @param visitor the visitor to accept.
     */
    public void accept(TermVisitor visitor) {
        visitor.visitConstant(this);
    }

    /**
     * Returns the type of this constant.
     *
     * @return the type of this constant.
     */
    public SQLRepresentableType getType() {
        return type;
    }
}
