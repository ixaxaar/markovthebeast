package com.googlecode.thebeast.world;

/**
 * A SignatureListener listens to changes made to the signature. It will be
 * notified whenever new types or predicates are added or removed.
 *
 * @author Sebastian Riedel
 */
public interface SignatureListener {

  /**
   * Called when a new symbol is added to the signature. Note that this method
   * is called after the symbol was registered but possibly before it was stored
   * in its corresponding container. For example, if a type was added it might
   * not be contained in {@link com.googlecode.thebeast.world.sql.SQLSignature#getTypes()}
   * yet. Also note that this method is not called for the constants of
   * non-iterable types. 
   *
   * @param symbol the symbol that was added to the signature.
   */
  void symbolAdded(Symbol symbol);


  /**
   * This method is called whenver a symbol is removed from the signature. Note
   * when this method is called the symbol has been removed from the list of
   * symbols contained in the signature but it might still be in the container
   * specfic to the type of the symbol.
   *
   * @param symbol the symbol that was removed from the signature.
   */
  void symbolRemoved(Symbol symbol);
}
