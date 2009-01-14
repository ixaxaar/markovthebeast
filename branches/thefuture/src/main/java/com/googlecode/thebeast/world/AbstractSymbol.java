package com.googlecode.thebeast.world;

/**
 * An abstract helper implementation of Symbol. Provides a simple implementation of this methods required by the Symbol
 * interface.
 *
 * @author Sebastian Riedel
 */
public abstract class AbstractSymbol implements Symbol {

    /**
     * The name of this symbol.
     */
    private final String name;

    /**
     * The signature of this symbol.
     */
    private final Signature signature;

    /**
     * Creates an AbstractSymbol with the given name and signature.
     *
     * @param name      the name of the symbol.
     * @param signature the signature of the symbol.
     */
    protected AbstractSymbol(final String name, final Signature signature) {
        this.name = name;
        this.signature = signature;
    }

    /**
     * Returns the name of this symbol.
     *
     * @return String with name of this symbol.
     * @see Symbol#getName()
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the signature this symbol belongs to.
     *
     * @return Signature of this symbol.
     * @see Symbol#getSignature()
     */
    public final Signature getSignature() {
        return signature;
    }


    /**
     * Two symbols are equal if they have the same name and are part of the same signature.
     *
     * @param o the other symbol.
     * @return true iff both symbols have the same name and signature.
     */
    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractSymbol that = (AbstractSymbol) o;

        boolean nameEquals =
            name != null ? !name.equals(that.name) : that.name != null;
        if (nameEquals) {
            return false;
        }
        if (signature != null
            ? !signature.equals(that.signature) : that.signature != null) {
            return false;
        }

        return true;
    }

    /**
     * The hashcode of a symbol is based on the hashcode of its name and signature.
     *
     * @return a hashcode based on the name and signature.
     */
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (signature != null ? signature.hashCode() : 0);
        return result;
    }

    /**
     * Returns the name of this symbol as representation.
     *
     * @return the name of this symbol.
     */
    public String toString() {
        return getName();
    }
}
