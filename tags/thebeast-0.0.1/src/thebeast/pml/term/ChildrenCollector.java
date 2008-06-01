package thebeast.pml.term;

import thebeast.util.HashMultiMapList;

import java.util.Stack;

/**
 * @author Sebastian Riedel
 */
public class ChildrenCollector implements TermVisitor{

  public HashMultiMapList<Term,Term> children;
  private Stack<Term> parents;

  public HashMultiMapList<Term,Term> collect(Term term){
    children = new HashMultiMapList<Term, Term>();
    parents = new Stack<Term>();
    term.acceptTermVisitor(this);
    return children;
  }


  public void visitFunctionApplication(FunctionApplication functionApplication) {
    children.add(functionApplication,functionApplication);
    for (Term parent : parents){
      children.add(parent,functionApplication);
    }
    parents.push(functionApplication);
    for (Term arg : functionApplication.getArguments())
      arg.acceptTermVisitor(this);
    parents.pop();
  }

  public void visitVariable(Variable variable) {
    children.add(variable, variable);
    for (Term parent : parents)
      children.add(parent,variable);
  }

  public void visitIntConstant(IntConstant intConstant) {
    children.add(intConstant,intConstant);
    for (Term parent : parents)
      children.add(parent,intConstant);
  }

  public void visitCategoricalConstant(CategoricalConstant categoricalConstant) {
    children.add(categoricalConstant,categoricalConstant);
    for (Term parent : parents)
      children.add(parent,categoricalConstant);
  }

  public void visitDontCare(DontCare dontCare) {
    children.add(dontCare, dontCare);
    for (Term parent : parents)
      children.add(parent,dontCare);
  }

  public void visitDoubleConstant(DoubleConstant doubleConstant) {
    children.add(doubleConstant,doubleConstant);
    for (Term parent : parents)
      children.add(parent,doubleConstant);

  }

  public void visitBinnedInt(BinnedInt binnedInt) {
    children.add(binnedInt,binnedInt);
    for (Term parent : parents)
      children.add(parent, binnedInt);
    parents.push(binnedInt);
    binnedInt.getArgument().acceptTermVisitor(this);
    parents.pop();
  }

  public void visitBoolConstant(BoolConstant boolConstant) {
    children.add(boolConstant,boolConstant);
    for (Term parent : parents)
      children.add(parent,boolConstant);

  }
}
