package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */
public class BIOLabel implements TokenProcessor {
  public String process(String token) {
    return token.equals("O") ? "O" : token.substring(2);
  }
}
