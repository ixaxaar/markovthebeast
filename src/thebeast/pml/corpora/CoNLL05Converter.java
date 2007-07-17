package thebeast.pml.corpora;

import thebeast.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 17-Jul-2007 Time: 16:07:54
 */
public class CoNLL05Converter {

  private static final int CORE_FEATURE_COUNT = 4;
  private static final int WORD_INDEX = 0;
  private static final int POS_INDEX = 1;
  private static final int CHARNIAK_INDEX = 2;
  private static final int NE_INDEX = 3;
  private static final String NONE = "NONE";


  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: CoNLL05Converter conll2pml|pml2conll < <inputfile> > <outputfile>");
      System.exit(0);
    }
    String mode = args[0];
    if (mode.equals("conll2pml")) {
      conll2pml(new BufferedReader(new InputStreamReader(System.in)),System.out);
    } else if (mode.equals("pml2conll")) {

    } else {
      System.out.println("Usage: CoNLL05Converter conll2pml|pml2conll < <inputfile> > <outputfile>");
      System.exit(0);
    }
  }

  private static void conll2pml(BufferedReader reader, PrintStream out) throws IOException {
    boolean inSentence = true;
    Sentence sentence = new Sentence(50);
    ArrayList<Integer> predicateTokens = new ArrayList<Integer>();
    ArrayList<String> predicateLemmas = new ArrayList<String>();
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      line = line.trim();
      if (!line.equals("")) {
        StringTokenizer tokenizer = new StringTokenizer(line, "[\t\n ]", false);
        ArrayList<String> fields = new ArrayList<String>(20);
        while (tokenizer.hasMoreTokens()) fields.add(tokenizer.nextToken());
        sentence.add(fields);
        if (!fields.get(CORE_FEATURE_COUNT + 1).equals("-")){
          predicateTokens.add(sentence.size() - 1);
          predicateLemmas.add(fields.get(CORE_FEATURE_COUNT + 1));
        }
      } else {
        //collect all spans
        Tree charniak = Tree.create(sentence, CHARNIAK_INDEX);
        Tree ne = Tree.create(sentence, NE_INDEX);

        //count of predicates
        int predCount = sentence.get(0).size() - CORE_FEATURE_COUNT - 2;
        for (int pred = 0; pred < predCount; ++pred) {
          //words
          int predicateToken = predicateTokens.get(pred);
          out.println(">>");
          printFeatures("word", out, sentence, WORD_INDEX);
          printFeatures("pos", out, sentence, POS_INDEX);

          HashSet<Pair<Integer,Integer>> spans = new HashSet<Pair<Integer, Integer>>();
          Tree args = Tree.createChunks(sentence, CORE_FEATURE_COUNT + 2 + pred);
          charniak.getSpans(spans);
          ne.getSpans(spans);
          args.getChunks(spans);

          ArrayList<Pair<Integer,Integer>> sorted = new ArrayList<Pair<Integer, Integer>>(spans);
          Collections.sort(sorted, new Tree.SpanComparator());

          //spans
          int id = 0;
          out.println(">span");
          for (Pair<Integer,Integer> span : sorted){
            out.println(id++ + "\t" + span.arg1 + "\t" + span.arg2);
          }
          out.println();

          //labels
          id = 0;
          out.println(">label");
          for (Pair<Integer,Integer> span : spans){
            out.println(id + "\tNE\t" + ne.getLabel(span.arg1,span.arg2));
            out.println(id++ + "\tCharniak\t" + charniak.getLabel(span.arg1,span.arg2));
          }
          out.println();          

          //paths
          id = 0;
          out.println(">path");
          Tree from = charniak.getSmallestCoveringTree(predicateToken,predicateToken);
          for (Pair<Integer,Integer> span : spans){
            if (charniak.contains(span.arg1,span.arg2)){
              Tree.Path path = new Tree.Path();
              path.add(new Tree.PathStep(from.label,true));
              Tree to = charniak.getSmallestCoveringTree(span.arg1,span.arg2);
              from.getPath(to,path);
              out.println(id++ + "\tCharniak\t\"" + path + "\"");              
            } else {
              out.println(id++ + "\tCharniak\tNONE");
            }
          }
          out.println();

          //args
          id = 0;
          out.println(">arg");
          for (Pair<Integer,Integer> span : spans){
            String label = args.getLabel(span.arg1, span.arg2);
            if (!label.equals(NONE))
              out.println(id + "\t" + args.getLabel(span.arg1,span.arg2));
            ++id;
          }
          out.println();

          //write out predicate information
          out.println(">pred");
          out.println(predicateToken + "\t" + predicateLemmas.get(pred));
          out.println();


        }
        sentence.clear();
        predicateTokens.clear();

      }
    }
  }

  private static void printFeatures(String name, PrintStream out, Sentence sentence, int index) {
    out.println(">" + name);
    for (int token = 0; token < sentence.size(); ++token) {
      out.println(token + " \"" + sentence.get(token).get(index) + "\"");
    }
    out.println();
  }


  private static class Tree implements Comparable<Tree>{
    String label;
    ArrayList<Tree> children = new ArrayList<Tree>();
    int begin, end;
    Tree parent;

    public Tree(Tree tree){
      this.label = tree.label;
      this.begin = tree.begin;
      this.end = tree.end;
    }
    public Tree(Tree tree, Tree parent){
      this.label = tree.label;
      this.begin = tree.begin;
      this.end = tree.end;
      this.parent = parent;
    }
    public Tree(String label) {
      this.label = label;
    }

    public Tree(Tree parent, String label) {
      this.parent = parent;
      this.label = label;
    }

    public void getSpans(Collection<Pair<Integer,Integer>> spans){
      spans.add(new Pair<Integer, Integer>(begin,end));
      for (Tree child : children) child.getSpans(spans);
    }
    public void getChunks(Collection<Pair<Integer,Integer>> spans){
      for (Tree child : children) child.getSpans(spans);
    }

    public Tree pruneXuePalmer(){
      Tree current = this;
      Tree result = new Tree(this);
      while (current != null){
        for (Tree child : children){
          Tree tree = new Tree(child, result);
          result.children.add(tree);
          if (tree.label.equals("PP")){
            for (Tree ppChild : child.children){
              tree.children.add(new Tree(ppChild,tree));
            }
          }
        }
        current = current.parent;
      }
      return null;
    }

    public Tree getRoot(){
      if (parent == null) return this;
      else return parent.getRoot();
    }

    public String getLabel(int begin, int end){
      if (this.begin == begin && this.end == end)
        return label;
      for (Tree child : children) {
        if (child.covers(begin,end)) return child.getLabel(begin, end);
      }
      return NONE;
    }

    public boolean contains(int begin, int end){
      //noinspection StringEquality
      return getLabel(begin, end) != NONE;
    }

    public boolean covers(int begin, int end){
      return this.begin <= begin && this.end >= end;
    }

    public boolean covers(Tree other){
      return covers(other.begin,other.end);
    }

    public Tree getSmallestCoveringTree(int begin, int end){
      if (!covers(begin,end)) return null;
      if (this.begin == begin && this.end == end)
        return this;
      for (Tree child : children){
        Tree result = child.getSmallestCoveringTree(begin,end);
        if (result != null) return result;
      }
      return this;
    }

    public void getPath(Tree other, Path path){
      if (covers(other)){
        for (Tree child : children){
          if (child.covers(other)) {
            path.add(new PathStep(label,false));
            child.getPath(other,path);
            return;
          }
        }
      } else {
        if (parent == null) return;
        path.add(new PathStep(label,true));
        parent.getPath(other,path);
      }
    }

    public static class PathStep {
      public final boolean up;
      public final String label;
      public PathStep(String label, boolean up) {
        this.label = label;
        this.up = up;
      }

      public String toString() {
        return label + (up ? " ^ " : " v ");
      }
    }

    public static class Path extends LinkedList<PathStep> {

      public String toString() {
        StringBuffer result = new StringBuffer();
        for (PathStep step : this)
          result.append(step);
        return result.toString();
      }
    }


    public static Tree create(Sentence sentence, int column){
      Stack<Tree> stack = new Stack<Tree>();
      for (int i = 0; i < sentence.size(); ++i){
        StringTokenizer tokenizer = new StringTokenizer(sentence.get(i).get(column),"[()*]",true);
        while (tokenizer.hasMoreTokens()){
          String token = tokenizer.nextToken();
          if (token.equals("(")){
            String label = tokenizer.nextToken();
            Tree tree = stack.size() > 0 ? new Tree(stack.peek(),label) : new Tree(label);
            tree.begin = i;
            stack.push(tree);
          } else if (token.equals(")")){
            Tree tree = stack.pop();
            tree.end = i;
            if (stack.size() > 0) stack.peek().children.add(tree);
            else return tree;
          }
        }
      }
      return stack.pop();
    }
    public static Tree createChunks(Sentence sentence, int column){
      Stack<Tree> stack = new Stack<Tree>();
      stack.push(new Tree("chunks"));
      for (int i = 0; i < sentence.size(); ++i){
        StringTokenizer tokenizer = new StringTokenizer(sentence.get(i).get(column),"[()*]",true);
        while (tokenizer.hasMoreTokens()){
          String token = tokenizer.nextToken();
          if (token.equals("(")){
            String label = tokenizer.nextToken();
            Tree tree = new Tree(stack.peek(),label);
            tree.begin = i;
            stack.push(tree);
          } else if (token.equals(")")){
            Tree tree = stack.pop();
            tree.end = i;
            stack.peek().children.add(tree);
          }
        }
      }
      return stack.pop();
    }


    public int compareTo(Tree o) {
      return o.covers(begin,end) ? -1 : covers(o.begin,o.end) ? 1 : begin - o.begin; 
    }

    private static class SpanComparator implements Comparator<Pair<Integer,Integer>> {

      public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
        return covers(o1,o2) ? -1 : covers(o2,o1) ? 1 : o1.arg1 - o2.arg1;
      }

      public static boolean covers(Pair<Integer,Integer> o1, Pair<Integer,Integer> o2){
        return o1.arg1 <= o2.arg1 && o1.arg2 >= o2.arg2;
      }

    }

  }


  private static class Sentence extends ArrayList<ArrayList<String>> {

    public Sentence(int initialCapacity) {
      super(initialCapacity);
    }

    public Sentence() {
    }
  }

}
