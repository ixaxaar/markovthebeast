package thebeast.pml.corpora;

import thebeast.pml.GroundAtoms;
import thebeast.pml.Signature;
import thebeast.pml.Type;
import thebeast.pml.UserPredicate;
import thebeast.pml.term.Constant;
import thebeast.util.HashMultiMap;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 12-Feb-2007 Time: 17:55:01
 */
public class CoNLLCorpus extends AbstractCollection<GroundAtoms> implements Corpus {

  private Signature signature;

  private File file;
  private byte[] bytes;

  private boolean sizeKnown;
  private int size;
  private HashMultiMap<Integer, Extractor> extractors = new HashMultiMap<Integer, Extractor>();
  public final static CorpusFactory CONLL_06_FACTORY = new CoNLL06Factory();
  public static final Generator CONLL_06_GENERATOR = new Generator();
  private HashMap<UserPredicate, Constant[]> constantAtoms = new HashMap<UserPredicate, Constant[]>();

  static {
    CONLL_06_GENERATOR.addTypeTemplate(1,"Word",true,true);
    CONLL_06_GENERATOR.addTypeTemplate(3,"Cpos",true,true);
    CONLL_06_GENERATOR.addTypeTemplate(4,"Pos",true,true);
    CONLL_06_GENERATOR.addTypeTemplate(7,"Dep",false,true);
    CONLL_06_GENERATOR.addInitialConstants("Word", "ROOT");
    CONLL_06_GENERATOR.addInitialConstants("Cpos", "ROOT");
    CONLL_06_GENERATOR.addInitialConstants("Pos", "ROOT");
  }

  public static class CoNLL06Factory implements CorpusFactory {

    public Corpus createCorpus(Signature signature, File file) {
      UserPredicate word = (UserPredicate) signature.getPredicate("word");
      UserPredicate pos = (UserPredicate) signature.getPredicate("pos");
      UserPredicate link = (UserPredicate) signature.getPredicate("link");
      UserPredicate dep = (UserPredicate) signature.getPredicate("dep");
      UserPredicate cpos = (UserPredicate) signature.getPredicate("cpos");

      CoNLLCorpus.AttributeExtractor words = new CoNLLCorpus.AttributeExtractor(word, 2);
      words.addMapping(0,0);
      words.addMapping(1,1);
      words.addToQuote(1);

      CoNLLCorpus.AttributeExtractor cpostags = new CoNLLCorpus.AttributeExtractor(cpos, 2);
      cpostags.addMapping(0,0);
      cpostags.addMapping(3,1);

      CoNLLCorpus.AttributeExtractor postags = new CoNLLCorpus.AttributeExtractor(pos, 2);
      postags.addMapping(0,0);
      postags.addMapping(4,1);

      CoNLLCorpus.AttributeExtractor links = new CoNLLCorpus.AttributeExtractor(link, 2);
      links.addMapping(0,1);
      links.addMapping(6,0);

      CoNLLCorpus.AttributeExtractor deps = new CoNLLCorpus.AttributeExtractor(dep, 3);
      deps.addMapping(0,1);
      deps.addMapping(6,0);
      deps.addMapping(7,2);

      CoNLLCorpus corpus = new CoNLLCorpus(signature, file);
      corpus.addExtractor(words,0,1);
      corpus.addExtractor(cpostags,0,3);
      corpus.addExtractor(postags,0,4);
      corpus.addExtractor(links,0,6);
      corpus.addExtractor(deps,0,6,7);

      corpus.addConstantAtom(word, 0, "ROOT");
      corpus.addConstantAtom(pos, 0, "ROOT");
      corpus.addConstantAtom(cpos, 0, "ROOT");

      return corpus;
    }

  }

  public static class Generator implements TypeGenerator {

    private HashMap<Integer, String> names = new HashMap<Integer, String>();
    private HashMap<Integer, Boolean> quotes = new HashMap<Integer, Boolean>();
    private HashMap<Integer, Boolean> unknowns = new HashMap<Integer, Boolean>();
    private HashMap<Integer, HashSet<String>> values = new HashMap<Integer, HashSet<String>>();

    private HashMultiMap<String,String> initialConstants = new HashMultiMap<String, String>();

    public void addTypeTemplate(int column, String name, boolean quote, boolean unknown) {
      names.put(column, name);
      quotes.put(column, quote);
      unknowns.put(column, unknown);
    }

    public void addInitialConstants(String type, String ... constants){
      for (String constant : constants)
        initialConstants.add(type,constant);
    }

