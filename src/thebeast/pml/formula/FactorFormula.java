package thebeast.pml.formula;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.TypeFactory;
import thebeast.pml.Quantification;
import thebeast.pml.TheBeast;
import thebeast.pml.UserPredicate;
import thebeast.pml.function.WeightFunction;
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

  private Heading headingSolution;

  private static Attribute indexAttribute, weightAttribute;
  private static final TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
  private Heading headingILP, heading;

  static {
    indexAttribute = factory.createAttribute("index", factory.intType());
    weightAttribute = factory.createAttribute("weight", factory.doubleType());

  }

  public FactorFormula(Quantification quantification, BooleanFormula condition,
                       BooleanFormula formula, Term weight){
    this(null,quantification, condition, formula, weight);
  }

  public FactorFormula(String name, Quantification quantification, BooleanFormula condition,
                       BooleanFormula formula, Term weight) {
    this.quantification = quantification;
    this.condition = condition;
    this.formula = formula;
    this.weight = weight;
    this.name = name;

    LinkedList<Attribute> varAttributes = new LinkedList<Attribute>();
    int index = 0;
    for (Variable var : quantification.getVariables()){
      varAttributes.add(factory.createAttribute("var" + index++, var.getType().getNodType()));
    }

    if (isParametrized()) varAttributes.add(indexAttribute);
    //varAttributes.add(weightAttribute);

    headingSolution = factory.createHeadingFromAttributes(varAttributes);

    LinkedList<Attribute> ilpAttributes = new LinkedList<Attribute>(varAttributes);
    ilpAttributes.add(weightAttribute);

    headingILP = factory.createHeadingFromAttributes(ilpAttributes);
  }

  public boolean isLocal(){
    return formula instanceof PredicateAtom;
  }

  public boolean isDeterministic() {
    if (!(weight instanceof DoubleConstant)) return false;
    DoubleConstant constant = (DoubleConstant) weight;
    return constant.getValue() == Double.POSITIVE_INFINITY || constant.getValue() == Double.NEGATIVE_INFINITY;
  }

  public boolean isAcyclicityConstraint(){
    return formula instanceof AcyclicityConstraint;
  }

  public Quantification getQuantification() {
    return quantification;
  }

  public UserPredicate getLocalPredicate(){
    return (UserPredicate) ((PredicateAtom)formula).getPredicate();
  }

  public AcyclicityConstraint getAcyclicityConstraint(){
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

  public String toString(){
    return (quantification.getVariables().size() > 0  ? "FOR " + quantification : "")
            + (condition != null ? " IF " + condition + " ": "") +
            (!isDeterministic() ? " ADD [" + formula + "] * " + weight : ": " + formula);

  }

  public Heading getSolutionHeading() {
    return headingSolution;
  }

  public Heading getHeadingIndex() {
    return headingSolution;
  }

  public Heading getHeadingILP() {
    return headingILP;
  }


  public boolean isParametrized() {
    return weight instanceof FunctionApplication &&
            ((FunctionApplication)weight).getFunction() instanceof WeightFunction;
  }

  /**
   * returs the name of this formula
   * @return the name of this formula or null if it's an anonymous formula.
   */
  public String getName() {
    return name;
  }

  /**
   * Return the weight function for this factors weight term in case it's a parametrized
   * factor with a function application weight term.
   * @return the weight function of the weight term (if this is a parametrized factor).
   */
  public WeightFunction getWeightFunction(){
    return (WeightFunction) ((FunctionApplication)weight).getFunction();
  }
}
