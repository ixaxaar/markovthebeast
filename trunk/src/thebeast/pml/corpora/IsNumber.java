package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */
public class IsNumber implements TokenProcessor{
  public String process(String token) {
    try {
      Double.parseDouble(token);
    } catch (NumberFormatException e) {
      return "NONE";
    }
    return "NUMBER";
  }
}
