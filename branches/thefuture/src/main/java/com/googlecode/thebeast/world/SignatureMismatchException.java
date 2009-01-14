package com.googlecode.thebeast.world;

/**
 * Many methods and classes require matching signatures for the objects it proceses or contains. This method is thrown
 * when signatures do not match, e.g. if a predicate for a signature is to be created with types from a different
 * signature.
 *
 * @author Sebastian Riedel
 */
public final class SignatureMismatchException extends RuntimeException {

    /**
     * The expected signature.
     */
    private final Signature expected;

    /**
     * The signature which was found.
     */
    private final Signature actual;


    /**
     * Create a new SignatureMismatchException with the given properties.
     *
     * @param message  the message to display.
     * @param expected the expected signature.
     * @param actual   the actual signature.
     */
    public SignatureMismatchException(final String message,
                                      final Signature expected,
                                      final Signature actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * Returns the signature which was expected.
     *
     * @return the expected signature.
     */
    public Signature getExpected() {
        return expected;
    }

    /**
     * Returns the signature which was observed.
     *
     * @return the actual signature.
     */
    public Signature getActual() {
        return actual;
    }
}
