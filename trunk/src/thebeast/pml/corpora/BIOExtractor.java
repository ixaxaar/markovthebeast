package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.UserPredicate;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Sebastian Riedel
 */
public class BIOExtractor implements Extractor {

  private TokenProcessor processor = new Itself();

  private int column;

  private UserPredicate predicate;
  private int begin, end, newBegin;
  private int lineNr;
  private boolean endFound = false;
  private String label, lastLabel, lastChunk;


  public BIOExtractor(int column, UserPredicate predicate) {
    this.column = column;
    this.predicate = predicate;
  }

  public Collection<Integer> getColumns() {
    return Collections.singleton(column);
  }

  public void beginLine(int lineNr) {
    if (lineNr == 0){
      lastLabel = "O";
      lastChunk = "0";
    }
    this.lineNr = lineNr;
  }

  public void endLine(GroundAtoms atoms) {
    if (endFound) {
      atoms.getGroundAtomsOf(predicate).addGroundAtom(begin, end, label);
      endFound = false;
      begin = newBegin;
    }
  }

  public void endSentence(GroundAtoms atoms) {
    if (!lastLabel.equals("O"))
      atoms.getGroundAtomsOf(predicate).addGroundAtom(begin, end, label);      
  }

  public void extract(int column, String value) {
    if (value.equals("O")) {
      if (!lastLabel.equals("O")) {
        endFound = true;
        end = lineNr - 1;
        label = lastLabel;
        lastLabel = "O";
        lastChunk = "O";
      }
    } else {
      String[] split = value.split("[-]");
      if (split[0].equals("B")) {
        if (lastChunk.equals("B")) {
          endFound = true;
          end = lineNr - 1;
          label = lastLabel;
          newBegin = lineNr;
        } else
          begin = lineNr;
        lastLabel = processor.process(split[1]);
        lastChunk = split[0];
      }
    }

  }
}
