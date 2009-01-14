package com.googlecode.thebeast.world;

/**
 * This Exception is is thrown when a type is queried for a constant with an id but there is no constant with the given
 * id in the type.
 *
 * @author Sebastian Riedel
 */
public final class ConstantIdNotInTypeException extends RuntimeException {

    /**
     * The requiested constant id.
     */
    private final int constantId;

    /**
     * The type that got the request.
     */
    private final Type type;

    /**
     * Creates a new exception for the given id and type.
     *
     * @param constantId the id that was to be resolved.
     * @param type       the type that got the request.
     */
    public ConstantIdNotInTypeException(final int constantId, final Type type) {
        super("There is no constant with id " + constantId + " in type "
            + type.getName());
        this.constantId = constantId;
        this.type = type;
    }

    /**
     * Returns an integer value that was used to request a constant from the given type.
     *
     * @return the id that was to be resolved.
     */
    public int getConstantId() {
        return constantId;
    }

    /**
     * Returns the type that got the request for a constant with the given id.
     *
     * @return the type which got the request.
     */
    public Type getType() {
        return type;
    }
}
