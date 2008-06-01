package thebeast.pml;

import thebeast.nod.expression.TupleExpression;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.value.TupleValue;
import thebeast.pml.term.Constant;
import thebeast.pml.predicate.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 22-Jan-2007 Time: 13:49:23
 */
public class GroundAtom {

  private UserPredicate predicate;

  private ArrayList<Constant> arguments;

  private static ExpressionBuilder builder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
 
  GroundAtom(UserPredicate predicate, List<Constant> arguments) {
    this.predicate = predicate;
    this.arguments = new ArrayList<Constant>(arguments);
  }

  GroundAtom(UserPredicate predicate, Object ... args){
    builder.clear();
    int index = 0;
    this.arguments = new ArrayList<Constant>(args.length);
    for (Type type : predicate.getArgumentTypes()) {
      arguments.add(type.toConstant(args[index++]));
    }
  }

  GroundAtom(UserPredicate predicate, TupleValue tuple){
    this.predicate = predicate;    
    arguments = new ArrayList<Constant>(tuple.size());
    int index = 0;
    for (Type type : predicate.getArgumentTypes()){
      arguments.add(type.toConstant(tuple.element(predicate.getColumnName(index++))));
    }
  }

  public List<Constant> getArguments() {
    return arguments;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  synchronized TupleExpression toTuple(){
    builder.clear();
    for (Constant constant : arguments)
      builder.expr(constant.toScalar());
    return builder.getTuple();
  }

  public String toString(){
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bos);
    for (Constant constant : arguments)
      out.printf("%-10s ",constant.toString());
    return bos.toString();
  }

}
