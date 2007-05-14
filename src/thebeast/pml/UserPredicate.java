package thebeast.pml;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.TypeFactory;
import thebeast.pml.predicate.Predicate;
import thebeast.pml.predicate.PredicateVisitor;
import thebeast.pml.predicate.PredicateIndex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:14:32
 */
public class UserPredicate extends Predicate {

  private Heading heading, headingScores, headingTmpScores, headingSolution;
  protected static Attribute scoreAttribute;
  protected static Attribute indexAttribute;
  protected static Attribute featureIndicesAttribute;
  protected static Attribute solutionAttribute;
  private LinkedList<Attribute> attributes;
  private LinkedList<PredicateIndex> indices = new LinkedList<PredicateIndex>();

  private int sequentialColumn = -1;

  private static final String INDEX_COL_NAME = "index";
  private Heading headingIndex;
  private Heading headingForScore;
  private Heading headingCycle;
  private Heading headingCycles;
  private Heading headingGroupedFeatures;

  static {
    TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
    indexAttribute = factory.createAttribute(INDEX_COL_NAME, factory.intType());
    scoreAttribute = factory.createAttribute("score", factory.doubleType());
    solutionAttribute = factory.createAttribute("solution", factory.doubleType());
    LinkedList<Attribute> indexTableAttributes = new LinkedList<Attribute>();
    indexTableAttributes.add(indexAttribute);
    RelationType relType = factory.createRelationType(factory.createHeadingFromAttributes(indexTableAttributes));
    featureIndicesAttribute = factory.createAttribute("features", relType);
  }


  /**
   * Creates a new predicate with the given name and argument types.
   *
   * @param name          the name of the predicate.
   * @param argumentTypes the list of argument types.
   */
  protected UserPredicate(String name, List<Type> argumentTypes) {
    this(name, argumentTypes, -1);
  }


  /**
   * Creates a new predicate with the given name and argument types.
   *
   * @param name             the name of the predicate.
   * @param argumentTypes    the list of argument types.
   * @param sequentialColumn the column/argument which will contain a contiguous set of integers in each
   *                         ground atom collection. -1 if there is no such column. The Column type must be integer.
   */
  protected UserPredicate(String name, List<Type> argumentTypes, int sequentialColumn) {
    super(name, argumentTypes);
    this.sequentialColumn = sequentialColumn;
    this.name = name;
    this.argumentTypes = new ArrayList<Type>(argumentTypes);
    TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
    attributes = new LinkedList<Attribute>();
    int index = 0;
    for (Type type : argumentTypes) {
      attributes.add(factory.createAttribute(getColumnName(index++), type.getNodType()));
    }
    heading = factory.createHeadingFromAttributes(attributes);


    LinkedList<Attribute> attributesForScores = new LinkedList<Attribute>(attributes);
    attributesForScores.add(featureIndicesAttribute);
    attributesForScores.add(scoreAttribute);

    headingScores = factory.createHeadingFromAttributes(attributesForScores);

    LinkedList<Attribute> attributesForTmpScores = new LinkedList<Attribute>(attributes);
    attributesForTmpScores.add(indexAttribute);
    attributesForTmpScores.add(scoreAttribute);

    headingTmpScores = factory.createHeadingFromAttributes(attributesForTmpScores);

    LinkedList<Attribute> attributesForSolution = new LinkedList<Attribute>(attributes);
    attributesForSolution.add(featureIndicesAttribute);
    attributesForSolution.add(solutionAttribute);

    headingSolution = factory.createHeadingFromAttributes(attributesForSolution);

    LinkedList<Attribute> attributesForIndex = new LinkedList<Attribute>(attributes);
    attributesForIndex.add(indexAttribute);

    headingIndex = factory.createHeadingFromAttributes(attributesForIndex);

    LinkedList<Attribute> attributesForGroupedFeatures = new LinkedList<Attribute>(attributes);
    attributesForGroupedFeatures.add(featureIndicesAttribute);
    headingGroupedFeatures = factory.createHeadingFromAttributes(attributesForGroupedFeatures);

    LinkedList<Attribute> attributesForRealScores = new LinkedList<Attribute>(attributes);
    attributesForRealScores.add(scoreAttribute);
    headingForScore = factory.createHeadingFromAttributes(attributesForRealScores);

    LinkedList<Attribute> attributesForCycle = new LinkedList<Attribute>();
    attributesForCycle.add(factory.createAttribute("cycle", factory.createRelationType(heading)));
    headingCycle = factory.createHeadingFromAttributes(attributesForCycle);

    LinkedList<Attribute> attributeForCycles = new LinkedList<Attribute>();
    attributeForCycles.add(factory.createAttribute("cycles", factory.createRelationType(headingCycle)));
    headingCycles = factory.createHeadingFromAttributes(attributeForCycles);

  }


