package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.DoubleType;
import com.googlecode.thebeast.world.IntegerType;
import com.googlecode.thebeast.world.Predicate;
import com.googlecode.thebeast.world.PredicateNotInSignatureException;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.SignatureListener;
import com.googlecode.thebeast.world.SignatureMismatchException;
import com.googlecode.thebeast.world.Symbol;
import com.googlecode.thebeast.world.SymbolAlreadyExistsException;
import com.googlecode.thebeast.world.SymbolNotPartOfSignatureException;
import com.googlecode.thebeast.world.Type;
import com.googlecode.thebeast.world.TypeNotInSignatureException;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.World;
import com.googlecode.thebeast.world.sigl.SIGLInterpreter;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * An SQLSignature is an SQL based implementation of a Signature. Essentially relations are stored as SQL tables and
 * types know how to represent their constants as sql terms.
 *
 * @author Sebastian Riedel
 * @see com.googlecode.thebeast.world.Type
 */
public final class SQLSignature implements Serializable, Signature {

    /**
     * Serial version id for java serialization.
     */
    private static final long serialVersionUID = 1999L;

    /**
     * The Id to be given to the next possible world to create.
     */
    private int currentWorldId = 0;

    /**
     * The pool of sql tables to reuse.
     */
    private SQLTablePool sqlTablePool;

    /**
     * The query engine to use for all worlds of this signature.
     */
    private SQLBasedQueryEngine queryEngine;

    private SQLIntegerType integerType;

    private SQLDoubleType doubleType;

    /**
     * A map from type names to types. This map contains user types as well as built-in types.
     *
     * @see com.googlecode.thebeast.world.sql.SQLUserType
     */
    private final LinkedHashMap<String, Type>
        types = new LinkedHashMap<String, Type>();

    /**
     * A mapping from names to user types.
     */
    private final LinkedHashMap<String, SQLUserType>
        userTypes = new LinkedHashMap<String, SQLUserType>();

    /**
     * A mapping from predicate names to predicates.
     */
    private final LinkedHashMap<String, Predicate>
        predicates = new LinkedHashMap<String, Predicate>();

    /**
     * Stores all user predicates.
     */
    private final LinkedHashMap<String, SQLUserPredicate>
        userPredicates = new LinkedHashMap<String, SQLUserPredicate>();

    /**
     * A mapping from names to symbols (types, predicates, constants and functions).
     */
    private final LinkedHashMap<String, Symbol>
        symbols = new LinkedHashMap<String, Symbol>();


    /**
     * The list of listeners of this signature.
     */
    private final ArrayList<SignatureListener>
        listeners = new ArrayList<SignatureListener>();

    /**
     * Connection to database that is used to store ground atoms.
     */
    private Connection connection;
    private SIGLInterpreter interpreter = new SIGLInterpreter(this);

