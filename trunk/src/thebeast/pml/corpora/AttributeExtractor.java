package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Type;
import thebeast.pml.UserPredicate;
import thebeast.pml.term.Constant;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Sebastian Riedel
 */
public class AttributeExtractor implements Extractor {

  private HashMap<Integer, Integer> mapping;
  private UserPredicate predicate;
  private Object[] args;
  private HashMap<Integer, TokenProcessor> processors = new HashMap<Integer, TokenProcessor>();
  private HashSet<Integer> lineNrArgs = new HashSet<Integer>();

  public AttributeExtractor(UserPredicate predicate, Map<Integer, Integer> mapping) {
    this.predicate = predicate;
    this.mapping = new HashMap<Integer, Integer>(mapping);
    args = new Object[mapping.size()];
  }

  public AttributeExtractor(UserPredicate predicate, int size) {
    this.predicate = predicate;
    this.mapping = new HashMap<Integer, Integer>();
    args = new Object[size];

  }

  public void addLineNrArg(int argIndex) {
    lineNrArgs.add(argIndex);
  }

  public void addMapping(int column, int argIndex) {
    mapping.put(column, argIndex);
    processors.put(column, new Itself());
  }

  public void addMapping(int column, int argIndex, TokenProcessor processor) {
    mapping.put(column, argIndex);
    processors.put(column, processor);
  }

  public void beginLine(int lineNr) {
    for (int i : lineNrArgs) {
      args[i] = lineNr;
    }
  }

  public void endLine(GroundAtoms atoms) {
    atoms.getGroundAtomsOf(predicate).addGroundAtom(args);
  }

  public void endSentence(GroundAtoms atoms) {

  }

  public void extract(int column, String value) {
    int argIndex = mapping.get(column);
    Type type = predicate.getArgumentTypes().get(argIndex);
    args[argIndex] = type.getObject(processors.get(column).process(value));
  }

  public Collection<Integer> getColumns() {
    return processors.keySet();
  }
}
