package thebeast.pml;

import thebeast.pml.formula.FactorFormula;

import java.util.*;

/**
 * @author Sebastian Riedel
 */
public class Model {

  private Signature signature;

  private LinkedList<UserPredicate>
          hidden = new LinkedList<UserPredicate>(),
          observed = new LinkedList<UserPredicate>(),
          globalPreds = new LinkedList<UserPredicate>();

  private LinkedList<FactorFormula>
          factorFormulas = new LinkedList<FactorFormula>(),
          localFactorFormulas = new LinkedList<FactorFormula>(),
          globalFactorFormulas = new LinkedList<FactorFormula>();

  /**
   * Creates a new Model with the given signature, i.e. it only contains predicates and functions
   * which are described in the signature object.
   *
   * @param signature the signature the predicates of this model use.
   */
  Model(Signature signature) {
    this.signature = signature;
  }

  /**
   * A hidden predicate is a predicate for which we don't have observed ground atoms. Instead, the solver
   * is responsible for inferring the true ground atoms of the given predicate.
   *
   * @param predicate the predicate to be defined as hidden.
   */
  public void addHiddenPredicate(UserPredicate predicate) {
    hidden.add(predicate);
    Collections.sort(hidden);
  }

  /**
   * An observed predicate is a predicate for which we have observed data. The solver will take these as
   * ground truth.
   *
   * @param predicate the predicate to be defined as observed.
   */
  public void addObservedPredicate(UserPredicate predicate) {
    observed.add(predicate);
    Collections.sort(observed);
  }

  /**
   * A global predicate is a predicate for which the same ground atoms hold over a whole corpus, not
   * just for a single problem instance.
   *
   * @param predicate the predicate to be defined as global.
   */
  public void addGlobalPredicate(UserPredicate predicate) {
    globalPreds.add(predicate);
    Collections.sort(globalPreds);
  }


  /**
   * All formulas of this model contain predicates and functions of the same signature. This method
   * returns this signature.
   *
   * @return The common signature containing all used predicates in this model.
   */
  public Signature getSignature() {
    return signature;
  }

  /**
   * Adds a factor formula to this model.
   *
   * @param factorFormula the formula to add.
   */
  public void addFactorFormula(FactorFormula factorFormula) {
    factorFormulas.add(factorFormula);
    if (factorFormula.isLocal()) {
      localFactorFormulas.add(factorFormula);
    } else {
      globalFactorFormulas.add(factorFormula);
    }
  }

  /**
   * Returns all factor formulas.
   *
   * @return all factor formulas contained in this model.
   */
  public List<FactorFormula> getFactorFormulas() {
    return factorFormulas;
  }

  /**
   * Get all formulas that only contain one atom.
   *
   * @return the local formulae of this model.
   */
  public List<FactorFormula> getLocalFactorFormulas() {
    return localFactorFormulas;
  }

  /**
   * Get the formulas that contain more than one atom.
   * @return the global factor formulas.
   */
  public LinkedList<FactorFormula> getGlobalFactorFormulas() {
    return globalFactorFormulas;
  }

  public void validateModel(){
    if (hidden.size() == 0)
      throw new RuntimeException("Model does not contain any hidden predicates -> senseless");
  }


  public List<UserPredicate> getHiddenPredicates() {
    return hidden;
  }

  public List<UserPredicate> getObservedPredicates() {
    return observed;
  }

  public List<UserPredicate> getGlobalPredicates() {
    return globalPreds;
  }
}
