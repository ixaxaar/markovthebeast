package thebeast.pml.corpora;

import thebeast.pml.*;
import thebeast.pml.term.Constant;
import thebeast.pml.term.IntConstant;
import thebeast.util.HashMultiMap;

import java.io.*;
import java.util.*;

/**
 * A class for corpora based on the CoNLL column format. Can be configured to extract dependency parse information
 * from CoNLL 06/07) or MALT format.
 */
public class TabFormatCorpus extends AbstractCollection<GroundAtoms> implements Corpus {

  private Signature signature;

  private File file;
  private byte[] bytes;

  private boolean sizeKnown;
  private int size;
  private HashMultiMap<Integer, Extractor> extractors = new HashMultiMap<Integer, Extractor>();
  private HashMultiMap<UserPredicate, AtomWriter> writers = new HashMultiMap<UserPredicate, AtomWriter>();
  public final static CorpusFactory CONLL_06_FACTORY = new CoNLL06Factory();
  public final static CorpusFactory MALT_FACTORY = new MALTFactory();
  public static final Generator
          CONLL_06_GENERATOR = new Generator(),
          MALT_GENERATOR = new Generator();
  private HashMap<UserPredicate, Constant[]> constantAtoms = new HashMap<UserPredicate, Constant[]>();
  private boolean printZeroRow = false;

  private int columns = 0;
  private PrintStream out;


  static {
    CONLL_06_GENERATOR.addTokenCollector(1, "Word", true, new Quote(), "ROOT");
    CONLL_06_GENERATOR.addTokenCollector(1, "Prefix", true, new Pipeline(new Prefix(5), new Quote()), "ROOT");
    CONLL_06_GENERATOR.addTokenCollector(3, "Cpos", true, new Quote(), "ROOT");
    CONLL_06_GENERATOR.addTokenCollector(4, "Pos", true, new Quote(), "ROOT");
    CONLL_06_GENERATOR.addTokenCollector(7, "Dep", true, new Itself());
    MALT_GENERATOR.addTokenCollector(0, "Word", true, new Quote(), "ROOT");
    MALT_GENERATOR.addTokenCollector(0, "Prefix", true, new Pipeline(new Prefix(5), new Quote()), "ROOT");
    MALT_GENERATOR.addTokenCollector(1, "Pos", true, new Quote(), "ROOT");
    MALT_GENERATOR.addTokenCollector(1, "Cpos", true, new Pipeline(new Prefix(2), new Quote()), "ROOT");
    MALT_GENERATOR.addTokenCollector(3, "Dep", true, new Itself());
  }

  public static interface TokenProcessor {
    String process(String token);
  }

  public static class Pipeline implements TokenProcessor {
    TokenProcessor[] processors;

    public Pipeline(TokenProcessor... processors) {
      this.processors = processors;
    }

    public String process(String token) {
      String result = token;
      for (TokenProcessor processor : processors)
        result = processor.process(result);
      return result;
    }
  }

  public static class Quote implements TokenProcessor {
    public String process(String token) {
      return "\"" + token + "\"";
    }
  }

  public static class Dequote implements TokenProcessor {
    public String process(String token) {
      return token.substring(1, token.length() - 1);
    }
  }

  public static class Prefix implements TokenProcessor {
    int howmany;

    public Prefix(int howmany) {
      this.howmany = howmany;
    }

    public String process(String token) {
      return token.length() > howmany ? token.substring(0, howmany) : token;
    }
  }

  public static class Itself implements TokenProcessor {
    public String process(String token) {
      return token;
    }
  }

  public static class MALTFactory implements CorpusFactory {