    public void generateTypes(InputStream is, Signature signature) {
      try {
        for (int col : names.keySet()) {
          values.put(col, new HashSet<String>());
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
          line = line.trim();
          if (!line.equals("")) {
            String[] split = line.split("[\t ]");
            for (int col = 0; col < split.length; ++col) {
              Boolean quote = quotes.get(col);
              if (quote != null)
                values.get(col).add(quote ? "\"" + split[col] + "\"" : split[col]);
            }
          }
        }
        for (int col : names.keySet()) {
          String name = names.get(col);
          boolean unknown = unknowns.get(col);
          HashSet<String> values = this.values.get(col);
          List<String> initial = initialConstants.get(name);
          if (initial != null) values.addAll(initial);
          ArrayList<String> strings = new ArrayList<String>(values);
          signature.createType(name, unknown, strings);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
  }

  private CoNLLCorpus(Signature signature, File file, HashMultiMap<Integer, Extractor> map) {
    this.file = file;
    this.signature = signature;
    this.extractors.putAll(map);
  }

  public CoNLLCorpus(Signature signature, File file, Extractor... extractors) {
    this.file = file;
    this.signature = signature;
    int index = 0;
    for (Extractor extractor : extractors)
      this.extractors.add(index++, extractor);
  }

  public void addExtractor(Extractor extractor, int... cols) {
    for (int col : cols)
      this.extractors.add(col, extractor);
  }


  public CoNLLCorpus(Signature signature, byte[] bytes, Extractor... extractors) {
    this.signature = signature;
    this.bytes = bytes;
    int index = 0;
    for (Extractor extractor : extractors)
      this.extractors.add(index++, extractor);
  }

  public Iterator<GroundAtoms> iterator() {
    return new SentenceIterator(new BufferedReader(new InputStreamReader(createStream())));
  }

  public int size() {
    if (!sizeKnown) determineSize();
    return size;
  }

  private void determineSize() {
    size = 0;
    boolean inSentence = false;
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(createStream()));
      for (String line = reader.readLine(); line != null; line = reader.readLine()) {
        if (!inSentence && !line.trim().equals("")) {
          inSentence = true;
        } else if (inSentence && line.trim().equals("")) {
          ++size;
          inSentence = false;
        }
      }
      if (inSentence) {
        ++size;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    sizeKnown = true;
  }

  private InputStream createStream() {
    try {
      return bytes != null ? new ByteArrayInputStream(bytes) : new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Signature getSignature() {
    return null;
  }

  public ListIterator<GroundAtoms> listIterator() {
    return null;
  }

  public class SentenceIterator implements Iterator<GroundAtoms> {


    BufferedReader reader;
    boolean hasNext;
    GroundAtoms next;

    public SentenceIterator(BufferedReader reader) {
      if (!sizeKnown) size = 0;
      this.reader = reader;
      //look for the first ">>";
      try {
        String line;
        for (line = reader.readLine(); line != null; line = reader.readLine()) {
          if (line.trim().equals("")) break;
        }
        if (line == null) {
          size = 0;
          hasNext = false;
          sizeKnown = true;
        } else {
          hasNext = true;
        }

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }


    private void update() {
      try {
        String line;
        int lineNr = 0;
        next = signature.createGroundAtoms();
        for (UserPredicate pred : constantAtoms.keySet())
          next.getGroundAtomsOf(pred).addGroundAtom(constantAtoms.get(pred));
        for (line = reader.readLine(); line != null; line = reader.readLine()) {
          if (line.trim().equals("")) break;
          String[] split = line.split("[\t ]");
          for (int col = 0; col < split.length; ++col) {
            List<Extractor> extractorsForCol = CoNLLCorpus.this.extractors.get(col);
            if (extractorsForCol != null) for (Extractor extractor : extractorsForCol)
              extractor.extract(lineNr, col, split[col], next);
          }
          ++lineNr;
        }
        if (line == null) {
          hasNext = false;
          sizeKnown = true;
        } else {
          hasNext = true;
          if (!sizeKnown) ++size;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public boolean hasNext() {
      return hasNext;
    }

    public GroundAtoms next() {
      update();
      return next;
    }

    public void remove() {

    }
  }

  public void addConstantAtom(UserPredicate predicate, Object ... arg){
    int index = 0;
    Constant[] constants = new Constant[arg.length];
    for (Type type : predicate.getArgumentTypes()){
      constants[index] = type.getConstant(arg[index++].toString());  
    }
    constantAtoms.put(predicate,constants);
  }

  public static interface Extractor {
    void extract(int line, int column, String value, GroundAtoms atoms);
  }

  public static class AttributeExtractor implements Extractor {

    private int count = 0;
    private HashMap<Integer, Integer> mapping;
    private UserPredicate predicate;
    private Constant[] args;
    private HashSet<Integer> toQuote = new HashSet<Integer>();

    public AttributeExtractor(UserPredicate predicate, Map<Integer, Integer> mapping) {
      this.predicate = predicate;
      this.mapping = new HashMap<Integer, Integer>(mapping);
      args = new Constant[mapping.size()];
    }

    public AttributeExtractor(UserPredicate predicate, int size) {
      this.predicate = predicate;
      this.mapping = new HashMap<Integer, Integer>();
      args = new Constant[size];

    }

    public void addMapping(int column, int argIndex){
      mapping.put(column, argIndex);
    }

    public void addToQuote(int column){
      toQuote.add(column);
    }

    public void extract(int line, int column, String value, GroundAtoms atoms) {
      int argIndex = mapping.get(column);
      Type type = predicate.getArgumentTypes().get(argIndex);
      args[argIndex] = type.getConstant(toQuote.contains(column) ? "\"" + value + "\"" : value);
      if (count == args.length - 1) {
        atoms.getGroundAtomsOf(predicate).addGroundAtom(args);
        count = 0;
      } else
        ++count;
    }
  }

  public class BIOExtractor {
    private String predicate;

  }


}
