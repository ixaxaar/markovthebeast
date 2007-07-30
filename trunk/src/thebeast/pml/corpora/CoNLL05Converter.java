package thebeast.pml.corpora;

import thebeast.util.Pair;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 17-Jul-2007 Time: 16:07:54
 */
public class CoNLL05Converter {

  private static final int CORE_FEATURE_COUNT = 9;
  private static final int WORD_INDEX = 0;
  private static final int POS_INDEX = 1;
  private static final int COLLINS_INDEX = 2;
  private static final int COLLINS_POS_INDEX = 3;
  private static final int UPC_CHUNK_INDEX = 4;
  private static final int UPC_CLAUSE_INDEX = 5;
  private static final int UPC_POS_INDEX = 6;
  private static final int CHARNIAK_INDEX = 7;
  private static final int NE_INDEX = 8;
  private static final String NONE = "NONE";
  private static HeadFinder headFinder = new HeadFinder();
  private static HashSet<String> copula = new HashSet<String>();
  private static HashSet<String> punctuation = new HashSet<String>();

  static {
    headFinder.addRuleAsString("NP\tr POS NN NNP NNPS NNS;r NX;r JJR;r CD;r JJ;r JJS;r RB;r QP;r NP;r");
    headFinder.addRuleAsString("ADJP\tr NNS;r QP;r NN;r $;r ADVP;r JJ;r VBN;r VBG;r ADJP;r JJR;r NP;r JJS;r DT;r FW;r RBR;r RBS;r SBAR;r RB;r");
    headFinder.addRuleAsString("ADVP\tl RB;l RBR;l RBS;l FW;l ADVP;l TO;l CD;l JJR;l JJ;l IN;l NP;l JJS;l NN;l");
    headFinder.addRuleAsString("CONJP\tl CC;l RB;l IN;l");
    headFinder.addRuleAsString("FRAG\tl");
    headFinder.addRuleAsString("INTJ\tr");
    headFinder.addRuleAsString("LST\tl LS;l :;l");
    headFinder.addRuleAsString("NAC\tr NN NNS NNP NNPS;r NP;r NAC;r EX;r $;r CD;r QP;r PRP;r VBG;r JJ;r JJS;r JJR;r ADJP;r FW;r");
    headFinder.addRuleAsString("PP\tl IN;l TO;l VBG;l VBN;l RP;l FW;l");
    headFinder.addRuleAsString("PRN\tr");
    headFinder.addRuleAsString("PRT\tl RP;l");
    headFinder.addRuleAsString("QP\tr $;r IN;r NNS;r NN;r JJ;r RB;r DT;r CD;r NCD;r QP;r JJR;r JJS;r");
    headFinder.addRuleAsString("RRC\tl VP;l NP;l ADVP;l ADJP;l PP;l");
    headFinder.addRuleAsString("S\tr TO;r IN;r VP;r S;r SBAR;r ADJP;r UCP;r NP;r");
    headFinder.addRuleAsString("SBAR\tr WHNP;r WHPP;r WHADVP;r WHADJP;r IN;r DT;r S;r SQ;r SINV;r SBAR;r FRAG;r");
    headFinder.addRuleAsString("SBARQ\tr SQ;r S;r SINV;r SBARQ;r FRAG;r");
    headFinder.addRuleAsString("SINV\tr VBZ;r VBD;r VBP;r VB;r MD;r VP;r S;r SINV;r ADJP;r NP;r");
    headFinder.addRuleAsString("SQ\tr VBZ;r VBD;r VBP;r VB;r MD;r VP;r SQ;r");
    headFinder.addRuleAsString("UCP\tl");
    headFinder.addRuleAsString("VP\tl VBD;l VBN;l MD;l VBZ;l VB;l VBG;l VBP;l VP;l ADJP;l NN;l NNS;l NP;l");
    headFinder.addRuleAsString("WHADJP\tr CC;r WRB;r JJ;r ADJP;r");
    headFinder.addRuleAsString("WHADVP\tl CC;l WRB;l");
    headFinder.addRuleAsString("WHNP\tr WDT;r WP;r WP$;r WHADJP;r WHPP;r WHNP;r");
    headFinder.addRuleAsString("WHPP\tl IN;l TO;l FW;l");
    headFinder.addRuleAsString("NX\tr POS NN NNP NNPS NNS;r NX;r JJR;r CD;r JJ;r JJS;r RB;r QP;r NP;r");
    headFinder.addRuleAsString("X\tr");

    copula.add("is");
    copula.add("were");
    copula.add("was");
    copula.add("be");
    copula.add("got");
    copula.add("are");
    copula.add("am");

    punctuation.add(".");
    punctuation.add(",");
    punctuation.add(";");
    punctuation.add("''");
    punctuation.add("``");
    punctuation.add(")");
    punctuation.add("(");
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: CoNLL05Converter conll2pml|pml2conll < <inputfile> > <outputfile>");
      System.exit(0);
    }
    String mode = args[0];
    if (mode.equals("conll2pml")) {
      conll2pml(new BufferedReader(new InputStreamReader(args.length == 2 ? new FileInputStream(args[1]) : System.in)), System.out);
    } else if (mode.equals("pml2conll")) {

    } else {
      System.out.println("Usage: CoNLL05Converter conll2pml|pml2conll < <inputfile> > <outputfile>");
      System.exit(0);
    }
  }

  private static void conll2pml(BufferedReader reader, PrintStream out) throws IOException {
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
        if (!fields.get(CORE_FEATURE_COUNT + 1).equals("-")) {
          predicateTokens.add(sentence.size() - 1);
          predicateLemmas.add(fields.get(CORE_FEATURE_COUNT + 1));
        }
      } else {
        //collect all spans
        Tree charniak = Tree.create(sentence, CHARNIAK_INDEX);
        Tree upc = Tree.createChunks(sentence, UPC_CHUNK_INDEX);
        Tree ne = Tree.createChunks(sentence, NE_INDEX);

        HashMap<Pair<Integer, Integer>, Integer> span2id = new HashMap<Pair<Integer, Integer>, Integer>();

        //count of predicates
        int predCount = sentence.get(0).size() - CORE_FEATURE_COUNT - 2;
        for (int pred = 0; pred < predCount; ++pred) {
          //words
          int predicateToken = predicateTokens.get(pred);
          out.println(">>");
          printFeatures("word", out, sentence, WORD_INDEX);
          printFeatures("pos", out, sentence, POS_INDEX);


          Tree predicateTree = charniak.getSmallestCoveringTree(predicateToken, predicateToken);

          HashSet<Pair<Integer, Integer>> spans = new HashSet<Pair<Integer, Integer>>();
          HashSet<Pair<Integer, Integer>> candidates = new HashSet<Pair<Integer, Integer>>();
          HashSet<Pair<Integer, Integer>> argSpans = new HashSet<Pair<Integer, Integer>>();
          Tree args = Tree.createChunks(sentence, CORE_FEATURE_COUNT + 2 + pred);
          Tree charniakPruned = predicateTree.pruneXuePalmer().getRoot();
          charniakPruned.getSpans(candidates);
          ne.getSpans(candidates);
          args.getChunks(argSpans);
          spans.addAll(candidates);
          spans.addAll(argSpans);

          ArrayList<Pair<Integer, Integer>> sortedSpans = new ArrayList<Pair<Integer, Integer>>(spans);
          Collections.sort(sortedSpans, new Tree.SpanComparator());
          ArrayList<Pair<Integer, Integer>> sortedCandidates = new ArrayList<Pair<Integer, Integer>>(candidates);
          Collections.sort(sortedCandidates, new Tree.SpanComparator());

          //spans
          int id = 0;
          out.println(">span");
          for (Pair<Integer, Integer> span : sortedSpans) {
            span2id.put(span, id);
            out.println(id++ + "\t" + span.arg1 + "\t" + span.arg2);
          }
          out.println();

          //candidates
          out.println(">candidate");
          for (Pair<Integer, Integer> span : sortedCandidates) {
            out.println(span2id.get(span));
          }
          out.println();

          //labels
          out.println(">label");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            out.println(id + "\tNE\t" + ne.getLabel(span.arg1, span.arg2));
            out.println(id + "\tCharniak\t" + charniakPruned.getLabel(span.arg1, span.arg2));
          }
          out.println();

          out.println(">parentlabel");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            Tree tree = charniak.getNode(span.arg1, span.arg2);
            if (tree != null) {
              Tree parent = tree.parent;
              if (parent != null) {
                out.println(id + "\tCharniak\t" + parent.label);
              }
            }
          }
          out.println();

          out.println(">parenthead");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            Tree tree = charniak.getNode(span.arg1, span.arg2);
            if (tree != null && tree.parent != null) {
              Tree parent = tree.parent;
              out.println(id + "\tCharniak\t" + headFinder.getHead(parent).begin);
            } else {
              out.println(id + "\tCharniak\t-1");
            }
          }
          out.println();


          out.println(">sister");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            Tree tree = charniakPruned.getNode(span.arg1, span.arg2);
            if (tree != null) {
              Tree leftSister = tree.getLeftSister();
              Tree rightSister = tree.getRightSister();
              int idLeft = leftSister != null ? span2id.get(leftSister.getSpan()) : -1;
              int idRight = rightSister != null ? span2id.get(rightSister.getSpan()) : -1;
              out.println(id + "\tCharniak\t" + idLeft + "\t" + idRight);
            }
          }
          out.println();

          //chunk distance
          out.println(">chunkdistance");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            out.println(id + "\t" + upc.getLeafDistance(span.arg1, predicateToken));
          }
          out.println();

          //frames
          out.println(">frame");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            Tree arg = charniakPruned.getNode(span.arg1, span.arg2);
            //out.println(id + "\tCharniak\t\"" + charniakFrame.getSyntacticFrameString(arg) + "\"");
            if (arg != null)
              out.println(id + "\tCharniak\t\"" + new Tree.SyntacticFrame(charniakPruned, predicateTree, arg) + "\"");
            else out.println(id + "\tCharniak\tNONE");
          }
          out.println();

          out.println(">framepattern");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            Tree arg = charniakPruned.getNode(span.arg1, span.arg2);
            //out.println(id + "\tCharniak\t\"" + charniakFrame.getSyntacticFrameString(arg) + "\"");
            if (arg != null)
              out.println(id + "\tCharniak\t\"" +
                      new Tree.SyntacticFrame(charniakPruned, predicateTree, arg).toStringPattern() + "\"");
            else out.println(id + "\tCharniak\tNONE");
          }
          out.println();

          //before/after
          out.println(">position");
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            out.println(id + "\t" + (span.arg2 < predicateToken ? "Before" : "After"));
          }
          out.println();

          //paths
          out.println(">path");
          Tree from = charniak.getSmallestCoveringTree(predicateToken, predicateToken);
          for (Pair<Integer, Integer> span : candidates) {
            id = span2id.get(span);
            if (charniak.contains(span.arg1, span.arg2)) {
              Tree.Path path = new Tree.Path();
              Tree to = charniak.getSmallestCoveringTree(span.arg1, span.arg2);
              from.getPath(to, path);
              out.println(id + "\tCharniak\t\"" + path + "\"");
            } else {
              out.println(id + "\tCharniak\tNONE");
            }
          }
          out.println();

          //heads
          out.println(">head");
          for (Pair<Integer, Integer> span : spans) {
            id = span2id.get(span);
            if (charniak.contains(span.arg1, span.arg2)) {
              Tree tree = charniak.getSmallestCoveringTree(span.arg1, span.arg2);
              out.println(id + "\tCharniak\t" + headFinder.getHead(tree).begin);
            } else {
              out.println(id + "\tCharniak\t-1 ");
            }
          }
          out.println();

          //args
          out.println(">arg");
          for (Pair<Integer, Integer> span : argSpans) {
            String label = args.getLabel(span.arg1, span.arg2);
            id = span2id.get(span);
            if (!label.equals(NONE))
              out.println(id + "\t\"" + args.getLabel(span.arg1, span.arg2) + "\"");
          }
          out.println();

          //write out predicate information
          out.println(">pred");
          out.println(predicateToken + "\t\"" + predicateLemmas.get(pred) + "\"\t" +
                  (predicateTree.activeVoice(sentence) ? "Active" : "Passive"));
          out.println();

          out.println(">subcat");
          out.println("Charniak \"" + predicateTree.parent.getSubcat() + "\"");
          out.println();

        }
        sentence.clear();
        predicateTokens.clear();
        predicateLemmas.clear();

      }
    }
  }

  private static void printFeatures(String name, PrintStream out, Sentence sentence, int index) {
    out.println(">" + name);
    out.println("-1\tSTART");
    for (int token = 0; token < sentence.size(); ++token) {
      out.println(token + " \"" + sentence.get(token).get(index) + "\"");
    }
    out.println();
  }


  public static class Tree implements Comparable<Tree> {
    String label;
    ArrayList<Tree> children = new ArrayList<Tree>();
    int begin, end;
    Tree parent;

    public Tree(Tree tree) {
      this.label = tree.label;
      this.begin = tree.begin;
      this.end = tree.end;
    }

    public Tree(Tree tree, Tree parent) {
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

    public String getSubcat() {
      StringBuffer result = new StringBuffer();
      int index = 0;
      for (Tree child : children) {
        if (index++ > 0) result.append(" ");
        result.append(unquote(child.label));

      }
      return result.toString();
    }

    public Tree(Tree parent, String label, int begin, int end) {
      this.parent = parent;
      this.label = label;
      this.begin = begin;
      this.end = end;
    }

    public void getSpans(Collection<Pair<Integer, Integer>> spans) {
      spans.add(new Pair<Integer, Integer>(begin, end));
      for (Tree child : children) child.getSpans(spans);
    }

    public void getChunks(Collection<Pair<Integer, Integer>> spans) {
      for (Tree child : children) child.getSpans(spans);
    }

    public Tree getLeftSister() {
      if (parent == null) return null;
      Tree leftsister = null;
      int max = Integer.MIN_VALUE;
      for (Tree sister : parent.children) {
        if (sister.end < begin && sister.end > max) {
          leftsister = sister;
          max = sister.end;
        }
      }
      return leftsister;
    }

    public Tree getRightSister() {
      if (parent == null) return null;
      Tree rightsister = null;
      int min = Integer.MIN_VALUE;
      for (Tree sister : parent.children) {
        if (sister.begin > end && sister.begin < min) {
          rightsister = sister;
          min = sister.begin;
        }
      }
      return rightsister;
    }

    public Pair<Integer, Integer> getSpan() {
      return new Pair<Integer, Integer>(begin, end);
    }

    public static class SyntacticFrame extends LinkedList<Tree> {
      private HashSet<Tree> pivots = new HashSet<Tree>();
      private Tree predicate = null;

      public SyntacticFrame(Tree root, Tree... pivots) {
        this(root, Arrays.asList(pivots));
        this.predicate = pivots[0];
      }

      public SyntacticFrame(Tree root, Collection<Tree> pivots) {
        this.pivots.addAll(pivots);
        addYield(root);
      }

      private void addYield(Tree root) {
        for (Tree leaf : pivots) {
          if (root.equals(leaf)) {
            add(root);
            return;
          }
          if (root.covers(leaf)) {
            int oldSize = size();
            for (Tree child : root.children) {
              addYield(child);
            }
            if (size() == oldSize) {
              add(root);
            }
            return;
          }
        }
        add(root);

      }

      public String toString() {
        StringBuffer result = new StringBuffer();
        int index = 0;
        for (Tree child : this) {
          if (punctuation.contains(unquote(child.label))) continue;
          if (index++ > 0) result.append("_");
          if (pivots.contains(child)) {
            if (child.equals(predicate)) result.append("!");
            result.append(unquote(child.label).substring(0, 1).toUpperCase());
          } else
            result.append(unquote(child.label).substring(0, 1).toLowerCase());
        }
        return result.toString();
      }

      public String toStringPattern() {
        StringBuffer result = new StringBuffer();
        int index = 0;
        for (Tree child : this) {
          if (punctuation.contains(unquote(child.label))) continue;
          if (index++ > 0) result.append("_");
          if (pivots.contains(child)) {
            if (child.equals(predicate)) result.append("!");
            if (unquote(child.label).startsWith("V"))
              result.append(unquote(child.label).substring(0, 1).toUpperCase());
            else
              result.append("CUR");
          } else
            result.append(unquote(child.label).substring(0, 1).toLowerCase());
        }
        return result.toString();
      }


    }


    public int getLeafDistance(int from, int to) {
      boolean reverse = from > to;
      if (reverse) {
        int tmp = from;
        from = to;
        to = tmp;
      }
      if (isLeaf())
        return begin >= from && end <= to ? 1 : 0;
      int distance = 0;
      for (Tree child : children) {
        if (child.begin > from && child.end < to) {
          distance += child.getLeafDistance(child.begin, child.end);
        }
      }
      return distance;
      //return reverse ? -distance : distance;
    }

    public boolean isLeaf() {
      return children.size() == 0;
    }

    public Tree pruneXuePalmer() {
      Tree current = this.parent;
      Tree result = null;
      Tree lastNode = null;
      Tree lastResult = null;
      while (current != null) {
        result = new Tree(current);
        if (!current.label.equals("CC")) for (Tree child : current.children) {
          if (child == lastNode) {
            result.addChild(lastResult);
            continue;
          }
          Tree tree = new Tree(child, result);
          result.children.add(tree);
          if (tree.label.equals("PP")) {
            for (Tree ppChild : child.children) {
              tree.children.add(new Tree(ppChild, tree));
            }
          }
        }
        lastNode = current;
        current = current.parent;
        lastResult = result;
      }
      return result;
    }

    public void addChild(Tree tree) {
      children.add(tree);
      tree.parent = this;
    }

    public void getSyntacticFrame(List<Tree> frame, Tree predicate) {
      if (!covers(predicate) && (label.equals("NP") || label.equals("PP")) || this.equals(predicate)) {
        frame.add(this);
        return;
      }
      for (Tree child : children) {
        child.getSyntacticFrame(frame, predicate);
      }
    }

    public Tree getSyntacticFrameTree(Tree predicate) {
      LinkedList<Tree> frame = new LinkedList<Tree>();
      getSyntacticFrame(frame, predicate);
      Tree result = new Tree("Frame");
      result.begin = Integer.MAX_VALUE;
      result.end = Integer.MIN_VALUE;
      for (Tree tree : frame) {
        result.addChild(new Tree(tree));
        if (tree.begin < result.begin) result.begin = tree.begin;
        if (tree.end > result.end) result.end = tree.end;
      }
      return result;
    }

    public String getSyntacticFrameString(Tree argument) {
      StringBuffer result = new StringBuffer();
      int index = 0;
      for (Tree child : children) {
        if (index++ > 0) result.append("_");
        if (child.equals(argument)) {
          result.append(unquote(child.label).substring(0, 1).toUpperCase());
        } else
          result.append(unquote(child.label).substring(0, 1).toLowerCase());
      }
      return result.toString();
    }


    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Tree tree = (Tree) o;

      return begin == tree.begin && end == tree.end && !(label != null ? !label.equals(tree.label) : tree.label != null);

    }

    public int hashCode() {
      int result;
      result = (label != null ? label.hashCode() : 0);
      result = 31 * result + begin;
      result = 31 * result + end;
      return result;
    }

    public Tree getRoot() {
      if (parent == null) return this;
      else return parent.getRoot();
    }

    public String getLabel(int begin, int end) {
      if (this.begin == begin && this.end == end)
        return label;
      for (Tree child : children) {
        if (child.covers(begin, end)) return child.getLabel(begin, end);
      }
      return NONE;
    }

    public Tree getNode(int begin, int end) {
      if (this.begin == begin && this.end == end)
        return this;
      for (Tree child : children) {
        if (child.covers(begin, end)) return child.getNode(begin, end);
      }
      return null;
    }


    public boolean contains(int begin, int end) {
      //noinspection StringEquality
      return getLabel(begin, end) != NONE;
    }

    public boolean covers(int begin, int end) {
      return this.begin <= begin && this.end >= end;
    }

    public boolean covers(Tree other) {
      return covers(other.begin, other.end);
    }

    public boolean isToken() {
      return begin == end;
    }

    public boolean activeVoice(Sentence sentence) {
      if (!label.equals("\"VBN\"")) return true;
      Tree vp = parent;
      Tree aux = vp.parent;
      if (aux == null) return true;
      //check for "is"
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (sentence.get(child.begin).get(WORD_INDEX).equals("is"))
          return false;
      }
      //check for "was"
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (sentence.get(child.begin).get(WORD_INDEX).equals("was"))
          return false;
      }
      //check for "got"
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (sentence.get(child.begin).get(WORD_INDEX).equals("got"))
          return false;
      }
      //check for "are"
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (sentence.get(child.begin).get(WORD_INDEX).equals("are"))
          return false;
      }
      //check for "be"
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (sentence.get(child.begin).get(WORD_INDEX).equals("be"))
          return false;
      }
      //check for "were"
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (sentence.get(child.begin).get(WORD_INDEX).equals("were"))
          return false;
      }
      //check for "am"
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (sentence.get(child.begin).get(WORD_INDEX).equals("am"))
          return false;
      }
      //check for "has been"
      boolean lookForBeen = false;
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (!lookForBeen && sentence.get(child.begin).get(WORD_INDEX).equals("has"))
          lookForBeen = true;
        else if (lookForBeen && sentence.get(child.begin).get(WORD_INDEX).equals("been"))
          return false;
      }

      //check for "had been"
      lookForBeen = false;
      for (Tree child : aux.children) {
        if (child.begin == begin) break;
        if (!lookForBeen && sentence.get(child.begin).get(WORD_INDEX).equals("had"))
          lookForBeen = true;
        else if (lookForBeen && sentence.get(child.begin).get(WORD_INDEX).equals("been"))
          return false;
      }
      return true;
    }

    public void augmentWithTags(Sentence sentence, int column) {
      ArrayList<Tree> result = new ArrayList<Tree>();
      int previous = begin;
      for (Tree child : children) {
        for (int i = previous; i < child.begin; ++i) {
          Tree tag = new Tree(this, "\"" + sentence.get(i).get(column) + "\"", i, i);
          result.add(tag);
        }
        result.add(child);
        child.augmentWithTags(sentence, column);

        previous = child.end + 1;
      }
      for (int i = previous; i <= end; ++i) {
        Tree tag = new Tree(this, "\"" + sentence.get(i).get(column) + "\"", i, i);
        result.add(tag);
      }
      this.children = result;
    }

    public Tree getSmallestCoveringTree(int begin, int end) {
      if (!covers(begin, end)) return null;
      if (this.begin == begin && this.end == end)
        return this;
      for (Tree child : children) {
        Tree result = child.getSmallestCoveringTree(begin, end);
        if (result != null) return result;
      }
      return this;
    }

    public String toString() {
      StringBuffer result = new StringBuffer();
      result.append("(").append(label);
      for (Tree child : children) result.append(child);
      result.append(")");
      return result.toString();
    }

    public void getPath(Tree other, Path path) {
      if (covers(other)) {
        for (Tree child : children) {
          if (child.covers(other)) {
            path.add(new PathStep(label, false));
            child.getPath(other, path);
            return;
          }
        }
      } else {
        if (parent == null) return;
        path.add(new PathStep(label, true));
        parent.getPath(other, path);
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
        return abbreviate(unquote(label)) + (up ? " ^ " : " v ");
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


    public static Tree create(Sentence sentence, int column) {
      Stack<Tree> stack = new Stack<Tree>();
      for (int i = 0; i < sentence.size(); ++i) {
        StringTokenizer tokenizer = new StringTokenizer(sentence.get(i).get(column), "[()*]", true);
        while (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken();
          if (token.equals("(")) {
            String label = tokenizer.nextToken();
            Tree tree = stack.size() > 0 ? new Tree(stack.peek(), label) : new Tree(label);
            tree.begin = i;
            stack.push(tree);
          } else if (token.equals(")")) {
            Tree tree = stack.pop();
            tree.end = i;
            if (stack.size() > 0) stack.peek().children.add(tree);
            else {
              tree.augmentWithTags(sentence, POS_INDEX);
              return tree;
            }
          }
        }
      }
      Tree result = stack.pop();
      result.augmentWithTags(sentence, POS_INDEX);
      return result;
    }

    public static Tree createChunks(Sentence sentence, int column) {
      Stack<Tree> stack = new Stack<Tree>();
      stack.push(new Tree(null, "Root", 0, sentence.size()-1));
      for (int i = 0; i < sentence.size(); ++i) {
        StringTokenizer tokenizer = new StringTokenizer(sentence.get(i).get(column), "[()*]", true);
        while (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken();
          if (token.equals("(")) {
            String label = tokenizer.nextToken();
            Tree tree = new Tree(stack.peek(), label);
            tree.begin = i;
            stack.push(tree);
          } else if (token.equals(")")) {
            Tree tree = stack.pop();
            tree.end = i;
            stack.peek().children.add(tree);
          }
        }
      }
      return stack.pop();
    }


    public int compareTo(Tree o) {
      return o.covers(begin, end) ? -1 : covers(o.begin, o.end) ? 1 : begin - o.begin;
    }

    private static class SpanComparator implements Comparator<Pair<Integer, Integer>> {

      public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
        return covers(o1, o2) ? -1 : covers(o2, o1) ? 1 : o1.arg1 - o2.arg1;
      }

      public static boolean covers(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
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

  private static class HeadFinder {

    private HashMap<String, Rule> rules = new HashMap<String, Rule>();

    static class Scan {
      boolean left;
      HashSet<String> labels = new HashSet<String>();

      public Scan(boolean left, String... tags) {
        this.left = left;
        for (String tag : tags) this.labels.add(tag);
      }

      public Scan(boolean left, Collection<String> tags) {
        this.left = left;
        this.labels.addAll(tags);
      }

      public Tree scan(Tree parent, HeadFinder finder) {
        if (parent.children.size() == 0) return parent;
        if (!left) {
          for (int i = parent.children.size() - 1; i >= 0; --i) {
            Tree child = parent.children.get(i);
            if (labels.contains(unquote(child.label))) return finder.getHead(child);
          }
        } else {
          //noinspection ForLoopReplaceableByForEach
          for (int i = 0; i < parent.children.size(); ++i) {
            Tree child = parent.children.get(i);
            if (labels.contains(unquote(child.label))) return finder.getHead(child);
          }

        }
        return null;
      }


    }

    public Tree getHead(Tree tree) {
      if (tree.children.size() == 0) return tree;
      Rule rule = rules.get(tree.label);
      if (rule == null) return tree.children.get(tree.children.size() - 1);
      Tree head = rule.apply(tree, this);
      if (head == null) return tree.children.get(tree.children.size() - 1);
      return head;
    }

    static class Rule extends LinkedList<Scan> {
      String label;

      public Rule(String label) {
        this.label = label;
      }

      public Tree apply(Tree tree, HeadFinder finder) {
        for (Scan scan : this) {
          Tree result = scan.scan(tree, finder);
          if (result != null) return result;
        }
        return null;
      }


    }

    public void addRuleAsString(String string) {
      StringTokenizer tokenizer = new StringTokenizer(string, "[; \t]", false);
      String label = tokenizer.nextToken();
      LinkedList<String> items = new LinkedList<String>();
      while (tokenizer.hasMoreElements()) {
        items.add(tokenizer.nextToken());
      }
      addRule(label, items.toArray(new String[items.size()]));
    }

    public void addRule(String label, String... string) {
      Rule rule = new Rule(label);
      LinkedList<String> tags = new LinkedList<String>();
      boolean left = false;
      for (String item : string) {
        if (item.equals("l")) {
          if (tags.size() > 0) {
            rule.add(new Scan(left, tags));
            tags.clear();
          }
          left = true;
        } else if (item.equals("r")) {
          if (tags.size() > 0) {
            rule.add(new Scan(left, tags));
            tags.clear();
          }
          left = false;
        } else {
          tags.add(item);
        }
      }
      rule.add(new Scan(left, tags));
      tags.clear();
      rules.put(label, rule);
    }

  }


  public static String unquote(String label) {
    return (label.startsWith("\"")) ? label.substring(1, label.length() - 1) : label;
  }

  public static String abbreviate(String label) {
    return label.length() > 2 ? label.substring(0, 2) : label;
  }

}
