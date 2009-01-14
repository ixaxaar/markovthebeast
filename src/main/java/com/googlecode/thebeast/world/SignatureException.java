package com.googlecode.thebeast.world;

/**
 * An Exception thrown by the {@link com.googlecode.thebeast.world.sql.SQLSignature} class.
 *
 * @author Sebastian Riedel
 */
public abstract class SignatureException extends RuntimeException {

    /**
     * The signature that threw this exception.
     */
    private final Signature signature;

    /**
     * Create a new Signature exception with the corresponding message belonging to the given signature.
     *
     * @param message   the message to display.
     * @param signature the signature that created and threw this exception.
     */
    protected SignatureException(final String message,
                                 final Signature signature) {
        super(message);
        this.signature = signature;
    }

    /**
     * Returns the signature that threw this exception.
     *
     * @return A signature that threw this exception.
     */
    public final Signature getSignature() {
        return signature;
    }
}