    public Corpus createCorpus(Signature signature, File file) {
      UserPredicate word = (UserPredicate) signature.getPredicate("word");
      UserPredicate pos = (UserPredicate) signature.getPredicate("pos");
      UserPredicate link = (UserPredicate) signature.getPredicate("link");
      UserPredicate dep = (UserPredicate) signature.getPredicate("dep");
      UserPredicate cpos = (UserPredicate) signature.getPredicate("cpos");
      UserPredicate prefix = (UserPredicate) signature.getPredicate("prefix");

      TabFormatCorpus.AttributeExtractor words = new TabFormatCorpus.AttributeExtractor(word, 2);
      words.addLineNrArg(0);
      words.addMapping(0, 1, new Quote());
      //words.addToQuote(1);

      TabFormatCorpus.AttributeExtractor prefixes = new TabFormatCorpus.AttributeExtractor(prefix, 2);
      prefixes.addLineNrArg(0);
      prefixes.addMapping(0, 1, new Pipeline(new Prefix(5), new Quote()));

      TabFormatCorpus.AttributeExtractor cpostags = new TabFormatCorpus.AttributeExtractor(cpos, 2);
      cpostags.addLineNrArg(0);
      cpostags.addMapping(1, 1, new Pipeline(new Prefix(2), new Quote()));

      TabFormatCorpus.AttributeExtractor postags = new TabFormatCorpus.AttributeExtractor(pos, 2);
      postags.addLineNrArg(0);
      postags.addMapping(1, 1, new Quote());

      TabFormatCorpus.AttributeExtractor links = new TabFormatCorpus.AttributeExtractor(link, 2);
      links.addLineNrArg(1);
      links.addMapping(2, 0);

      TabFormatCorpus.AttributeExtractor deps = new TabFormatCorpus.AttributeExtractor(dep, 3);
      deps.addLineNrArg(1);
      deps.addMapping(2, 0);
      deps.addMapping(3, 2);

      TabFormatCorpus corpus = new TabFormatCorpus(signature, file);
      corpus.addExtractor(prefixes);
      corpus.addExtractor(words);
      corpus.addExtractor(cpostags);
      corpus.addExtractor(postags);
      corpus.addExtractor(links);
      corpus.addExtractor(deps);

      corpus.addConstantAtom(prefix, 0, "ROOT");
      corpus.addConstantAtom(word, 0, "ROOT");
      corpus.addConstantAtom(pos, 0, "ROOT");
      corpus.addConstantAtom(cpos, 0, "ROOT");

      return corpus;
    }


  }

  public static class CoNLL06Factory implements CorpusFactory {

    public Corpus createCorpus(Signature signature, File file) {
      UserPredicate word = (UserPredicate) signature.getPredicate("word");
      UserPredicate pos = (UserPredicate) signature.getPredicate("pos");
      UserPredicate link = (UserPredicate) signature.getPredicate("link");
      UserPredicate dep = (UserPredicate) signature.getPredicate("dep");
      UserPredicate cpos = (UserPredicate) signature.getPredicate("cpos");
      UserPredicate prefix = (UserPredicate) signature.getPredicate("prefix");

      TabFormatCorpus.AttributeExtractor words = new TabFormatCorpus.AttributeExtractor(word, 2);
      words.addMapping(0, 0);
      words.addMapping(1, 1, new Quote());
      //words.addToQuote(1);

      TabFormatCorpus.AttributeExtractor prefixes = new TabFormatCorpus.AttributeExtractor(prefix, 2);
      prefixes.addMapping(0, 0);
      prefixes.addMapping(1, 1, new Pipeline(new Prefix(5), new Quote()));

      TabFormatCorpus.AttributeExtractor cpostags = new TabFormatCorpus.AttributeExtractor(cpos, 2);
      cpostags.addMapping(0, 0);
      cpostags.addMapping(3, 1);

      TabFormatCorpus.AttributeExtractor postags = new TabFormatCorpus.AttributeExtractor(pos, 2);
      postags.addMapping(0, 0);
      postags.addMapping(4, 1);

      TabFormatCorpus.AttributeExtractor links = new TabFormatCorpus.AttributeExtractor(link, 2);
      links.addMapping(0, 1);
      links.addMapping(6, 0);

      TabFormatCorpus.AttributeExtractor deps = new TabFormatCorpus.AttributeExtractor(dep, 3);
      deps.addMapping(0, 1);
      deps.addMapping(6, 0);
      deps.addMapping(7, 2);

      TabFormatCorpus corpus = new TabFormatCorpus(signature, file);
      corpus.addExtractor(prefixes);
      corpus.addExtractor(words);
      corpus.addExtractor(cpostags);
      corpus.addExtractor(postags);
      corpus.addExtractor(links);
      corpus.addExtractor(deps);

      corpus.addConstantAtom(prefix, 0, "ROOT");
      corpus.addConstantAtom(word, 0, "ROOT");
      corpus.addConstantAtom(pos, 0, "ROOT");
      corpus.addConstantAtom(cpos, 0, "ROOT");

      corpus.addWriter(word, new TokenFeatureWriter(0,0,0));
      corpus.addWriter(word, new TokenFeatureWriter(1,0,1, new Dequote()));
      corpus.addWriter(word, new ConstantWriter(2,0,"_"));
      corpus.addWriter(cpos, new TokenFeatureWriter(3,0,1, new Dequote()));
      corpus.addWriter(pos, new TokenFeatureWriter(4,0,1, new Dequote()));
      corpus.addWriter(word, new ConstantWriter(5,0,"_"));
      corpus.addWriter(link, new TokenFeatureWriter(6,1,0));
      corpus.addWriter(dep, new TokenFeatureWriter(7,1,2));

      return corpus;
    }


  }

