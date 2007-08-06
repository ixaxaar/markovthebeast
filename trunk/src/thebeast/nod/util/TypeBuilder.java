package thebeast.nod.util;

import thebeast.nod.NoDServer;
import thebeast.nod.type.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * @author Sebastian Riedel
 */
public class TypeBuilder {

  private TypeFactory factory;
  private NoDServer server;

  private Stack<Type> typeStack = new Stack<Type>();
  private Stack<Attribute> attributeStack = new Stack<Attribute>();

  public TypeBuilder(NoDServer server) {
    this.factory = server.typeFactory();
    this.server = server;

  }

  public TypeBuilder intType() {
    typeStack.push(factory.intType());
    return this;
  }

  public TypeBuilder doubleType() {
    typeStack.push(factory.doubleType());
    return this;
  }

  public TypeBuilder boolType() {
    typeStack.push(factory.boolType());
    return this;
  }

  public TypeBuilder catType(String typeName, String... constants) {
    typeStack.push(factory.createCategoricalType(server.identifierFactory().createName(typeName),
            Arrays.asList(constants)));
    return this;
  }

  public TypeBuilder att(String name) {
    attributeStack.push(factory.createAttribute(name, typeStack.pop()));
    return this;
  }

  public TypeBuilder tupleType(int howMany) {
    Heading heading = createHeading(howMany);
    typeStack.push(factory.createTupleType(heading));
    return this;
  }

  public TypeBuilder relationType(int howMany) {
    Heading heading = createHeading(howMany);
    typeStack.push(factory.createRelationType(heading));
    return this;
  }

  public TypeBuilder arrayType() {
    typeStack.push(factory.createArrayType(typeStack.pop()));
    return this;
  }

  private Heading createHeading(int howMany) {
    ArrayList<Attribute> attributes = new ArrayList<Attribute>(howMany);
    for (int i = 0; i < howMany; ++i) {
      attributes.add(attributeStack.pop());
    }
    return factory.createHeadingFromAttributes(attributes);
  }

  public Type build() {
    return typeStack.pop();
  }

  public RelationType buildRelationType() {
    return (RelationType) typeStack.pop();
  }
  
}
