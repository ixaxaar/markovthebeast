package thebeast.pml.corpora;

import thebeast.pml.*;
import thebeast.pml.term.Constant;
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
  private HashMultiMap<Integer, Extractor> col2extractor = new HashMultiMap<Integer, Extractor>();
  private HashMultiMap<UserPredicate, AtomWriter> writers = new HashMultiMap<UserPredicate, AtomWriter>();
  private HashMap<UserPredicate, Constant[]> constantAtoms = new HashMap<UserPredicate, Constant[]>();
  private boolean printZeroRow = false;

  private HashSet<Extractor> extractors = new HashSet<Extractor>();

  private int columns = 0;
  private PrintStream out;


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
      this.col2extractor.add(index++, extractor);
  }

  public void addExtractor(Extractor extractor, int... cols) {
    for (int col : cols)
      this.col2extractor.add(col, extractor);
    extractors.add(extractor);

  }

  public void addExtractor(Extractor extractor) {
    for (int col : extractor.getColumns())
      this.col2extractor.add(col, extractor);
    extractors.add(extractor);
  }

  public TabFormatCorpus(Signature signature, byte[] bytes, Extractor... extractors) {
    this.signature = signature;
    this.bytes = bytes;
    int index = 0;
    for (Extractor extractor : extractors)
      this.col2extractor.add(index++, extractor);
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
          for (Extractor extractor : extractors)
            extractor.beginLine(lineNr);
          for (int col = 0; col < split.length; ++col) {
            List<Extractor> extractorsForCol = TabFormatCorpus.this.col2extractor.get(col);
            if (extractorsForCol != null) for (Extractor extractor : extractorsForCol)
              extractor.extract(col, split[col]);
          }
          for (Extractor extractor : extractors)
            extractor.endLine(next);
          ++lineNr;
        }

        for (Extractor extractor : extractors)
          extractor.endSentence(next);

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


}
