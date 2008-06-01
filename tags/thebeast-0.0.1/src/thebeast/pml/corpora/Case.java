package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */
public class Case implements TokenProcessor {
  public String process(String token) {
    if (token.length() == 0) return "NONE";
    boolean lowercased = token.toLowerCase().equals(token);
    boolean uppercased = token.toUpperCase().equals(token);
    if (lowercased && uppercased) return "NONE";
    if (lowercased) return "LOWER";
    if (uppercased) return "ALLCAPS";
    String rest = token.substring(1);
    if (!rest.toLowerCase().equals(rest)) return "MIXED";
    return "CAP";
  }
}
