package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */

public class Quote implements TokenProcessor {
  public String process(String token) {
    return "\"" + token + "\"";
  }
}
