package com.googlecode.thebeast;

/**
 * This is the main class of markov thebeast.
 *
 * @author Sebastian Riedel
 */
public final class TheBeast {

  /**
   * Hide the default constructor.
   */
  private TheBeast() {

  }

  /**
   * Version string. Should be updated when a new version is to be released.
   */
  public static final String VERSION = "1.0.0-SNAPSHOT";

  /**
   * Does not do much yet.
   *
   * @param args array of parameters, not used yet.
   */
  public static void main(final String[] args) {
    System.out.println("markov thebeast " + VERSION);
  }
}
