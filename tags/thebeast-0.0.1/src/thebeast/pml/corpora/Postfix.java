package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */
public class Postfix implements TokenProcessor {
   int howmany;

   public Postfix(int howmany) {
     this.howmany = howmany;
   }

   public String process(String token) {
     return token.length() > howmany ? token.substring(token.length() - howmany) : token;
   }
 }