  public static class Generator implements TypeGenerator {

    private HashMultiMap<Integer, TokenCollector> collectors = new HashMultiMap<Integer, TokenCollector>();

    public static class TokenCollector {
      public String typeName;
      public boolean unknowns;
      public TokenProcessor processor;
      public HashSet<String> tokens = new HashSet<String>();

      public TokenCollector(String typeName, boolean unknowns, TokenProcessor processor) {
        this.typeName = typeName;
        this.unknowns = unknowns;
        this.processor = processor;
      }

      public TokenCollector(String typeName, boolean unknowns, TokenProcessor processor, String... initial) {
        this.typeName = typeName;
        this.unknowns = unknowns;
        this.processor = processor;
        for (String s : initial) tokens.add(s);
      }


      public void collect(String token) {
        tokens.add(processor.process(token));
      }
    }


    public void addTokenCollector(int column, String typeName, boolean unknown,
                                  TokenProcessor processor, String... intial) {
      collectors.add(column, new TokenCollector(typeName, unknown, processor, intial));
    }


    public void generateTypes(InputStream is, Signature signature) {
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
          line = line.trim();
          if (!line.equals("")) {
            String[] split = line.split("[\t ]");
            for (int col = 0; col < split.length; ++col) {
              List<TokenCollector> collectors4column = collectors.get(col);
              if (collectors4column != null) for (TokenCollector collector : collectors4column)
                collector.collect(split[col]);
            }
          }
        }
        for (List<TokenCollector> list : collectors.values())
          for (TokenCollector collector : list)
            signature.createType(collector.typeName, collector.unknowns, new LinkedList<String>(collector.tokens));

      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
  }

