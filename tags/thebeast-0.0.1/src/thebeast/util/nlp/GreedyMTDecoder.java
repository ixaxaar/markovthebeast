package thebeast.util.nlp;

import thebeast.pml.GroundAtoms;
import thebeast.util.HashMultiMapSet;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class GreedyMTDecoder {


  private static class Translation {
    private LinkedList<Integer> target = new LinkedList<Integer>();
    private HashMultiMapSet<Integer,Integer> tgt2src = new HashMultiMapSet<Integer, Integer>();
    
  }

  private static class Problem {
    GroundAtoms atoms;
  }



  public static void main(String[] args) {

  }

  public void decode(GroundAtoms source){

  }

}
