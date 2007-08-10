package thebeast.pml.corpora;

import thebeast.pml.*;
import thebeast.util.Pair;
import thebeast.util.Util;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Aug-2007 Time: 22:36:11
 */
public class CoNLL05Evaluator implements CorpusEvaluationFunction {

  private int fp=0, fn=0, totalGold=0, totalGuess = 0;
  private boolean joinMode = true;

  public enum Type {RECALL, PRECISION, F1}

  private Type type;

  public CoNLL05Evaluator(Type type) {
    this.type = type;
  }

  private static class Span {
    int from,to;

    public Span(int from, int to) {
      this.from = from;
      this.to = to;
    }

    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Span span = (Span) o;

      if (from != span.from) return false;
      if (to != span.to) return false;

      return true;
    }


    @Override
    public String toString() {
      return "(" + from + "," + to + ")";
    }

    public int hashCode() {
      int result;
      result = from;
      result = 31 * result + to;
      return result;
    }
  }

  private static class Arg {
    String label;
    Set<Span> spans = new HashSet<Span>();

    public Arg(String label) {
      this.label = label;
    }

    

    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Arg arg = (Arg) o;

      return label.equals(arg.label) && spans.equals(arg.spans);

    }

    public int hashCode() {
      int result;
      result = label.hashCode();
      result = 31 * result + spans.hashCode();
      return result;
    }


    @Override
    public String toString() {
      return label + " " + spans.toString();
    }
  }

  public void start() {
    fp = 0;
    fn = 0;
    totalGold = 0;
    totalGuess = 0;
  }

  public void evaluate(GroundAtoms gold, GroundAtoms guess) {
    //get all gold Args
    Set<Arg> goldArgs = getArgs(gold);
    totalGold += goldArgs.size();
    Set<Arg> guessArgs = getArgs(guess);
    totalGuess += guessArgs.size();
    HashSet<Arg> fp = new HashSet<Arg>(guessArgs);
    fp.removeAll(goldArgs);
    this.fp += fp.size();
    HashSet<Arg> fn = new HashSet<Arg>(goldArgs);
    fn.removeAll(guessArgs);
    this.fn += fn.size();
    //join spans

  }

  private Set<Arg> getArgs(GroundAtoms atoms) {
    Set<Arg> result = new CopyOnWriteArraySet<Arg>();
    HashMap<Integer, Arg> goldArgs = new HashMap<Integer, Arg>();
    HashMap<String, Arg> label2Arg = new HashMap<String, Arg>();
    for (GroundAtom atom : atoms.getGroundAtomsOf("arg")){
      Arg arg = new Arg(Util.unquote(atom.getArguments().get(1).toString()));
      goldArgs.put(atom.getArguments().get(0).asInt(),arg);
      label2Arg.put(arg.label,arg);
    }
    //attach spans
    for (GroundAtom atom : atoms.getGroundAtomsOf("span")){
      int id = atom.getArguments().get(0).asInt();
      int begin = atom.getArguments().get(1).asInt();
      int end = atom.getArguments().get(2).asInt();
      Arg arg = goldArgs.get(id);
      if (arg != null) {
        if (joinMode && arg.label.startsWith("C-")){
          String realLabel = arg.label.substring(2);
          arg = label2Arg.get(realLabel);
          if (arg == null){
            arg = new Arg(realLabel);
            result.add(arg);
          }
        }
        arg.spans.add(new Span(begin,end));
        result.add(arg);
      }
    }
    return result;
  }

  public double getResult() {
    double recall = (double) (totalGold - fn) / (double) totalGold;
    double precision = (double) (totalGuess - fp) / (double) totalGuess;
    switch (type){
      case F1: return 2 * recall * precision / (recall + precision);
      case RECALL: return recall;
      case PRECISION: return precision;
    }
    return 0;
  }

  public static void main(String[] args) {
    Signature signature = TheBeast.getInstance().createSignature();
    signature.createType("Argument", false, "\"A1\"","\"A2\"","\"C-A1\"");
    signature.createPredicate("arg", "Int", "Argument");
    signature.createPredicate("span", "Int", "Int", "Int");

    Model model = signature.createModel();
    model.addHiddenPredicate(signature.getUserPredicate("arg"));
    model.addObservedPredicate(signature.getUserPredicate("span"));

    GroundAtoms gold = signature.createGroundAtoms();
    gold.getGroundAtomsOf("arg").addGroundAtom(0,"\"A1\"");
    gold.getGroundAtomsOf("arg").addGroundAtom(1,"\"A2\"");
    gold.getGroundAtomsOf("arg").addGroundAtom(2,"\"C-A1\"");
    gold.getGroundAtomsOf("span").addGroundAtom(0,0,1);
    gold.getGroundAtomsOf("span").addGroundAtom(1,2,3);
    gold.getGroundAtomsOf("span").addGroundAtom(2,4,5);
    gold.getGroundAtomsOf("span").addGroundAtom(3,6,7);

    GroundAtoms guess = signature.createGroundAtoms();
    guess.getGroundAtomsOf("arg").addGroundAtom(0,"\"A1\"");
    guess.getGroundAtomsOf("arg").addGroundAtom(1,"\"A2\"");
    guess.getGroundAtomsOf("arg").addGroundAtom(2,"\"C-A1\"");
    guess.getGroundAtomsOf("span").addGroundAtom(0,0,1);
    guess.getGroundAtomsOf("span").addGroundAtom(1,2,3);
    guess.getGroundAtomsOf("span").addGroundAtom(2,4,5);
    guess.getGroundAtomsOf("span").addGroundAtom(3,6,7);

    CorpusEvaluation corpusEvaluation = new CorpusEvaluation(model);
    corpusEvaluation.addCorpusEvaluationFunction("F1 srl", new CoNLL05Evaluator(Type.F1));
    corpusEvaluation.addCorpusEvaluationFunction("Recall srl", new CoNLL05Evaluator(Type.RECALL));
    Evaluation evaluation = new Evaluation(model);
    evaluation.evaluate(gold,guess);
    corpusEvaluation.add(evaluation);
    System.out.println(corpusEvaluation);

  }
}
