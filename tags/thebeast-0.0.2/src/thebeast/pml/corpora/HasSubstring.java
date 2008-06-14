package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */
public class HasSubstring implements TokenProcessor {

  private String substring;


  public HasSubstring(String substring) {
    this.substring = substring;
  }

  public String process(String token) {
    return token.contains(substring) ? "True" : "False";
  }
}
