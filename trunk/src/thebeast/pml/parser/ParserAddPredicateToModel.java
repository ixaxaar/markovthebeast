package thebeast.pml.parser;

import thebeast.util.Util;

import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class ParserAddPredicateToModel extends ParserStatement {

  enum Type {HIDDEN, OBSERVED, GLOBAL
  }

  final Type type;
  final List<String> predicates;

  public ParserAddPredicateToModel(Type type, List<String> predicates) {
    this.type = type;
    this.predicates = predicates;
  }

  public void acceptParserStatementVisitor(ParserStatementVisitor visitor) {
    visitor.visitAddPredicateToModel(this);
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(type.name()).append(": ");
    return type.name().toLowerCase() + ": " + Util.toStringWithDelimiters(predicates, ", ");
  }
}
