package thebeast.pml;

import thebeast.pml.predicate.Predicate;
import thebeast.pml.predicate.Equals;
import thebeast.pml.predicate.NotEquals;
import thebeast.pml.function.WeightFunction;
import thebeast.pml.function.Function;

import java.util.*;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Jan-2007 Time: 14:15:15
 */
public class Signature {

  private HashMap<String, Predicate> name2predicate = new HashMap<String, Predicate>();

  private HashMap<String, WeightFunction> name2weightFunction = new HashMap<String, WeightFunction>();

  private HashMap<String, Type> name2type = new HashMap<String, Type>();

  private LinkedList<UserPredicate> userPredicates = new LinkedList<UserPredicate>();
  private LinkedList<WeightFunction> weightFunctions = new LinkedList<WeightFunction>();


  public Signature(){
    name2type.put(Type.DOUBLE.getName(), Type.DOUBLE);
    name2type.put(Type.INT.getName(), Type.INT);
  }

  /**
   * A signature describes a set of types, predicates and functions. 
   * @return a universe which can be populated with ground atoms of this signature's predicates and functions.
   */
  public GroundAtoms createGroundAtoms(){
    return new GroundAtoms(this);
  }

  /**
   * The weight functions this signature stores are instantiated via a Weights object. This method
   * creates such an object. 
   * @return a database of weight function mappings
   */
  public Weights createWeights(){
    return new Weights(this);
  }

  /**
   * Creates a new model belonging to this signature.
   * @return a model that has to only contain formulas included in this signature.
   */
  public Model createModel(){
    return new Model(this);
  }

  /**
   * Creates a categorical type with a set of constants
   * @param name the name of the type
   * @param withUnknowns if true the types matches unknown strings to a designated UNKNOWN constant.
   * @param constants the list of constants
   * @return a type object representing a categorical type with the specified constants.
   */
  public Type createType(String name, boolean withUnknowns, List<String> constants) {
    Type result = new Type(name,constants, withUnknowns);
    name2type.put(name,result);
    return result;
  }

  /**
   * Convenience method for {@link Signature#createType(String, boolean, java.util.List)}
   * @param name the name of the type
   * @param withUnknowns if true the types matches unknown strings to a designated UNKNOWN constant.
   * @param constants the array of constants
   * @return a type object representing a categorical type with the specified constants.
   */
  public Type createType(String name, boolean withUnknowns, String... constants) {
    return createType(name, withUnknowns, Arrays.asList(constants));
  }

  /**
   * A signature stores a set of predicates. Get them with this method.
   * @return an unsorted collection of all predicates in this signature.
   */
  public Collection<Predicate> getPredicates(){
    return name2predicate.values();
  }

  /**
   * Getting the type for a given name
   * @param name the name of the type
   * @return the type for the name
   */
  public Type getType(String name) {
    return name2type.get(name);
  }

  /**
   * Create a new weight function with the given signature.
   * @param name the name of the weight function
   * @param argumentTypes the list of argument types
   * @return a weight function with the given signature
   */
  public WeightFunction createWeightFunction(String name, List<Type> argumentTypes){
    WeightFunction result = new WeightFunction(name, argumentTypes);
    name2weightFunction.put(name, result);
    weightFunctions.add(result);
    Collections.sort(weightFunctions);
    return result;
  }

  /**
   * Convenience method to create a new weight function with the given signature by
   * passing a vararg array.
   * @param name the name of the weight function
   * @param argumentTypes the list of argument types
   * @return a weight function with the given signature
   */
  public WeightFunction createWeightFunction(String name, Type ... argumentTypes){
    return createWeightFunction(name, Arrays.asList(argumentTypes));
  }

  /**
   * Create a new bounded weight function with the given signature.
   * @param name the name of the weight function
   * @param positive if true the weight function will only map to non-negative reals, if
   * false it will only map to non-positive reals. 
   * @param argumentTypes the list of argument types
   * @return a weight function with the given signature
   */
  public WeightFunction createWeightFunction(String name, boolean positive, List<Type> argumentTypes){
    WeightFunction result = new WeightFunction(name, positive, argumentTypes);
    name2weightFunction.put(name, result);
    weightFunctions.add(result);
    Collections.sort(weightFunctions);
    return result;
  }

  public WeightFunction createWeightFunction(String name, boolean positive, Type ... argumentTypes){
    return createWeightFunction(name, positive,Arrays.asList(argumentTypes));
  }

  /**
   * Returns all weight functions defined in this signature.
   * @return a collection of all weight function.
   */
  public List<WeightFunction> getWeightFunctions(){
    return weightFunctions;
  }


  public List<UserPredicate> getUserPredicates() {
    return userPredicates;
  }

  public UserPredicate createPredicate(String name, List<Type> argumentTypes) {
    UserPredicate result = new UserPredicate(name, argumentTypes);
    name2predicate.put(name, result);
    userPredicates.add(result);
    Collections.sort(userPredicates);
    return result;
  }

  public UserPredicate createPredicate(String name, Type ... argumentTypes) {
    return createPredicate(name, Arrays.asList(argumentTypes));
  }


  public Predicate getPredicate(String name) {
    return name2predicate.get(name);
  }

  /**
   * Accesses this signatures name -> weight function mapping.
   * @param functionName the name of the function to return
   * @return a function with the given name which is a member of this signature.
   */
  public Function getFunction(String functionName) {
    return name2weightFunction.get(functionName);
  }

  /**
   * Create an Equals predicate for the given type
   * @param type the type of objects to compare
   * @return an Equals predicate for the given type.
   */
  public Equals createEquals(Type type){
    return new Equals("equals", type);
  }

  /**
   * Create an NotEquals predicate for the given type
   * @param type the type of objects to compare
   * @return an NotEquals predicate for the given type.
   */
  public NotEquals createNotEquals(Type type){
    return new NotEquals("equals", type);
  }

  public Collection<Type> getTypes(){
    return name2type.values();
  }


  public GroundAtoms loadGroundAtoms(InputStream inputStream){
    return null;
  }

}



