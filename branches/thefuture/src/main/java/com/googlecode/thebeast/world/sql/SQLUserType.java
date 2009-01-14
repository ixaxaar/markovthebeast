package com.googlecode.thebeast.world.sql;

import com.google.common.collect.Iterators;
import com.googlecode.thebeast.world.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A UserType is a collection of constants created by the user. Note that the UserType object creates and owns all its
 * constants. Also note that a UserType can only be created using {@link com.googlecode.thebeast.world.sql.SQLSignature#createPredicate(String,
 * java.util.List)}.
 *
 * @author Sebastian Riedel
 * @see SQLUserConstant
 * @see com.googlecode.thebeast.world.sql.SQLSignature
 */
final class SQLUserType extends SQLRepresentableType
    implements UserType {

    /**
     * Indicates whether type can be extended on the fly.
     */
    private final boolean extendable;

    /**
     * Contains a mapping from unique ids to constants.
     */
    private final LinkedHashMap<Integer, SQLUserConstant>
        id2constant = new LinkedHashMap<Integer, SQLUserConstant>();

    /**
     * Contains a mapping from constant names to constants.
     */
    private final LinkedHashMap<String, SQLUserConstant>
        constants = new LinkedHashMap<String, SQLUserConstant>();


    /**
     * Creates a type with the given name. Usually called by the {@link com.googlecode.thebeast.world.sql.SQLSignature}.
     *
     * @param name       the name of the type.
     * @param extendable true iff calls to getConstant with an unknown name should create new constants with the unknown
     *                   names.
     * @param signature  the signature this type belongs to.
     */
    SQLUserType(final String name, final boolean extendable,
                final SQLSignature signature) {
        super(name, signature);
        this.extendable = extendable;
    }


    /**
     * Returns an unmodifiable view on the set of constants.
     *
     * @return an unmodifiable collection of the constants contained in this type.
     */
    public Collection<SQLUserConstant> getConstants() {
        return Collections.unmodifiableCollection(constants.values());
    }


    /**
     * Creates a new UserConstant with the given name, assigns an id to it and links it to this type.
     *
     * @param name the name of the constant.
     * @return a UserConstant with the given name and this object as its type.
     */
    public SQLUserConstant createConstant(final String name) {
        SQLUserConstant constant =
            new SQLUserConstant(name, this, constants.size());
        ((SQLSignature) getSignature()).registerSymbol(constant);
        constants.put(name, constant);
        id2constant.put(constant.getId(), constant);
        return constant;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExtendable() {
        return extendable;
    }

    /**
     * {@inheritDoc}
     */
    public UserConstant getConstant(final String name)
        throws ConstantNameNotInTypeException {
        UserConstant constant = constants.get(name);
        if (constant == null) {
            if (extendable) {
                return createConstant(name);
            } else {
                throw new ConstantNameNotInTypeException(name, this);
            }

        } else {
            return constant;
        }
    }

    /**
     * {@inheritDoc}
     */
    public UserConstant getConstant(final int id)
        throws ConstantIdNotInTypeException {
        UserConstant constant = id2constant.get(id);
        if (constant == null) {
            throw new ConstantIdNotInTypeException(id, this);
        } else {
            return constant;
        }
    }

    /**
     * Returns the SQL type to represent user constants. UserType uses integers (the constant ids) to represent its
     * constants.
     *
     * @return the SQL column type to represent objects of this type.
     * @see com.googlecode.thebeast.world.sql.SQLRepresentableType#asSQLType()
     */
    String asSQLType() {
        return "integer";
    }

    /**
     * Get the constant denoted by the given SQL representation.
     *
     * @param representation the Integer object that represents the id of the constant to return.
     * @return the constant denoted by the given representation.
     * @see SQLRepresentableType#getConstantFromSQL(Object)
     */
    Constant getConstantFromSQL(final Object representation) {
        return getConstant((Integer) representation);
    }

    /**
     * By default types are not iterable.
     *
     * @return false by default.
     * @see com.googlecode.thebeast.world.sql.SQLRepresentableType#isIterable()
     */
    public boolean isIterable() {
        return true;
    }

    /**
     * Returns the number of user constants in this type.
     *
     * @return the number of user constants.
     * @see com.googlecode.thebeast.world.Type#size()
     */
    public int size() {
        return constants.size();
    }

    /**
     * Method iterator returns an iterator over the created user constants.
     *
     * @return Iterator<Constant> an iterator that iterates over all constants in this type.
     * @see com.googlecode.thebeast.world.sql.SQLRepresentableType#iterator()
     */
    public Iterator<Constant> iterator() {
        return Iterators.filter(constants.values().iterator(), Constant.class);
    }
}
