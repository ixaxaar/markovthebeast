package thebeast.pml.formula;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.TypeFactory;
import thebeast.pml.Quantification;
import thebeast.pml.TheBeast;
import thebeast.pml.UserPredicate;
import thebeast.pml.Type;
import thebeast.pml.function.WeightFunction;
import thebeast.pml.function.DoubleProduct;
import thebeast.pml.term.DoubleConstant;
import thebeast.pml.term.Term;
import thebeast.pml.term.Variable;
import thebeast.pml.term.FunctionApplication;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:19:29
 */
public class FactorFormula {

  private Quantification quantification;
  private BooleanFormula condition;
  private BooleanFormula formula;
  private Term weight;
  private String name;
  private String toString;
  private int order = 0;
  private boolean ground = false;

  private Heading headingSolution, headingIndex;

  private static Attribute indexAttribute, weightAttribute, scaleAttribute;
  private static final TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
  private Heading headingILP;

  static {
    indexAttribute = factory.createAttribute("index", factory.intType());
    weightAttribute = factory.createAttribute("weight", factory.doubleType());
    scaleAttribute = factory.createAttribute("scale",factory.doubleType());

  }

  public FactorFormula(Quantification quantification, BooleanFormula condition,
                       BooleanFormula formula, Term weight) {
    this("formula", quantification, condition, formula, weight);
  }

  public FactorFormula(String name, Quantification quantification, BooleanFormula condition,
                       BooleanFormula formula, Term weight) {
    if (quantification.getVariables().size() == 0)
      quantification = new Quantification(new Variable(Type.SINGLETON, "singleton"));
    this.quantification = quantification;
    this.condition = condition;
    this.formula = formula;
    this.weight = weight;
    this.name = name == null ? "formula" : name;

//    if (isLocal() && isParametrized() && (weight.isNonNegative() || weight.isNonPositive()))
//      throw new RuntimeException("We don't support local features with non-free weights.");

    LinkedList<Attribute> varAttributes = new LinkedList<Attribute>();
    int index = 0;
    for (Variable var : quantification.getVariables()) {
      varAttributes.add(factory.createAttribute("var" + index++, var.getType().getNodType()));
    }

    LinkedList<Attribute> solutionAttributes = new LinkedList<Attribute>(varAttributes);
    LinkedList<Attribute> ilpAttributes = new LinkedList<Attribute>(varAttributes);
    LinkedList<Attribute> indexAttributes = new LinkedList<Attribute>(varAttributes);

    if (usesWeights()) {
      solutionAttributes.add(indexAttribute);
      solutionAttributes.add(scaleAttribute);
      indexAttributes.add(indexAttribute);
      ilpAttributes.add(indexAttribute);
    }
    ilpAttributes.add(weightAttribute);
    //varAttributes.add(weightAttribute);

    headingSolution = factory.createHeadingFromAttributes(solutionAttributes);
    headingIndex = factory.createHeadingFromAttributes(indexAttributes);
    headingILP = factory.createHeadingFromAttributes(ilpAttributes);

    toString = (name != null ? (name + ":") : "") + (quantification.getVariables().size() > 0 ? "FOR " + quantification : "")
            + (condition != null ? " IF " + condition + " " : "") +
            (!isDeterministic() ? " ADD [" + formula + "] * " + weight : ": " + formula);

  }

  /**
   * Some formulas only score single hidden atoms (based on some observed atoms), these are said to be local. If local,
   * the formula part of the factor is a single atom and the condition can be anything.
   *
   * @return true if this formula is local.
   */
  public boolean isLocal() {
    return formula instanceof PredicateAtom;
  }

  /**
   * If the formula has infinitive weight it is said to be deterministic (note that strictly speaking this causes the
   * loglinear model to stop being a Markov Network (Hammersley-Clifford). However, in the context of MAP inference with
   * ILP this does not really matter.
   *
   * @return true if formula is deterministic.
   */
  public boolean isDeterministic() {
    if (!(weight instanceof DoubleConstant)) return false;
    DoubleConstant constant = (DoubleConstant) weight;
    return constant.getValue() == Double.POSITIVE_INFINITY || constant.getValue() == Double.NEGATIVE_INFINITY;
  }

  public boolean isAcyclicityConstraint() {
    return formula instanceof AcyclicityConstraint;
  }

  public Quantification getQuantification() {
    return quantification;
  }

  public UserPredicate getLocalPredicate() {
    return (UserPredicate) ((PredicateAtom) formula).getPredicate();
  }

  public AcyclicityConstraint getAcyclicityConstraint() {
    return (AcyclicityConstraint) formula;
  }

  public BooleanFormula getCondition() {
    return condition;
  }

  public BooleanFormula getFormula() {
    return formula;
  }

  public Term getWeight() {
    return weight;
  }

  public String toString() {
    return toString;
  }

  public String toShortString() {
    if (!name.equals("formula")) return name;
    return toString.length() > 30 ? toString.substring(toString.length() - 30) : toString;
  }

  public Heading getSolutionHeading() {
    return headingSolution;
  }

  public Heading getHeadingIndex() {
    return headingIndex;
  }

  public Heading getHeadingILP() {
    return headingILP;
  }


  /**
   * Determines whether this factor formula uses a weight function or some static term (say some external scores)
   *
   * @return true iff this factor formula uses a weight function.
   */
  public boolean usesWeights() {
    return weight.usesWeights();
  }

  /**
   * returs the name of this formula
   *
   * @return the name of this formula or null if it's an anonymous formula.
   */
  public String getName() {
    return name;
  }

  /**
   * Return the weight function for this factors weight term in case it's a parametrized factor with a function
   * application weight term.
   *
   * @return the weight function of the weight term (if this is a parametrized factor).
   */
  public WeightFunction getWeightFunction() {
    FunctionApplication app = (FunctionApplication) weight;
    if (app.getFunction() instanceof DoubleProduct)
      return (WeightFunction) ((FunctionApplication)app.getArguments().get(1)).getFunction();
    return (WeightFunction) app.getFunction();
  }

  /**
   * If this a generator formula for auxilaries this method returns the target user predicate this formula generates
   * ground atoms for.
   *
   * @return the user predicate this generator generate ground atoms for.
   */
  public UserPredicate getGeneratorTarget() {
    Implication implication = (Implication) formula;
    return (UserPredicate) ((PredicateAtom) implication.getConclusion()).getPredicate();
  }


  public boolean isGround() {
    return ground;
  }

  public void setGround(boolean ground) {
    this.ground = ground;
  }

  /**
   * We can define an order on formulas which can be used by inference and learning methods. The order of a formula is
   * an integer number that is used to sort formulas.
   *
   * @return the order of a formula.
   */
  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }
}
