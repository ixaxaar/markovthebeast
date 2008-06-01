package thebeast.pml.parser;

/**
 * @author Sebastian Riedel
 */
public class ParserTyping {

  String var;
  String type;

  public ParserTyping(String type, String var) {
    this.type = type;
    this.var = var;
  }


  public String toString() {
    return type + " " + var;
  }
}