  /**
   * Get the argument index for which, in each ground atom collection, the set of arguments of all ground atoms
   * for this index will be a contiguous set of integers (i.e. from f,f+1,f+2,f+3 to t-2,t-1,t).
   *
   * @return the sequential column or -1 if there is no such column.
   */
  public int getSequentialColumn() {
    return sequentialColumn;
  }

  /**
   * Get the name of this predicate.
   *
   * @return this predicate's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the list of argument types for this predicate.
   *
   * @return a list of types corresponding to the types of the arguments of this predicate.
   */
  public List<Type> getArgumentTypes() {
    return argumentTypes;
  }

  /**
   * Returns a heading for a plain ground atom
   *
   * @return a relational heading for representing ground atoms of this predicate in a relational table.
   */
  public Heading getHeading() {
    return heading;
  }

  /**
   * Returns the name of the column that stores the <code>argument</code>th's argument of this predicate.
   *
   * @param argument the index of the argument we want the name for.
   * @return the No-D name for the given argument index.
   */
  public String getColumnName(int argument) {
    return "arg_" + argument;
  }

  /**
   * Returns the attribute representing an argument
   *
   * @param argument the index of the argument we want the attribute for.
   * @return the attribute that is used to store arguments of the given index.
   */
  public Attribute getAttribute(int argument) {
    return attributes.get(argument);
  }


  /**
   * Adds an index for this predicate. This means in every collection of ground atoms for this
   * predicate there will be an index to make access of values fast.
   *
   * @param markers a list boolean markers, one for each argument of this predicate.
   *                If <code>markers.get(i)==true</code> the argument <code>i</code> is part of the index. Otherwise
   *                it is not.
   */
  public void addIndex(List<Boolean> markers) {
    if (markers.size() != getArity())
      throw new IllegalArgumentException("Number of index markers must match arity of predicate!");
    indices.add(new PredicateIndex(markers));
  }


  /**
   * Return the index definitions for this predicate.
   *
   * @return a list of index definition.
   */
  public List<PredicateIndex> getIndices() {
    return indices;
  }

  /**
   * Returns a heading for scored atoms.
   *
   * @return a heading for a table that represents a ground atom of this predicate , a list of active feature indices
   *         for this atom and a score.
   */
  public Heading getHeadingForFeaturesAndScores() {
    return headingScores;
  }

  /**
   * A heading for storing ground atoms of this predicate together with a feature index and a score.
   *
   * @return a relation heading with attributes corresponding to the ground arguments of the predicate, a ground atom
   *         score and an index.
   */
  public Heading getHeadingForTmpScores() {
    return headingTmpScores;
  }

  /**
   * A heading for storing ground atoms of this predicate together with a index ("index") and a score ("score").
   *
   * @return a relation heading with attributes corresponding to the ground arguments of the predicate, a ground atom
   *         score and an index.
   */
  public Heading getHeadingArgsIndexScore() {
    return headingTmpScores;
  }

  public static Attribute getScoreAttribute() {
    return scoreAttribute;
  }

  public static Attribute getFeatureIndexAttribute() {
    return indexAttribute;
  }

  public static Attribute getFeatureIndicesAttribute() {
    return featureIndicesAttribute;
  }

  public void acceptPredicateVisitor(PredicateVisitor visitor) {
    visitor.visitUserPredicate(this);
  }

  public Heading getHeadingForSolutions() {
    return headingSolution;
  }

  public Heading getHeadingIndex() {
    return headingIndex;
  }


  public Heading getHeadingForScore() {
    return headingForScore;
  }

  public Heading getHeadingForFeatures() {
    return headingIndex;
  }

  public static Attribute getSolutionAttribute() {
    return solutionAttribute;
  }

  public Heading getHeadingCycle() {
    return headingCycle;
  }

  public Heading getHeadingCycles() {
    return headingCycles;
  }


  public Heading getHeadingGroupedFeatures() {
    return headingGroupedFeatures;
  }
}
