package thebeast.pml.function;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.Heading;
import thebeast.nod.type.TypeFactory;
import thebeast.pml.TheBeast;
import thebeast.pml.Type;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 23-Jan-2007 Time: 16:27:55
 */
public class WeightFunction extends Function {

  private Heading headingWithIndex;
  private LinkedList<Attribute> attributes;
  private Attribute indexAttribute, countAttribute;
  private Heading heading;


  public WeightFunction(String name, List<Type> argumentTypes) {
    super(name, Type.DOUBLE, argumentTypes);
    init(argumentTypes, name);
  }

  public WeightFunction(String name, boolean positive, List<Type> argumentTypes) {
    super(name, positive ? Type.POS_DOUBLE : Type.NEG_DOUBLE, argumentTypes);
    init(argumentTypes, name);
  }

  private void init(List<Type> argumentTypes, String name) {
    TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
    attributes = new LinkedList<Attribute>();
    int index = 0;
    for (Type type : argumentTypes) {
      attributes.add(factory.createAttribute(getColumnName(index++), type.getNodType()));
    }
    heading = factory.createHeadingFromAttributes(attributes);
    indexAttribute = factory.createAttribute("index", factory.intType());
    attributes.add(indexAttribute);
    countAttribute = factory.createAttribute("count", factory.intType());
    attributes.add(countAttribute);

    headingWithIndex = factory.createHeadingFromAttributes(attributes);
  }

  public Heading getIndexedHeading() {
    return headingWithIndex;
  }

  public Heading getHeading() {
    return heading;
  }

  public Attribute getIndexAttribute() {
    return indexAttribute;
  }

  public Attribute getCountAttribute() {
    return countAttribute;
  }

  public void acceptFunctionVisitor(FunctionVisitor visitor) {
    visitor.visitWeightFunction(this);
  }

  public String getColumnName(int argIndex) {
    return "arg_" + argIndex;
  }

  public Attribute getAttributeForArg(int argIndex) {
    return heading.attributes().get(argIndex);
    //return attributes.get(argIndex + 1);
  }


}
