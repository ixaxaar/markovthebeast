package thebeast.pml;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * A GroundAtoms object represents a collection of ground atoms for the predicates of a certain signature. It also
 * stores a set of weight functions to be used in PML models.
 */
public class GroundAtoms implements Dumpable, SignatureListener {

  private Signature signature;
  private Map<UserPredicate, GroundAtomCollection> atoms = new TreeMap<UserPredicate, GroundAtomCollection>();

  GroundAtoms(Signature signature) {
    this.signature = signature;
    for (UserPredicate predicate : signature.getUserPredicates()) {
      atoms.put(predicate, new GroundAtomCollection(predicate));
    }
    //signature.addSignatureListener(this);
  }

  /**
   * Copy constructor.
   *
   * @param atoms the atoms to copy.
   */
  public GroundAtoms(GroundAtoms atoms) {
    this(atoms.getSignature());
    load(atoms);
  }

  /**
   * This method populates this universe with the ground atoms from another universe for the specified predicates.
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
   * The signature of a universe holds meta information for the predicates and functions stored in this universe.
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
   * Returns a collection of true ground atoms for the given predicate
   *
   * @param predicate the name of the predicate we want to know the true ground atoms for.
   * @return a collection of ground atoms.
   */
  public GroundAtomCollection getGroundAtomsOf(String predicate) {
    return atoms.get(signature.getUserPredicate(predicate));
  }

  /**
   * Creates a deep copy of these atoms. Note that this does not mean an actual copy is going on unless the copy will be
   * modified (lazy copying).
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
   *
   * @param src input string in PML data format
   */
  public void load(String src) {
    try {
      load(new ByteArrayInputStream(src.getBytes()));
    } catch (IOException e) {
      //won't happen
    }
  }

  /**
   * Clears the ground atoms for the given predicates.
   *
   * @param predicates the predicates to remove the ground atoms of.
   */
  public void clear(Collection<UserPredicate> predicates) {
    for (UserPredicate pred : predicates) {
      getGroundAtomsOf(pred).clear();
    }
  }

  /**
   * Load a set of ground atoms from the given input stream.
   *
   * @param is input stream with PML data format
   * @throws IOException in case something I/O-ish goes wrong.
   */
  public void load(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuffer buffer = new StringBuffer();
    String pred = null;
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.length() > 1 && line.charAt(0) == '>') {
        if (pred != null) {
          UserPredicate userPredicate = (UserPredicate) signature.getPredicate(pred);
          if (userPredicate == null)
            throw new RuntimeException("The predicate " + pred + " is not part of this signature");
          GroundAtomCollection atoms = getGroundAtomsOf(userPredicate);
          atoms.load(buffer.toString());
        }
        pred = line.substring(1).trim();
        buffer.setLength(0);

      } else if (!line.equals("")) {
        buffer.append(line).append("\n");
      }
    }
    GroundAtomCollection atoms = getGroundAtomsOf((UserPredicate) signature.getPredicate(pred));
    atoms.load(buffer.toString());
  }

  /**
   * Load a set of ground atoms from the given input stream.
   *
   * @param is        input stream with PML data format
   * @param predicate the predicate for which we want to load ground atoms.
   * @throws IOException in case something I/O-ish goes wrong.
   */
  public void load(InputStream is, UserPredicate predicate) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (line.length() > 1 && line.charAt(0) == '>') {
        String pred = line.substring(1);
        UserPredicate userPredicate = (UserPredicate) signature.getPredicate(pred);
        if (userPredicate.equals(predicate)) {
          StringBuffer buffer = new StringBuffer();
          int lineNr = 0;
          for (line = reader.readLine(); line != null && !line.equals("") && !line.startsWith(">"); line = reader.readLine()){
            buffer.append(line).append("\n");
            //System.out.println(lineNr++ + ":" + line);
          }
          GroundAtomCollection atoms = getGroundAtomsOf(userPredicate);
          atoms.load(buffer.toString());
          return;
        }
      }
    }
  }

  /**
   * Determines whether the ground atom collections for the specified predicates are all empty
   *
   * @param predicates a collection of ground atoms
   * @return true iff the ground atom collection for the given predicates are all empty.
   */
  public boolean isEmpty(Collection<UserPredicate> predicates) {
    for (UserPredicate predicate : predicates)
      if (!atoms.get(predicate).isEmpty()) return false;
    return true;
  }


  /**
   * Returns the (approximate) size of all contained ground atom collections in bytes.
   *
   * @return the size in bytes.
   */
  public int getMemoryUsage() {
    int byteSize = 0;
    for (Map.Entry<UserPredicate, GroundAtomCollection> entry : atoms.entrySet())
      byteSize += entry.getValue().getUsedMemory();
    return byteSize;
  }

  /**
   * Returns a string that shows all ground atoms in column form.
   *
   * @return a string in column form displaying all ground atoms for all predicates.
   */
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(">>\n");
    for (GroundAtomCollection atoms : this.atoms.values()) {
      result.append(atoms.toString());
    }
    return result.toString();
  }

  /**
   * Dump all ground atoms to a Database dump store. This is the fastest and most memory efficient way of storing ground
   * atoms.
   *
   * @param fileSink a database dump.
   * @throws java.io.IOException if I/O goes wrong
   */
  public void write(FileSink fileSink) throws IOException {
    for (UserPredicate predicate : signature.getUserPredicates()) {
      atoms.get(predicate).write(fileSink);
    }
  }

  /**
   * Loads ground atoms from a database dump.
   *
   * @param fileSource the dump to load from.
   * @throws java.io.IOException if I/O goes wrong.
   */
  public void read(FileSource fileSource) throws IOException {
    for (UserPredicate predicate : signature.getUserPredicates()) {
      atoms.get(predicate).read(fileSource);
    }
  }


  /**
   * Return the total number of ground atoms for all predicates.
   *
   * @return the sum of ground atom counts of all predicates.
   */
  public int getGroundAtomCount() {
    int count = 0;
    for (GroundAtomCollection atoms : this.atoms.values())
      count += atoms.size();
    return count;
  }

  /**
   * Called when the signature has added a predicate. Will ensure that this object is consistent with the update
   * signature.
   *
   * @param predicate the predicate which has been added.
   */
  public void predicateAdded(UserPredicate predicate) {
    atoms.put(predicate, new GroundAtomCollection(predicate));
  }

  public void detach() {
    signature.removeSignatureListener(this);
  }

}
