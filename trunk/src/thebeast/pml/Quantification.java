package thebeast.pml;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.TypeFactory;
import thebeast.pml.term.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:29:30
 */
public class Quantification {

  private ArrayList<Variable> variables;
  private ArrayList<Attribute> attributes;

  public Quantification(List<Variable> variables) {
    this.variables = new ArrayList<Variable>(variables);
    this.attributes = new ArrayList<Attribute>(variables.size());
    TypeFactory factory = TheBeast.getInstance().getNodServer().typeFactory();
    int index = 0;
    for (Variable var : variables){
      attributes.add(factory.createAttribute("var" + index++, var.getType().getNodType()));
    }
  }

  public Quantification(Variable ... variables){
    this(Arrays.asList(variables));
  }

  public List<Variable> getVariables() {
    return variables;
  }

  public String toString(){
    StringBuffer buffer = new StringBuffer();
    int index = 0;
    for (Variable var : variables){
      if (index++ > 0) buffer.append(", ");
      buffer.append(var.getType()).append(" ").append(var.getName());
    }
    buffer.append(": ");
    return buffer.toString();
  }

  public Attribute getAttribute(int argIndex) {
    return attributes.get(argIndex);
  }
}
