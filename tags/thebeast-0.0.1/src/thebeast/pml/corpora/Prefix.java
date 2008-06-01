package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */
public class Prefix implements TokenProcessor {
   int howmany;

   public Prefix(int howmany) {
     this.howmany = howmany;
   }

   public String process(String token) {
     return token.length() > howmany ? token.substring(0, howmany) : token;
   }
 }