    /**
     * Creates a new signature and opens a connection to the H2 database.
     */
    SQLSignature() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
            sqlTablePool = new SQLTablePool(this);
            queryEngine = new SQLBasedQueryEngine();
            integerType = new SQLIntegerType("Integer", this);
            doubleType = new SQLDoubleType("Double", this);
            registerType(integerType);
            registerType(doubleType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new SQLSignature.
     *
     * @return a new SQLSignature object.
     */
    public static SQLSignature createSignature() {
        return new SQLSignature();
    }


    /**
     * Tests whether the signature equals the given one. If not a runtime exception is thrown. This is mainly a
     * convenience method for clients how want to ensure that arguments to their operations have matching types.
     *
     * @param signature the signature to match.
     * @throws com.googlecode.thebeast.world.SignatureMismatchException
     *          when signatures do not match.
     */
    void match(final Signature signature)
        throws SignatureMismatchException {
        if (!this.equals(signature)) {
            throw new SignatureMismatchException("Signatures do not match",
                this, signature);
        }

    }

    /**
     * Provides a signature-wide database connection to classes of this package.
     *
     * @return the database connection of this signature.
     */
    Connection getConnection() {
        return connection;
    }

    /**
     * Returns the query engine to use for worlds of this signature.
     *
     * @return a global query engine for this signature.
     */
    SQLBasedQueryEngine getQueryEngine() {
        return queryEngine;
    }

    /**
     * Returns the pool of tables that represents relations of this signature.
     *
     * @return an SQLTablePool that manages tables representing relations of this signature.
     */
    SQLTablePool getSqlTablePool() {
        return sqlTablePool;
    }

    /**
     * Add a symbol to the signature. This method is called by all symbol factor methods in this class and in {@link
     * Type}.
     *
     * @param symbol the symbol we want to register.
     * @throws com.googlecode.thebeast.world.SymbolAlreadyExistsException
     *          if there is a symbol with the same name.
     */
    void registerSymbol(final Symbol symbol) throws SymbolAlreadyExistsException {
        Symbol old = symbols.get(symbol.getName());
        if (old != null) {
            throw new SymbolAlreadyExistsException(old, this);
        }
        symbols.put(symbol.getName(), symbol);
        for (SignatureListener l : listeners) {
            l.symbolAdded(symbol);
        }
    }

    /**
     * Unregistered the symbol from this signature.
     *
     * @param symbol the symbol to be removed from the signature.
     * @throws com.googlecode.thebeast.world.SymbolNotPartOfSignatureException
     *          if the symbol is not a member of this signature (e.g. it has been created by a different signature
     *          object).
     */
    void unregisterSymbol(final Symbol symbol)
        throws SymbolNotPartOfSignatureException {
        if (!symbol.getSignature().equals(this)) {
            throw new SymbolNotPartOfSignatureException(symbol, this);
        }
        symbols.remove(symbol.getName());
        for (SignatureListener l : listeners) {
            l.symbolRemoved(symbol);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void interpret(String sigl) {
        interpreter.interpret(sigl);
    }

    /**
     * Returns the symbol for the given name.
     *
     * @param name the name of the symbol.
     * @return the symbol with the given name.
     */
    public Symbol getSymbol(final String name) {
        try {
            return integerType.getConstant(Integer.parseInt(name));
        } catch (NumberFormatException e1) {
            try {
                return doubleType.getConstant(Double.parseDouble(name));
            } catch (NumberFormatException e2) {
                return symbols.get(name);
            }
        }
    }


    /**
     * Adds the given listener to the listeners of this signature. Will be notified of any changes to it.
     *
     * @param signatureListener a listener to signature events.
     */
    public void addSignatureListener(final SignatureListener signatureListener) {
        listeners.add(signatureListener);
    }

    /**
     * Removes the specified listener from the list of listeners this signature maintains.
     *
     * @param signatureListener the listener to remove.
     */
    public void removeSignatureListener(
        final SignatureListener signatureListener) {
        listeners.remove(signatureListener);
    }


    /**
     * Creates a new possible world implemented as a SQLWorld.
     *
     * @return a new possible world with unique id wrt to this signature.
     * @see com.googlecode.thebeast.world.Signature#createWorld()
     */
    public World createWorld() {
        return new SQLWorld(this, currentWorldId++);
    }

    /**
     * {@inheritDoc}
     */
    public World createWorld(World parent) {
        World result = createWorld();
        for (UserPredicate predicate : getUserPredicates())
            if (!parent.getRelation(predicate).isOpen())
                result.addParent(predicate, parent);
        return result;
    }

    /**
     * Creates a new UserType with the given name.
     *
     * @param name       the name of the type.
     * @param extendable if the type can create new constants when queried for constants with unknown names.
     * @return a SQLUserType with the given name.
     * @throws com.googlecode.thebeast.world.SymbolAlreadyExistsException
     *          if there is a symbol with the same name in the signature.
     * @see com.googlecode.thebeast.world.Signature#createType(String, boolean)
     * @see com.googlecode.thebeast.world.sql.SQLUserType
     */
    public SQLUserType createType(final String name, final boolean extendable)
        throws SymbolAlreadyExistsException {

        SQLUserType type = new SQLUserType(name, extendable, this);
        registerType(type);
        userTypes.put(name, type);
        return type;
    }


    private void registerType(final Type type) {
        registerSymbol(type);
        types.put(type.getName(), type);
    }

    /**
     * Convenience method to create a type that already contains a set of constants.
     *
     * @param name       the name of the type.
     * @param extendable whether the type should be extendable on the fly.
     * @param constants  a vararg array of constant names.
     * @return a type that contains constants with the provided names.
     */
    public SQLUserType createType(final String name, final boolean extendable,
                                  final String... constants) {
        SQLUserType type = createType(name, extendable);
        for (String constant : constants) {
            type.createConstant(constant);
        }
        return type;
    }

    /**
     * Removes a type from the signature.
     *
     * @param type the type to remove from the signature.
     * @throws com.googlecode.thebeast.world.SymbolNotPartOfSignatureException
     *          if the type is not a member of the signature (e.g. because it was created by a different signature
     *          object).
     */
    public void removeType(final Type type)
        throws SymbolNotPartOfSignatureException {
        unregisterSymbol(type);
        types.remove(type.getName());
    }

    /**
     * Creates a new UserPredicate and stores it in this signature.
     *
     * @param name          the name of the new predicate
     * @param argumentTypes a list with its argument types.
     * @return a UserPredicate with the specified properties.
     * @throws com.googlecode.thebeast.world.SymbolAlreadyExistsException
     *          if there is a symbol with the same name in the signature.
     */
    public SQLUserPredicate createPredicate(final String name,
                                            final List<Type> argumentTypes)
        throws SymbolAlreadyExistsException {

        ArrayList<SQLRepresentableType>
            sqlTypes = new ArrayList<SQLRepresentableType>();
        for (Type type : argumentTypes) {
            match(type.getSignature());
            sqlTypes.add((SQLRepresentableType) type);
        }
        SQLUserPredicate predicate = new SQLUserPredicate(name, sqlTypes, this);
        registerSymbol(predicate);
        predicates.put(name, predicate);
        userPredicates.put(name, predicate);
        return predicate;
    }

    /**
     * Convenience method to create predicates without using a list.
     *
     * @param name          the name of the predicate
     * @param argumentTypes an vararg array of argument types
     * @return a UserPredicate with the specified properties.
     * @throws com.googlecode.thebeast.world.SymbolAlreadyExistsException
     *          if there is a symbol in the signature that already has this name.
     * @see Signature#createPredicate(String, Type...)
     */
    public SQLUserPredicate createPredicate(final String name,
                                            final Type... argumentTypes)
        throws SymbolAlreadyExistsException {
        return createPredicate(name, Arrays.asList(argumentTypes));
    }

    /**
     * Removes a predicate from the signature.
     *
     * @param predicate the predicate to remove from the signature.
     * @throws com.googlecode.thebeast.world.SymbolNotPartOfSignatureException
     *          if the predicate is not a member of the signature (e.g. because it was created by a different signature
     *          object).
     */
    public void removePredicate(final UserPredicate predicate)
        throws SymbolNotPartOfSignatureException {
        unregisterSymbol(predicate);
        predicates.remove(predicate.getName());
        userPredicates.remove(predicate.getName());
    }


    /**
     * Returns the type corresponding to the given type name. An exception is thrown if there is no such type. If you want
     * to find out whether a type exists use {@link com.googlecode.thebeast.world.Signature#getTypeNames()} and {@link
     * java.util.Set#contains(Object)} instead.
     *
     * @param name the name of the type to return.
     * @return either a built-in type of a {@link com.googlecode.thebeast.world.sql.SQLUserType}
     * @throws com.googlecode.thebeast.world.TypeNotInSignatureException
     *          if there is no type with the given name.
     * @see com.googlecode.thebeast.world.Signature#getType(String)
     */
    public Type getType(final String name) throws TypeNotInSignatureException {
        Type type = types.get(name);
        if (type == null) {
            throw new TypeNotInSignatureException(name, this);
        }
        return type;
    }

    /**
     * Returns the user type corresponding to the given name.
     *
     * @param name the name of the type to return.
     * @return the user type with the given name.
     * @throws com.googlecode.thebeast.world.TypeNotInSignatureException
     *          if there is no type with this name.
     * @see com.googlecode.thebeast.world.Signature#getUserType(String)
     */
    public SQLUserType getUserType(final String name)
        throws TypeNotInSignatureException {
        SQLUserType type = userTypes.get(name);
        if (type == null) {
            throw new TypeNotInSignatureException(name, this);
        }
        return type;
    }

    /**
     * Returns the set of type names this signature maintains.
     *
     * @return an unmodifiable view on the set of type names.
     */
    public Set<String> getTypeNames() {
        return Collections.unmodifiableSet(types.keySet());
    }


    /**
     * Returns the predicate with the given name if available. Returns both user and built-in predicates.
     *
     * @param name the name of the predicate to return.
     * @return a predicate of this signature with the given name.
     * @throws com.googlecode.thebeast.world.PredicateNotInSignatureException
     *          if there is not predicate with the given name.
     */
    public Predicate getPredicate(final String name)
        throws PredicateNotInSignatureException {
        return predicates.get(name);
    }

    /**
     * Returns the set of predicate names this signature maintains.
     *
     * @return an unmodifiable view on the set of predicate names.
     */
    public Set<String> getPredicateNames() {
        return Collections.unmodifiableSet(predicates.keySet());
    }

    /**
     * Returns the collection user predicates in this signature.
     *
     * @return an unmodifiable view on the set of user predicates.
     */
    public Collection<SQLUserPredicate> getUserPredicates() {
        return Collections.unmodifiableCollection(userPredicates.values());
    }

    /**
     * Returns the set of all types this signature maintains.
     *
     * @return a collection of all types in this signature. Iterating over this collection maintains the order of type
     *         creation.
     */
    public Collection<Type> getTypes() {
        return Collections.unmodifiableCollection(types.values());
    }

    /**
     * {@inheritDoc}
     */
    public IntegerType getIntegerType() {
        return integerType;
    }

    /**
     * {@inheritDoc}
     */
    public DoubleType getDoubleType() {
        return doubleType;
    }


}
