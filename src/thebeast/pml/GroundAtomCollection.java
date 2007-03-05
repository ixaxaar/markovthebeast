package thebeast.pml;

import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.TupleValue;
import thebeast.nod.variable.BoolVariable;
import thebeast.nod.variable.RelationVariable;
import thebeast.nod.variable.Index;
import thebeast.nod.type.Attribute;
import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.pml.term.Constant;
import thebeast.pml.predicate.PredicateIndex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Jan-2007 Time: 17:03:18
 */
public class GroundAtomCollection extends AbstractCollection<GroundAtom> {

  private RelationVariable relation;
  private UserPredicate predicate;
  private Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
  private ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

  /**
   * Creates a new collection of ground atoms for the given predicate.
   *
   * @param predicate the predicate to create the collection for.
   */
  GroundAtomCollection(UserPredicate predicate) {
    this.predicate = predicate;
    this.relation = interpreter.createRelationVariable(predicate.getHeading());
    for (Attribute attribute : predicate.getHeading().attributes())
      interpreter.addIndex(relation, attribute.name(), Index.Type.HASH, attribute.name());
    int indexNr = 0;
    for (PredicateIndex index : predicate.getIndices()){
      ArrayList<String> attNames = new ArrayList<String>();
      int i = 0;
      for (Attribute attribute : predicate.getHeading().attributes()){
        if (index.getMarkers().get(i++))
          attNames.add(attribute.name());
      }
      interpreter.addIndex(relation, "index" + indexNr++, Index.Type.HASH, attNames);
    }
    this.relation.setLabel(predicate.getName());
  }

  /**
   * A universe stores ground atoms in the form of tuples in a relational database. For each predicate there exist
   * exactly one relational table in the database. This method returns it.
   *
   * @return the table/"relational variable" for the given predicate.
   */
  public RelationVariable getRelationVariable() {
    update();
    return relation;
  }

  /**
   * removes all ground atoms.
   */
  public void clear() {
    interpreter.clear(relation);
  }

  /**
   * Adds a ground atom to this collection.
   *
   * @param arguments the arguments of the atoms. Can be strings, doubles or integers.
   */
  public void addGroundAtom(Object... arguments) {
    int index = 0;
    builder.clear();
    for (Type type : predicate.getArgumentTypes()) {
      builder.id(predicate.getColumnName(index)).constant(type.getNodType(), arguments[index++]);
    }
    builder.tupleForIds().relation(1);
    interpreter.insert(relation, builder.getRelation());
  }


  /**
   * Adds a ground atom to this collection.
   *
   * @param arguments the arguments of the atoms. Can be strings, doubles or integers.
   */
  public void addGroundAtom(Constant... arguments) {
    builder.clear();
    for (int index = 0; index < arguments.length; ++index) {
      builder.id(predicate.getColumnName(index)).expr(arguments[index].toScalar());
    }
    builder.tupleForIds().relation(1);
    interpreter.insert(relation, builder.getRelation());
  }


  /**
   * Iterates over all ground atoms in this collection
   *
   * @return an iterator (non-mutable) over all ground atoms of this collection
   */
  public Iterator<GroundAtom> iterator() {
    return new Iterator<GroundAtom>() {
      private Iterator<TupleValue> delegate = relation.value().iterator();

      public boolean hasNext() {
        return delegate.hasNext();
      }

      public GroundAtom next() {
        return new GroundAtom(predicate, delegate.next());
      }

      public void remove() {

      }
    };
  }


  /**
   * Converts objects into constants and determines whether the collection contains the corresponding atom.
   *
   * @param arguments an array of objects, either strings (for categorical constants), ints or doubles.
   * @return true iff the collection contains an atom with arguments corresponding to the given parameter.
   */
  public boolean containsAtom(Object... arguments) {
    update();
    int index = 0;
    builder.clear();
    builder.expr(relation);
    for (Type type : predicate.getArgumentTypes()) {
      builder.id(predicate.getColumnName(index)).constant(type.getNodType(), arguments[index++]);
    }
    builder.tupleForIds();
    builder.contains();
    BoolVariable var = interpreter.createBoolVariable(builder.getBool());
    return var.value().getBool();
  }

  /**
   * Calculates and returns the number of ground atoms in this collection.
   *
   * @return the number of ground atoms.
   */
  public int size() {
    update();
    return relation.value().size();
  }

  /**
   * Prints ground atoms in table form
   *
   * @return a string containing a table of atoms
   */
  public String toString() {
    StringBuffer result = new StringBuffer(">" + predicate.getName() + "\n");
    for (GroundAtom atom : this) {
      result.append(atom.toString()).append("\n");
    }
    return result.toString();
  }


  /**
   * Determines whether there are any ground atoms in this collection.
   *
   * @return true iff there are no atoms in this collection.
   */
  public boolean isEmpty() {
    update();
    return relation.value().size() == 0;
  }

  /**
   * Loads another atom collection into this collection
   *
   * @param other a ground atom collection for the same predicate.
   */
  public void load(GroundAtomCollection other) {
    interpreter.assign(relation, other.getRelationVariable());
  }

  public void load(String tuples) throws IOException {
    interpreter.load(relation, new ByteArrayInputStream(tuples.getBytes()));
  }

  private void update() {
    if (!builder.isEmpty()) interpreter.insert(relation, builder.relation().getRelation());
  }

  /**
   * Returns the (approximate) size of this collection in bytes.
   *
   * @return the size in bytes.
   */
  public int getUsedMemory() {
    return relation.byteSize();
  }


  /**
   * Dumps these ground atoms to the database dump.
   *
   * @param fileSink a database dump store.
   * @throws java.io.IOException if I/O goes wrong.
   */
  public void write(FileSink fileSink) throws IOException {
    fileSink.write(relation);
  }

  /**
   * Read ground atoms from a database dump store.
   *
   * @param fileSource the store to load from.
   * @throws IOException if I/O goes wring.
   */
  public void read(FileSource fileSource) throws IOException {
    fileSource.read(relation);
  }
}
