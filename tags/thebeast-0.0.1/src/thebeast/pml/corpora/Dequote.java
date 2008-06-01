package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */
public class Dequote implements TokenProcessor {
  public String process(String token) {
    return token.substring(1, token.length() - 1);
  }
}
