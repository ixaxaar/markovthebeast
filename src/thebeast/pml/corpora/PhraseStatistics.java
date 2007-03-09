package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;
import thebeast.util.Counter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class PhraseStatistics implements Extractor {

  private int column;
  private ArrayList<String> tokens = new ArrayList<String>(50);
  private UserPredicate predicate;
  private Summarizer summarizer;


  public PhraseStatistics(int column, UserPredicate predicate, Summarizer summarizer) {
    this.column = column;
    this.predicate = predicate;
    this.summarizer = summarizer;
  }

  public Collection<Integer> getColumns() {
    return Collections.singleton(column);
  }

  public void beginLine(int lineNr) {
    if (lineNr == 0) tokens.clear();
  }

  public void endLine(GroundAtoms atoms) {
    
  }

  public void endSentence(GroundAtoms atoms) {
    for (int b = 0; b < tokens.size(); ++b){
      for (int e = b; e < tokens.size(); ++e){
        atoms.getGroundAtomsOf(predicate).addGroundAtom(b,e,summarizer.summarize(tokens.subList(b,e+1)));
      }
    }
  }

  public void extract(int column, String value) {
    if (this.column == column)
      tokens.add(value);
  }

  public static interface Summarizer {
    Object summarize(List<String> phrase);
  }

  public static class HighestFrequency implements Summarizer {

    private Counter<String> counter;

    public HighestFrequency(Counter<String> counter) {
      this.counter = counter;
    }

    public Counter<String> getCounter() {
      return counter;
    }

    public Object summarize(List<String> phrase) {
      int highest = Integer.MIN_VALUE;
      for (String token : phrase){
        int count = counter.get(token);
        if (count > highest)
          highest = count;
      }
      return highest;
    }
  }

}
