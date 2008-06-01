package thebeast.pml.parser;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 07-Nov-2007 Time: 14:39:15
 */
public class ParserFactorSpec {

  public final String name;
  public final boolean ground;
  public final int order;

  public ParserFactorSpec(String name, int order, boolean ground) {
    this.name = name;
    this.order = order;
    this.ground = ground;
  }
}
