package thebeast.pml;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Universe represents a collection of ground atoms for the predicates of a certain
 * signature. It also stores a set of weight functions to be used in PML models.
 */
public class GroundAtoms {

  private Signature signature;
  private Map<UserPredicate, GroundAtomCollection> atoms = new TreeMap<UserPredicate, GroundAtomCollection>();

  GroundAtoms(Signature signature) {
    this.signature = signature;
    for (UserPredicate predicate : signature.getUserPredicates()) {
      atoms.put(predicate, new GroundAtomCollection(predicate));
    }
  }

  /**
   * This method populates this universe with the ground atoms from another universe for
   * the specified predicates.
   *
   * @param groundAtoms the universe from which to take the ground atoms. Must have the same signature.
   * @param predicates  the predicates for which we want to include the ground atoms from.
   */
  public void load(GroundAtoms groundAtoms, Collection<? extends UserPredicate> predicates) {
    for (UserPredicate predicate : predicates) {
      GroundAtomCollection groundAtomCollection = atoms.get(predicate);
      groundAtomCollection.load(groundAtoms.getGroundAtomsOf(predicate));
    }
  }

  /**
   * This method populates this universe with the ground atoms from another universe.
   *
   * @param groundAtoms the universe from which to take the ground atoms. Must have the same signature.
   */
  public void load(GroundAtoms groundAtoms) {
    for (UserPredicate predicate : atoms.keySet()) {
      GroundAtomCollection groundAtomCollection = atoms.get(predicate);
      groundAtomCollection.load(groundAtoms.getGroundAtomsOf(predicate));
    }
  }

  /**
   * The signature of a universe holds meta information for the predicates and functions
   * stored in this universe.
   *
   * @return the signature of this universe.
   */
  public Signature getSignature() {
    return signature;
  }

  /**
   * Returns a collection of true ground atoms for the given predicate
   *
   * @param predicate the predicate we want to know the true ground atoms for.
   * @return a collection of ground atoms.
   */
  public GroundAtomCollection getGroundAtomsOf(UserPredicate predicate) {
    return atoms.get(predicate);
  }


  /**
   * Creates a deep copy of these atoms. Note that this does not mean an actual copy
   * is going on unless the copy will be modified (lazy copying).
   *
   * @return a deep copy of this object.
   */
  public GroundAtoms copy() {
    GroundAtoms result = signature.createGroundAtoms();
    result.load(this);
    return result;
  }


  /**
   * Load a set of ground atoms from the given string
   * @param src input string in PML data format
   */
  public void load(String src) {
    try {
      load(new ByteArrayInputStream(src.getBytes()));
    } catch (IOException e){
      //won't happen
    }
  }

  public void clear(Collection<UserPredicate> predicates){
    for (UserPredicate pred : predicates){
      getGroundAtomsOf(pred).clear();
    }
  }

  /**
   * Load a set of ground atoms from the given input stream.
   * @param is input stream with PML data format
   * @throws IOException in case something I/O-ish goes wrong.
   */
  public void load(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuffer buffer = new StringBuffer();
    String pred = null;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      if (line.charAt(0) == '>') {
        if (pred != null) {
          UserPredicate userPredicate = (UserPredicate) signature.getPredicate(pred);
          if (userPredicate == null)
            throw new RuntimeException("The predicate " + pred + " is not part of this signature");
          GroundAtomCollection atoms = getGroundAtomsOf(userPredicate);
          atoms.load(buffer.toString());
        }
        pred = line.substring(1);
        buffer.setLength(0);

      } else if (!line.trim().equals("")) {
        buffer.append(line).append("\n");
      }
    }
    GroundAtomCollection atoms = getGroundAtomsOf((UserPredicate) signature.getPredicate(pred));
    atoms.load(buffer.toString());
  }

  /**
   * Determines whether the ground atom collections for the specified predicates are all empty
   * @param predicates a collection of ground atoms
   * @return true iff the ground atom collection for the given predicates are all empty.
   */
  public boolean isEmpty(Collection<UserPredicate> predicates){
    for (UserPredicate predicate : predicates)
      if (!atoms.get(predicate).isEmpty()) return false;
    return true;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(">>\n");
    for (GroundAtomCollection atoms : this.atoms.values()) {
      result.append(atoms.toString());
    }
    return result.toString();
  }


}