  public TabFormatCorpus(Signature signature, File file, Extractor... extractors) {
    this.file = file;
    this.signature = signature;
    try {
      out = new PrintStream(new FileOutputStream(file, true));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    int index = 0;
    for (Extractor extractor : extractors)
      this.extractors.add(index++, extractor);
  }

  public void addExtractor(Extractor extractor, int... cols) {
    for (int col : cols)
      this.extractors.add(col, extractor);
  }

  public void addExtractor(Extractor extractor) {
    for (int col : extractor.getColumns())
      this.extractors.add(col, extractor);
  }

  public TabFormatCorpus(Signature signature, byte[] bytes, Extractor... extractors) {
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
    return signature;
  }

  public ListIterator<GroundAtoms> listIterator() {
    return null;
  }

  public int getUsedMemory() {
    return 1000;
  }

  public class ColumnTable {
    private HashMap<Integer, HashMap<Integer, String>> rows = new HashMap<Integer, HashMap<Integer, String>>();

    public void set(int line, int column, String value) {
      HashMap<Integer, String> row = rows.get(line);
      if (row == null) {
        row = new HashMap<Integer, String>();
        rows.put(line, row);
      }
      row.put(column, value);
    }

    public void write(PrintStream os, int firstLine) {

      for (int i = firstLine; i < rows.size(); ++i) {
        HashMap<Integer, String> row = rows.get(i);
        for (int j = 0; j < columns; ++j) {
          if (j > 0) os.print("\t");
          String value = row.get(j);
          os.print(value != null ? value : "_");
        }
        os.println();
      }
      os.println();
    }
  }

  public void addWriter(UserPredicate predicate, AtomWriter writer) {
    writers.add(predicate, writer);
    if (writer.getColumn() >= columns)
      columns = writer.getColumn() + 1;
  }

  public static interface AtomWriter {
    int getColumn();
    void write(GroundAtom atom, ColumnTable table);
  }

  public static class ConstantWriter implements AtomWriter {

    private int tokenArg;
    private int column;
    private String constant;


    public ConstantWriter(int column, int tokenArg, String constant) {
      this.column = column;
      this.tokenArg = tokenArg;
      this.constant = constant;
    }


    public int getColumn() {
      return column;
    }

    public void write(GroundAtom atom, ColumnTable table) {
      IntConstant token = (IntConstant) atom.getArguments().get(tokenArg);
      table.set(token.getInteger(),column,constant);      
    }
  }

  public static class TokenFeatureWriter implements AtomWriter {

    private int tokenArg;
    private int featureArg;
    private int column;
    private TokenProcessor processor;

    public TokenFeatureWriter(int column, int tokenArg, int featureArg, TokenProcessor processor) {
      this.column = column;
      this.tokenArg = tokenArg;
      this.featureArg = featureArg;
      this.processor = processor;
    }


    public int getColumn() {
      return column;
    }

    public TokenFeatureWriter(int column, int tokenArg, int featureArg) {
      this(column,tokenArg,featureArg, new Itself());
    }

    public void write(GroundAtom atom, ColumnTable table) {
      IntConstant token = (IntConstant) atom.getArguments().get(tokenArg);
      Constant feature = atom.getArguments().get(featureArg);
      table.set(token.getInteger(), column, processor.process(feature.toString()));
    }
  }

  public void append(GroundAtoms atoms) {
    ColumnTable table = new ColumnTable();
    for (UserPredicate predicate : signature.getUserPredicates()) {
      for (AtomWriter writer : writers.get(predicate))
        for (GroundAtom atom : atoms.getGroundAtomsOf(predicate)) {
          writer.write(atom, table);
        }
    }
    table.write(out, printZeroRow ? 0 : 1);
    out.flush();
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
            List<Extractor> extractorsForCol = TabFormatCorpus.this.extractors.get(col);
            if (extractorsForCol != null) for (Extractor extractor : extractorsForCol)
              extractor.beginLine(lineNr);
          }
          for (int col = 0; col < split.length; ++col) {
            List<Extractor> extractorsForCol = TabFormatCorpus.this.extractors.get(col);
            if (extractorsForCol != null) for (Extractor extractor : extractorsForCol)
              extractor.extract(col, split[col]);
          }
          for (int col = 0; col < split.length; ++col) {
            List<Extractor> extractorsForCol = TabFormatCorpus.this.extractors.get(col);
            if (extractorsForCol != null) for (Extractor extractor : extractorsForCol)
              extractor.endLine(next);
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

  public void addConstantAtom(UserPredicate predicate, Object... arg) {
    int index = 0;
    Constant[] constants = new Constant[arg.length];
    for (Type type : predicate.getArgumentTypes()) {
      constants[index] = type.getConstant(arg[index++].toString());
    }
    constantAtoms.put(predicate, constants);
  }

  public static interface Extractor {
    Collection<Integer> getColumns();

    void beginLine(int lineNr);

    void endLine(GroundAtoms atoms);

    void extract(int column, String value);
  }

  public static class AttributeExtractor implements Extractor {

    private HashMap<Integer, Integer> mapping;
    private UserPredicate predicate;
    private Constant[] args;
    private HashMap<Integer, TokenProcessor> processors = new HashMap<Integer, TokenProcessor>();
    private HashSet<Integer> lineNrArgs = new HashSet<Integer>();

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

    public void addLineNrArg(int argIndex) {
      lineNrArgs.add(argIndex);
    }

    public void addMapping(int column, int argIndex) {
      mapping.put(column, argIndex);
      processors.put(column, new Itself());
    }

    public void addMapping(int column, int argIndex, TokenProcessor processor) {
      mapping.put(column, argIndex);
      processors.put(column, processor);
    }

    public void beginLine(int lineNr) {
      for (int i : lineNrArgs) {
        args[i] = Type.INT.toConstant(lineNr);
      }
    }

    public void endLine(GroundAtoms atoms) {
      atoms.getGroundAtomsOf(predicate).addGroundAtom(args);
    }

    public void extract(int column, String value) {
      int argIndex = mapping.get(column);
      Type type = predicate.getArgumentTypes().get(argIndex);
      args[argIndex] = type.getConstant(processors.get(column).process(value));
    }

    public Collection<Integer> getColumns() {
      return processors.keySet();
    }
  }


}
