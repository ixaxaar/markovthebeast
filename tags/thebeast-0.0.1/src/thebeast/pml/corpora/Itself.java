package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */

public class Itself implements TokenProcessor {
  public String process(String token) {
    return token;
  }
}
