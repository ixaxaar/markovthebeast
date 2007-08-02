package thebeast.pml;

import junit.framework.TestCase;
import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.value.TupleValue;
import thebeast.nod.expression.Operator;
import thebeast.nod.expression.RelationExpression;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.type.RelationType;
import thebeast.nod.type.CategoricalType;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.RelationVariable;
import thebeast.pml.corpora.*;
import thebeast.pml.formula.Conjunction;
import thebeast.pml.formula.FactorFormula;
import thebeast.pml.formula.FormulaBuilder;
import thebeast.pml.formula.QueryGenerator;
import thebeast.pml.function.WeightFunction;
import thebeast.pml.term.CategoricalConstant;
import thebeast.pml.training.FeatureCollector;
import thebeast.pml.training.OnlineLearner;
import thebeast.pml.training.PerceptronUpdateRule;
import thebeast.pml.training.TrainingInstances;
import thebeast.pml.solve.ilp.ILPSolverLpSolve;
import thebeast.pml.solve.CuttingPlaneSolver;
import thebeast.pml.solve.ilp.ILPSolver;
import thebeast.pml.solve.ilp.IntegerLinearProgram;
import thebeast.pml.solve.ilp.ILPGrounder;
import thebeast.util.QuietProgressReporter;
import thebeast.util.TreeProfiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 17:38:07
 */
public class TestTheBeast extends TestCase {
  protected TheBeast server;
  protected FormulaBuilder builder;
  protected Type tag;
  protected UserPredicate token;
  protected Type word;
  protected Signature signature;
  protected WeightFunction weightFunction1;
  protected Type label;
  protected UserPredicate phrase;
  protected WeightFunction weightFunction2;
  private GroundAtoms theManLikesTheBoat;
  private Weights weights;
  private FactorFormula factorThe;
  private FactorFormula factorDT;
  private Model model;
  private FactorFormula np_vp_s;
  private WeightFunction weightFunction3;
  private Interpreter interpreter;
  private Scores scores;
  private LocalFeatures features;

  protected void setUp() {
    server = TheBeast.getInstance();
    signature = server.createSignature();
    builder = new FormulaBuilder(signature);
    word = signature.createType("Word", true, "the", "man", "likes", "boat", "\"likes\"");
    tag = signature.createType("Tag", false, "DT", "NN", "JJ", "VBZ");
    label = signature.createType("Label", false, "NP", "VP", "PP", "S");
    token = signature.createPredicate("token", Type.INT, word, tag);
    phrase = signature.createPredicate("phrase", Type.INT, Type.INT, label);
    weightFunction1 = signature.createWeightFunction("wThe", tag, label);
    weightFunction2 = signature.createWeightFunction("wDt", label);
    weightFunction3 = signature.createWeightFunction("wrule", true, label, label, label);

    theManLikesTheBoat = signature.createGroundAtoms();
    GroundAtomCollection tokens = theManLikesTheBoat.getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");

    weights = signature.createWeights();
    weights.addWeight(weightFunction1, 1.5, "DT", "NP");
    weights.addWeight(weightFunction1, 2.0, "NN", "VP");
    weights.addWeight(weightFunction2, 5.5, "NP");
    weights.addWeight(weightFunction2, -3.0, "VP");
    weights.addWeight(weightFunction3, 3.0, "NP", "VP", "S");

    FormulaBuilder builder = new FormulaBuilder(signature);

    builder.var(Type.INT, "t1").var(Type.INT, "t2").var(label, "l").var(tag, "p").quantify();
    builder.var("t1").dontCare().var("p").atom("token").
            var("t2").dontCare().dontCare().atom("token").
            var("t1").var("t2").intLEQ().
            and(3).condition();
    builder.var("t1").var("t2").var("l").atom("phrase").formula();
    builder.var("p").var("l").apply(weightFunction1).weight();

    factorThe = builder.produceFactorFormula();

    builder.var(Type.INT, "t1").var(Type.INT, "t2").var(label, "l").quantify();
    builder.var("t1").dontCare().term("DT").atom("token").
            var("t2").dontCare().dontCare().atom("token").
            var("t1").var("t2").intLEQ().
            and(3).condition();
    builder.var("t1").var("t2").var("l").atom("phrase").formula();
    builder.var("l").apply(weightFunction2).weight();

    factorDT = builder.produceFactorFormula();

    builder.var(Type.INT, "b").var(Type.INT, "m").var(Type.INT, "e").
            var(label, "left").var(label, "right").var(label, "parent").quantify();
    builder.var("b").var("m").var("left").atom("phrase");
    builder.var("m").term(1).add().var("e").var("right").atom("phrase").and(2);
    builder.var("b").var("e").var("parent").atom("phrase").implies().formula();
    builder.var("left").var("right").var("parent").apply(weightFunction3).weight();

    np_vp_s = builder.produceFactorFormula();


    model = signature.createModel();

    model.addFactorFormula(factorThe);
    model.addFactorFormula(factorDT);
    model.addFactorFormula(np_vp_s);
    model.addHiddenPredicate(phrase);
    model.addObservedPredicate(token);

    System.out.println(factorDT);

    features = new LocalFeatures(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    extractor.extract(theManLikesTheBoat, features);

    scores = new Scores(model, weights);
    scores.score(features, theManLikesTheBoat);

    interpreter = TheBeast.getInstance().getNodServer().interpreter();
  }

  public void testCreateType() {
    assertEquals(Type.Class.CATEGORICAL, tag.getTypeClass());
    assertEquals("DT", ((CategoricalConstant) tag.getConstant("DT")).getName());
    assertEquals(Type.Class.CATEGORICAL_UNKNOWN, word.getTypeClass());
  }

  public void testCreatePredicate() {
    assertEquals(token.getArgumentTypes().get(0), Type.INT);
    assertEquals(token.getArgumentTypes().get(1), word);
    assertEquals(token.getArgumentTypes().get(2), tag);
  }

  public void testLoadAtoms() throws IOException {
    GroundAtoms groundAtoms = signature.createGroundAtoms();
    GroundAtomCollection tokens = groundAtoms.getGroundAtomsOf(token);
    tokens.load("" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n");
    System.out.println(tokens.getRelationVariable().value());
    assertTrue(tokens.containsAtom(0, "the", "DT"));
    assertTrue(tokens.containsAtom(2, "\"likes\"", "VBZ"));
    assertEquals(5, tokens.size());

    groundAtoms.load("" +
            ">token\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            ">phrase\n" +
            "0 1 NP\n" +
            "2 4 VP\n");

    assertTrue(tokens.containsAtom(0, "the", "DT"));
    assertTrue(tokens.containsAtom(2, "\"likes\"", "VBZ"));
    assertEquals(5, tokens.size());

    GroundAtomCollection phrases = groundAtoms.getGroundAtomsOf(phrase);
    assertEquals(2, phrases.size());
    assertTrue(phrases.containsAtom(0, 1, "NP"));
    assertTrue(phrases.containsAtom(2, 4, "VP"));

    System.out.println(groundAtoms);
  }

  public void testRemoveGroundAtoms(){
    GroundAtomCollection tokens = theManLikesTheBoat.getGroundAtomsOf(token);
    tokens.remove(null,null,"DT");
    System.out.println(tokens);
    assertEquals(3, tokens.size());
    assertTrue(tokens.containsAtom(1,"man","NN"));
    assertTrue(tokens.containsAtom(2,"likes","VBZ"));
    assertTrue(tokens.containsAtom(4,"boat","NN"));
  }

  public void testLoadWeights() throws IOException {
    Weights weights = signature.createWeights();

    weights.load("" +
            ">wThe\n" +
            "NN  NP 3.5\n" +
            "VBZ VP 1.3\n\n" +
            ">wDt\n" +
            "NP 4.5\n" +
            "VP 0.2\n");

    System.out.println(weights.getRelation(weightFunction1).value());
    System.out.println(weights.getRelation(weightFunction2).value());
    System.out.println(weights.getWeights().value());
    assertEquals(4, weights.getFeatureCount());
    assertEquals(3.5, weights.getWeight(weightFunction1, "NN", "NP"));
    assertEquals(1.3, weights.getWeight(weightFunction1, "VBZ", "VP"));
    assertEquals(4.5, weights.getWeight(weightFunction2, "NP"));
    assertEquals(0.2, weights.getWeight(weightFunction2, "VP"));

    weights.save(System.out);

  }

  public void testLoadScores() throws IOException {
    Scores scores = new Scores(model, weights);

    scores.load("" +
            ">phrase\n" +
            "0 1 NP 1.3\n" +
            "0 0 NP -3.3\n" +
            "2 4 VP 5.2\n");

    assertTrue(scores.contains(phrase, 1.3, 0, 1, "NP"));
    assertTrue(scores.contains(phrase, -3.3, 0, 0, "NP"));
    assertTrue(scores.contains(phrase, 5.2, 2, 4, "VP"));

  }

  public void testLoadCorpora() {

    String corpusTxt = "" +
            ">>\n" +
            ">token\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            ">phrase\n" +
            "0 1 NP\n" +
            ">>\n" +
            ">token\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            ">phrase\n" +
            "0 1 NP\n" +
            "2 4 VP\n";

    ByteArrayCorpus bytes = new ByteArrayCorpus(signature, corpusTxt.getBytes());
    SequentialAccessCorpus corpus = new SequentialAccessCorpus(bytes);
    assertEquals(2, corpus.size());
    assertEquals(1, corpus.get(0).getGroundAtomsOf(phrase).size());
    assertEquals(5, corpus.get(0).getGroundAtomsOf(token).size());
    assertEquals(2, corpus.get(1).getGroundAtomsOf(phrase).size());
    assertEquals(5, corpus.get(1).getGroundAtomsOf(token).size());
    System.out.println(corpus.get(0));
    System.out.println(corpus.get(1));
  }


  public void testLoadAuxiliaryCorpora() {

    String corpusTxt = "" +
            ">>\n" +
            ">token\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            ">phrase\n" +
            "0 1 NP\n" +
            ">>\n" +
            ">token\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            ">phrase\n" +
            "0 1 NP\n" +
            "2 4 VP\n";

    UserPredicate twoPhrases = signature.createPredicate("twoPhrases", "Int", "Int", "Int", "Label", "Label");
    model.addAuxilaryPredicate(twoPhrases);

    builder.var("Int", "b").var("Int", "m").var("Int", "e").var("Label", "p1").var("Label", "p2").quantify();
    builder.var("b").var("m").term(1).minus().var("p1").atom("phrase");
    builder.var("m").var("e").var("p2").atom("phrase").and(2);
    builder.var("b").var("m").var("e").var("p1").var("p2").atom("twoPhrases").implies().formula();
    builder.term(Double.POSITIVE_INFINITY).weight();

    FactorFormula generator = builder.produceFactorFormula("generator");
    model.addFactorFormula(generator);

    ByteArrayCorpus bytes = new ByteArrayCorpus(signature, corpusTxt.getBytes());
    AugmentedCorpus augmented = new AugmentedCorpus(model, bytes);
    SequentialAccessCorpus corpus = new SequentialAccessCorpus(augmented);
    assertEquals(2, corpus.size());
    assertEquals(1, corpus.get(0).getGroundAtomsOf(phrase).size());
    assertEquals(5, corpus.get(0).getGroundAtomsOf(token).size());
    assertEquals(2, corpus.get(1).getGroundAtomsOf(phrase).size());
    assertEquals(5, corpus.get(1).getGroundAtomsOf(token).size());
    GroundAtomCollection twoPhrasesAtoms = corpus.get(1).getGroundAtomsOf(twoPhrases);
    assertEquals(1, twoPhrasesAtoms.size());
    assertTrue(twoPhrasesAtoms.containsAtom(0, 2, 4, "NP", "VP"));
    System.out.println(corpus.get(0));
    System.out.println(corpus.get(1));
  }

  public void testLoadCoNLL() {

    String corpusTxt = "" +
            "\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            "\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n";

    AttributeExtractor tokenExtractor = new AttributeExtractor(token, 3);
    tokenExtractor.addMapping(0, 0);
    tokenExtractor.addMapping(1, 1);
    tokenExtractor.addMapping(2, 2);

    TabFormatCorpus corpus = new TabFormatCorpus(signature, corpusTxt.getBytes());
    corpus.addExtractor(tokenExtractor);
    SequentialAccessCorpus seqCorpus = new SequentialAccessCorpus(corpus);
    System.out.println(seqCorpus.get(0));
    assertEquals(2, seqCorpus.size());
    assertEquals(0, seqCorpus.get(0).getGroundAtomsOf(phrase).size());
    assertEquals(5, seqCorpus.get(0).getGroundAtomsOf(token).size());
    assertEquals(0, seqCorpus.get(1).getGroundAtomsOf(phrase).size());
    assertEquals(5, seqCorpus.get(1).getGroundAtomsOf(token).size());
    assertTrue(seqCorpus.get(1).getGroundAtomsOf(token).containsAtom(0, "the", "DT"));


  }

  public void testGenerateCoNLLTypes() {

    String corpusTxt = "" +
            "\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 likes VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            "\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 likes VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n";

    TabFormatCorpus.Generator generator = new TabFormatCorpus.Generator();
    generator.addTokenCollector(1, "Word", true, new Quote());
    generator.addTokenCollector(2, "Tag", false, new Itself());
//    generator.addTypeTemplate(1, "Word", true, true);
//    generator.addTypeTemplate(2, "Tag", false, false);

    Signature signature = TheBeast.getInstance().createSignature();
    generator.generateTypes(new ByteArrayInputStream(corpusTxt.getBytes()), signature);
    System.out.println(signature.getType("Word").getConstants());

    assertTrue(signature.getType("Word").getTypeClass() == Type.Class.CATEGORICAL_UNKNOWN);
    assertTrue(signature.getType("Word").getConstants().contains("\"man\""));
    assertTrue(signature.getType("Tag").getConstants().contains("DT"));
    assertEquals(4, signature.getType("Word").getConstants().size());
    assertEquals(3, signature.getType("Tag").getConstants().size());

  }


  public void testAddGroundAtom() {
    GroundAtoms groundAtoms = signature.createGroundAtoms();
    GroundAtomCollection tokens = groundAtoms.getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");

    assertEquals(5, tokens.size());
    System.out.println(tokens.getRelationVariable().value());
    System.out.println(tokens);

    assertTrue(tokens.containsAtom(0, "the", "DT"));
    assertTrue(tokens.containsAtom(1, "man", "NN"));
    assertTrue(tokens.containsAtom(2, "likes", "VBZ"));
    assertTrue(tokens.containsAtom(3, "the", "DT"));
    assertTrue(tokens.containsAtom(4, "boat", "NN"));
    assertFalse(tokens.containsAtom(0, "the", "NN"));
  }

  public void testAddWeight() {
    Weights weights = signature.createWeights();
    weights.addWeight(weightFunction1, 1.5, "DT", "NP");
    weights.addWeight(weightFunction1, -2.0, "NN", "VP");
    assertEquals(1.5, weights.getWeight(weightFunction1, "DT", "NP"));
    assertEquals(-2.0, weights.getWeight(weightFunction1, "NN", "VP"));
    assertEquals(0.0, weights.getWeight(weightFunction1, "DT", "VP"));
    assertEquals(2, weights.getFeatureCount());
  }

  public void testWeightFunctionZeroArity() {
    WeightFunction function = signature.createWeightFunction("w");
    Weights weights = signature.createWeights();
    weights.addWeight(function, 1.5);
    assertEquals(1.5, weights.getWeight(function));
  }

  public void testSaveWeight() throws IOException {
    Weights weights = signature.createWeights();
    weights.addWeight(weightFunction1, 1.5, "DT", "NP");
    weights.addWeight(weightFunction1, -2.0, "NN", "VP");
    weights.addWeight(weightFunction2, -2.0, "VP");

    File file = new File("/tmp/test");
    file.delete();
    FileSink sink = server.getNodServer().createSink(file, 1024);
    weights.write(sink);
    sink.flush();
    weights.clear();
    weights.addWeight(weightFunction1, 1.5, "NN", "NP");
    weights.addWeight(weightFunction2, -2.0, "NP");
    weights.addWeight(weightFunction1, 2.0, "VBZ", "VP");
    weights.clear();

    FileSource source = server.getNodServer().createSource(file, 1024);
    weights.read(source);

    assertEquals(1.5, weights.getWeight(weightFunction1, "DT", "NP"));
    assertEquals(0.0, weights.getWeight(weightFunction1, "NN", "NP"));
    assertEquals(-2.0, weights.getWeight(weightFunction1, "NN", "VP"));
    assertEquals(-2.0, weights.getWeight(weightFunction2, "VP"));
    assertEquals(3, weights.getFeatureCount());

    file.delete();

  }


  public void testSparseAddWeight() {
    Weights weights = signature.createWeights();
    weights.addWeight(weightFunction1, 1.5, "DT", "NP");
    weights.addWeight(weightFunction1, -2.0, "NN", "VP");
    weights.addWeight(weightFunction1, 4.0, "VBZ", "VP");
    SparseVector vector = new SparseVector();
    vector.addValue(0, 0.75);
    vector.addValue(1, 2.0);
    vector.addValue(2, -1.5);
    weights.add(2.0, vector);

    assertEquals(3.0, weights.getWeight(weightFunction1, "DT", "NP"));
    assertEquals(2.0, weights.getWeight(weightFunction1, "NN", "VP"));
    assertEquals(1.0, weights.getWeight(weightFunction1, "VBZ", "VP"));
  }

  public void testSparseVectorIndices() {
    SparseVector vector1 = new SparseVector();
    vector1.addValue(0, 0.75);
    vector1.addValue(1, 2.0);
    vector1.addValue(2, -1.5);

    SparseVector vector2 = new SparseVector();
    vector2.addValue(3, 0.75);
    vector2.addValue(4, 2.0);
    vector2.addValue(2, -1.5);

    SparseVector.Indices indices = new SparseVector.Indices();
    indices.add(vector1.getIndices());
    indices.add(vector2.getIndices());

    assertTrue(indices.contains(4));
    assertFalse(indices.contains(5));
    assertEquals(5, indices.size());
    System.out.println(indices);

  }


  public void testCompactify() {
    SparseVector vector = new SparseVector();
    vector.addValue(0, 0.75);
    vector.addValue(1, 2.0);
    vector.addValue(2, -1.5);
    vector.addValue(3, 0.0);
    vector.compactify();
    System.out.println(vector);
    int[] indices = vector.getIndexArray();
    double[] values = vector.getValueArray();
    assertEquals(0, indices[0]);
    assertEquals(1, indices[1]);
    assertEquals(2, indices[2]);
    assertEquals(0.75, values[0]);
    assertEquals(2.0, values[1]);
    assertEquals(-1.5, values[2]);
    assertEquals(3, values.length);
  }

  public void testAddWeightGetIndex() {
    Weights weights = signature.createWeights();
    weights.addWeight(weightFunction1, 1.5, "DT", "NP");
    weights.addWeight(weightFunction1, -2.0, "NN", "VP");
    assertEquals(0, weights.getIndex(weightFunction1, "DT", "NP"));
    assertEquals(1, weights.getIndex(weightFunction1, "NN", "VP"));
    assertEquals(-1, weights.getIndex(weightFunction1, "DT", "VP"));
    System.out.println(weights.getFeatureString(1));
  }

  public void testAddWeightGetWeight() {
    Weights weights = signature.createWeights();
    weights.addWeight(weightFunction1, 1.5, "DT", "NP");
    weights.addWeight(weightFunction1, -2.0, "NN", "VP");
    assertEquals(1.5, weights.getWeight(0));
    assertEquals(-2.0, weights.getWeight(1));
    assertEquals(0.0, weights.getWeight(2));
  }

  public void testDumpGroundAtoms() throws IOException {
    GroundAtoms groundAtoms = signature.createGroundAtoms();
    GroundAtomCollection tokens = groundAtoms.getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");
    GroundAtomCollection phrases = groundAtoms.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");
    phrases.addGroundAtom(0, 4, "S");

    File file = new File("tmp");
    FileSink fileSink = server.getNodServer().createSink(file, 1024);
    FileSource fileSource = server.getNodServer().createSource(file, 1024);
    groundAtoms.write(fileSink);
    fileSink.flush();
    groundAtoms.clear(signature.getUserPredicates());
    assertEquals(0, tokens.size());
    groundAtoms.read(fileSource);

    assertEquals(5, tokens.size());
    assertEquals(3, phrases.size());
    System.out.println(tokens.getRelationVariable().value());
    System.out.println(tokens);

    assertTrue(tokens.containsAtom(0, "the", "DT"));
    assertTrue(tokens.containsAtom(1, "man", "NN"));
    assertTrue(tokens.containsAtom(2, "likes", "VBZ"));
    assertTrue(tokens.containsAtom(3, "the", "DT"));
    assertTrue(tokens.containsAtom(4, "boat", "NN"));
    assertFalse(tokens.containsAtom(0, "the", "NN"));

    file.delete();

  }

  public void testDumpedCorpus() throws IOException {
    GroundAtoms groundAtoms = signature.createGroundAtoms();
    GroundAtomCollection tokens = groundAtoms.getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");
    GroundAtomCollection phrases = groundAtoms.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");
    phrases.addGroundAtom(0, 4, "S");

    RandomAccessCorpus ramCorpus = new RandomAccessCorpus(signature, 50);
    for (int i = 0; i < 50; ++i)
      ramCorpus.add(groundAtoms);

    File file1 = new File("tmp1");
    File file2 = new File("tmp2");
    file1.delete();
    file2.delete();
    DumpedCorpus dumpedCorpus1 = new DumpedCorpus(file1, ramCorpus, 100 * 1024);
    DumpedCorpus dumpedCorpus2 = new DumpedCorpus(file2, ramCorpus, 10 * 1024);

    for (GroundAtoms atoms : dumpedCorpus1) {
      System.out.print(".");
      tokens = atoms.getGroundAtomsOf(token);
      phrases = atoms.getGroundAtomsOf(phrase);
      assertEquals(5, tokens.size());
      assertEquals(3, phrases.size());
      assertTrue(tokens.containsAtom(0, "the", "DT"));
      assertTrue(tokens.containsAtom(1, "man", "NN"));
      assertTrue(tokens.containsAtom(2, "likes", "VBZ"));
      assertTrue(tokens.containsAtom(3, "the", "DT"));
      assertTrue(tokens.containsAtom(4, "boat", "NN"));
      assertFalse(tokens.containsAtom(0, "the", "NN"));
    }
    //call it again.
    System.out.println("");
    for (GroundAtoms atoms : dumpedCorpus1) {
      System.out.print(".");
      tokens = atoms.getGroundAtomsOf(token);
      phrases = atoms.getGroundAtomsOf(phrase);
      assertEquals(5, tokens.size());
      assertEquals(3, phrases.size());
      assertTrue(tokens.containsAtom(0, "the", "DT"));
      assertTrue(tokens.containsAtom(1, "man", "NN"));
      assertTrue(tokens.containsAtom(2, "likes", "VBZ"));
      assertTrue(tokens.containsAtom(3, "the", "DT"));
      assertTrue(tokens.containsAtom(4, "boat", "NN"));
      assertFalse(tokens.containsAtom(0, "the", "NN"));
    }
    System.out.println("");
    for (GroundAtoms atoms : dumpedCorpus2) {
      System.out.print(".");
      tokens = atoms.getGroundAtomsOf(token);
      phrases = atoms.getGroundAtomsOf(phrase);
      assertEquals(5, tokens.size());
      assertEquals(3, phrases.size());
      assertTrue(tokens.containsAtom(0, "the", "DT"));
      assertTrue(tokens.containsAtom(1, "man", "NN"));
      assertTrue(tokens.containsAtom(2, "likes", "VBZ"));
      assertTrue(tokens.containsAtom(3, "the", "DT"));
      assertTrue(tokens.containsAtom(4, "boat", "NN"));
      assertFalse(tokens.containsAtom(0, "the", "NN"));
    }

    //noinspection MismatchedQueryAndUpdateOfCollection
    DumpedCorpus dumpedCorpus3 = new DumpedCorpus(signature, file1, 10 * 1024);
    System.out.println(dumpedCorpus3.getActiveCount());
    System.out.println("");
    for (GroundAtoms atoms : dumpedCorpus3) {
      System.out.print(".");
      tokens = atoms.getGroundAtomsOf(token);
      phrases = atoms.getGroundAtomsOf(phrase);
      assertEquals(5, tokens.size());
      assertEquals(3, phrases.size());
      assertTrue(tokens.containsAtom(0, "the", "DT"));
      assertTrue(tokens.containsAtom(1, "man", "NN"));
      assertTrue(tokens.containsAtom(2, "likes", "VBZ"));
      assertTrue(tokens.containsAtom(3, "the", "DT"));
      assertTrue(tokens.containsAtom(4, "boat", "NN"));
      assertFalse(tokens.containsAtom(0, "the", "NN"));
    }

    System.out.println("");
    file1.delete();
    file2.delete();
  }


  public void testLoadModel() throws Exception {
    String input = "//an example;\n" +
            "type Tag: DT, NN, VBZ, JJ;" +
            "type Label: NP, VP, PP;" +
            "type Word: \" the \", \"man\";" +
            "predicate token: Int x Word x Tag;" +
            "predicate phrase: Int x Int x Label;" +
            "hidden: phrase;" +
            "observed: token;" +
            "weight w: Tag x Tag x Label-> Double;\n" +
            "factor: for Int t1, Int t2, Tag p1, Tag p2, Label l " +
            "if token(t1,_,p1) & token(t2,_,p1) " +
            "add [phrase(t1,t2,l)] * w(p1,p2,l);";

    Model model = TheBeast.getInstance().loadModel(new ByteArrayInputStream(input.getBytes()));
    Signature signature = model.getSignature();

    assertEquals(Type.Class.CATEGORICAL, signature.getType("Tag").getTypeClass());
    assertEquals(1, model.getHiddenPredicates().size());
    assertEquals(1, model.getObservedPredicates().size());

    FactorFormula formula = model.getFactorFormulas().get(0);
    assertTrue(formula.getCondition() instanceof Conjunction);
    System.out.println(model.getFactorFormulas().get(0));
  }

  public void testLocalFeatures() {

    Weights weights = signature.createWeights();
    weights.addWeight(weightFunction1, 0.0, "DT", "NP");
    weights.addWeight(weightFunction1, 0.0, "VBZ", "VP");
    weights.addWeight(weightFunction2, 0.0, "NP");
    weights.addWeight(weightFunction2, 0.0, "VP");
    weights.addWeight(weightFunction3, 0.0, "NP", "VP", "S");
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    LocalFeatures features = new LocalFeatures(model, weights);
    extractor.extract(theManLikesTheBoat, features);
    System.out.println(features.getRelation(phrase).value());
    assertEquals(24, features.getRelation(phrase).value().size());
    assertTrue(features.containsFeature(phrase, 0, 3, 4, "NP"));
    assertTrue(features.containsFeature(phrase, 3, 0, 1, "VP"));
    assertTrue(features.containsFeature(phrase, 1, 2, 4, "VP"));
    assertTrue(features.containsFeature(phrase, 0, 0, 1, "NP"));
    assertTrue(features.containsFeature(phrase, 2, 0, 1, "NP"));
    System.out.println(features.toVerboseString());
    System.out.println(features.toVerboseString(phrase, 0, 1, "NP"));
    //features.invalidate();
    RelationVariable grouped = features.getGroupedRelation(phrase);
    System.out.println(grouped.value());
    assertEquals(17, grouped.value().size());
    assertTrue(grouped.contains(0, 1, "NP", new Object[]{new Object[]{0}, new Object[]{2}}));

    //test closure
    GroundAtoms closure = features.getClosure();
    assertEquals(17, closure.getGroundAtomsOf(phrase).size());
    System.out.println(closure);

  }

  public void testGEQCondition() {

    builder.var("Int","b").var("Int","e").quantify();
    builder.var("b").dontCare().dontCare().atom("token");
    builder.var("e").dontCare().dontCare().atom("token");
    builder.var("b").var("e").intLEQ();
    builder.var("Int","m").quantify().var("m");
    builder.dontCare().term("NN").atom("token").var("b").var("m").intLEQ().var("m").var("e").intLEQ().and(3);
    builder.cardinality().term(1).lowerBound().cardinalityConstraint(false);
    builder.and(4).condition();
    builder.var("b").var("e").term("NP").atom("phrase").formula().term(1.0).weight();

    Model model = new Model(signature);
    model.addHiddenPredicate(phrase);
    model.addObservedPredicate(token);
    model.addFactorFormula(builder.produceFactorFormula());
    Weights weights = new Weights(signature);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    LocalFeatures features = new LocalFeatures(model, weights);
    extractor.extract(theManLikesTheBoat, features);

    Scores scores = new Scores(model, weights);
    scores.score(features, theManLikesTheBoat);

    System.out.println(scores);

    assertEquals(11,scores.getScoreRelation(phrase).value().size());
    assertTrue(scores.getScoreRelation(phrase).contains(2,4,"NP",1.0));
    for (TupleValue tuple : scores.getScoreRelation(phrase).value()){
      assertEquals(1.0, tuple.doubleElement(3).getDouble());
    }


  }


  public void testScores() {
    LocalFeatures features = new LocalFeatures(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    extractor.extract(theManLikesTheBoat, features);
    System.out.println(features.getRelation(phrase).value().size());
    Scores scores = new Scores(model, weights);
    scores.score(features, theManLikesTheBoat);
    RelationVariable var = scores.getScoreRelation(phrase);
    System.out.println(var.value());
    assertEquals(19, var.value().size());
    assertEquals(7.0, scores.getScore(phrase, 0, 0, "NP"));
    assertEquals(0.0, scores.getScore(phrase, 2, 3, "VP"));

    //features.invalidate();
    Scores byGrouping = new Scores(model, weights);
    byGrouping.scoreWithGroups(features, theManLikesTheBoat);
    System.out.println(byGrouping);
    assertEquals(19, byGrouping.getScoreRelation(phrase).value().size());
    assertEquals(7.0, byGrouping.getScore(phrase, 0, 0, "NP"));
    assertEquals(0.0, byGrouping.getScore(phrase, 2, 3, "VP"));

    GroundAtoms gold = signature.createGroundAtoms();
    gold.getGroundAtomsOf(phrase).addGroundAtom(0, 1, "NP");
    gold.getGroundAtomsOf(phrase).addGroundAtom(3, 4, "NP");
    gold.getGroundAtomsOf(phrase).addGroundAtom(2, 4, "VP");

    byGrouping.penalize(gold);
    assertEquals(8.0, byGrouping.getScore(phrase, 0, 0, "NP"));
    assertEquals(6.0, byGrouping.getScore(phrase, 0, 1, "NP"));
    assertEquals(6.0, byGrouping.getScore(phrase, 3, 4, "NP"));


    System.out.println(byGrouping);

  }

  public void testDirectScores() throws IOException {
    UserPredicate directScore = signature.createPredicate("directScore","Int","Int","Label","Double");
    model.addObservedPredicate(directScore);

    GroundAtoms groundAtoms = signature.createGroundAtoms();
    groundAtoms.load("" +
            ">token\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n" +
            ">directScore\n" +
            "0 1 NP 0.7\n" +
            "2 4 VP 0.2\n");

    builder.var("Int","b").var("Int","e").var("Label","l").var("Double","score").quantify();
    builder.var("b").var("e").var("l").var("score").atom(directScore).condition();
    builder.var("b").var("e").var("l").atom(phrase).formula();
    builder.var("score").weight();
    FactorFormula directScoreFormula = builder.produceFactorFormula();
    model.addFactorFormula(directScoreFormula);

    LocalFeatures features = new LocalFeatures(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    extractor.extract(groundAtoms, features);
    System.out.println(features.getRelation(phrase).value().size());
    Scores scores = new Scores(model, weights);
    scores.score(features, groundAtoms);
    System.out.println(scores);
    RelationVariable var = scores.getScoreRelation(phrase);
    assertEquals(2, var.value().size());
    assertEquals(0.7, scores.getScore(phrase, 0, 1, "NP"));
    assertEquals(0.2, scores.getScore(phrase, 2, 4, "VP"));


  }

  public void testDirectWeightTerm() throws IOException {

    GroundAtoms groundAtoms = signature.createGroundAtoms();
    groundAtoms.load("" +
            ">token\n" +
            "0 the DT\n" +
            "1 man NN\n" +
            "2 \"likes\" VBZ\n" +
            "3 the DT\n" +
            "4 boat NN\n");

    builder.var("Int","b").var("Int","e").var("Label","l").quantify();
    builder.var("b").dontCare().dontCare().atom(token).var("e").dontCare().dontCare().atom(token).and(2).condition();
    builder.var("b").var("e").var("l").atom(phrase).formula();
    builder.term(1.0).weight();
    FactorFormula directScoreFormula = builder.produceFactorFormula();
    model.addFactorFormula(directScoreFormula);

    LocalFeatures features = new LocalFeatures(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    extractor.extract(groundAtoms, features);
    System.out.println(features.getRelation(phrase).value().size());
    Scores scores = new Scores(model, weights);
    scores.score(features, groundAtoms);
    System.out.println(scores);
    RelationVariable var = scores.getScoreRelation(phrase);
    assertEquals(100, var.value().size());
    assertEquals(1.0, scores.getScore(phrase, 0, 1, "NP"));
    assertEquals(1.0, scores.getScore(phrase, 2, 4, "VP"));


  }



  public void testGreedySolve() {
    LocalFeatures features = new LocalFeatures(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    extractor.extract(theManLikesTheBoat, features);
    Scores scores = new Scores(model, weights);
    scores.score(features, theManLikesTheBoat);
    GroundAtoms solution = scores.greedySolve(0.0);
    GroundAtomCollection atomCollection = solution.getGroundAtomsOf(phrase);
    System.out.println(atomCollection.getRelationVariable().value());
    assertTrue(atomCollection.containsAtom(0, 0, "NP"));
    assertTrue(atomCollection.containsAtom(3, 4, "NP"));

  }

  public void testQueryGeneratorGlobalPositive() {
    System.out.println(np_vp_s);
    QueryGenerator generator = new QueryGenerator();
    RelationExpression queryRule = generator.generateGlobalFalseQuery(np_vp_s, theManLikesTheBoat, weights);
    System.out.println(queryRule);
    GroundAtomCollection phrases = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");
    RelationVariable rel = interpreter.createRelationVariable(queryRule);
    assertEquals(1, rel.value().size());
    System.out.println(rel.value());
  }

  public void testSparseVectorAdd() {
    SparseVector arg1 = new SparseVector();
    arg1.addValue(0, 10.0);
    arg1.addValue(1, 1.0);
    SparseVector arg2 = new SparseVector();
    arg2.addValue(1, 2.0);
    arg2.addValue(2, 3.0);

    SparseVector result = arg1.add(2.0, arg2);

    System.out.println(result.getValuesRelation().value());
    assertTrue(result.getValuesRelation().contains(0, 10.0));
    assertTrue(result.getValuesRelation().contains(1, 5.0));
    assertTrue(result.getValuesRelation().contains(2, 6.0));

  }

  public void testSparseVectorAddInPlace() {
    SparseVector arg1 = new SparseVector();
    arg1.addValue(0, 10.0);
    arg1.addValue(1, 1.0);
    SparseVector result = new SparseVector();
    result.addValue(1, 2.0);
    result.addValue(2, 3.0);
    result.addInPlace(2.0, arg1);


    System.out.println(result.getValuesRelation().value());
    assertTrue(result.getValuesRelation().contains(0, 20.0));
    assertTrue(result.getValuesRelation().contains(1, 4.0));
    assertTrue(result.getValuesRelation().contains(2, 3.0));

  }

  public void testGroundFormulaExtractor() {
    GroundAtomCollection phrases = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);
    System.out.println(formulas.getNewGroundFormulas(np_vp_s).value());

    RelationVariable falseFormulas = formulas.getFalseGroundFormulas(np_vp_s);
    assertEquals(1, falseFormulas.value().size());
    assertTrue(falseFormulas.contains(4, 0, 1, 4, "NP", "VP", "S"));
    //assertTrue(falseFormulas.contains(0, 4, 4, "NP", 1, "S", "VP"));
    formulas.update(theManLikesTheBoat);
    System.out.println(formulas.getNewGroundFormulas(np_vp_s).value());


  }


  public void testQueryGeneratorThe() {

    System.out.println(factorThe);

    QueryGenerator generator = new QueryGenerator();
    RelationExpression queryThe = generator.generateLocalQuery(factorThe, theManLikesTheBoat, weights);

    System.out.println(queryThe);

    RelationVariable rel = interpreter.createRelationVariable(queryThe);

    ExpressionBuilder exprBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

    System.out.println(rel.value());
    assertEquals(12, rel.value().size());
    assertTrue(interpreter.evaluateBool(exprBuilder.expr(rel).
            id("arg_0").integer(0).id("arg_1").integer(0).id("arg_2").categorical((CategoricalType) label.getNodType(), "NP").
            id("index").integer(0).
            tuple(4).contains().getBool()).getBool());
    assertFalse(interpreter.evaluateBool(exprBuilder.expr(rel).
            id("arg_0").integer(1).id("arg_1").integer(0).id("arg_2").categorical((CategoricalType) label.getNodType(), "NP").
            id("index").integer(0).
            tuple(4).contains().getBool()).getBool());
    assertTrue(interpreter.evaluateBool(exprBuilder.expr(rel).
            id("arg_0").integer(3).id("arg_1").integer(4).id("arg_2").categorical((CategoricalType) label.getNodType(), "NP").
            id("index").integer(0).id("score").
            tuple(4).contains().getBool()).getBool());


    RelationExpression queryDT = generator.generateLocalQuery(factorDT, theManLikesTheBoat, weights);

    System.out.println(queryDT);
  }

  public void testQueryGeneratorSoftDisjunctionConstraint() {
    ILPGrounder generator = new ILPGrounder();
    ExpressionBuilder exprBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());
    Operator<RelationType> constraint =
            generator.generateSoftConstraintOperator(true, new boolean[]{true, false, true});
    exprBuilder.num(0).num(1).num(2).num(3).invokeRelOp(constraint);
    RelationVariable rel = interpreter.createRelationVariable(exprBuilder.getRelation());
    System.out.println(rel.value());
    assertEquals(4, rel.value().size());
  }

  public void testQueryGeneratorConstraintQuery() {
    GroundAtomCollection phrases = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);

    System.out.println(scores.getScoreRelation(phrase).value());

    ILPGrounder generator = new ILPGrounder(weights, theManLikesTheBoat);
    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, null);
    RelationExpression result = generator.generateConstraintQuery(np_vp_s, formulas, false, scores, ilp, model);
    System.out.println(result);

    RelationVariable constraints = interpreter.createRelationVariable(result);

    System.out.println(constraints.value());
    assertEquals(4, constraints.value().size());

    System.out.println(ilp.getGroundAtomIndices(phrase).value());
    assertEquals(3, ilp.getGroundAtomIndices(phrase).value().size());
    System.out.println(ilp.getGroundFormulaIndices(np_vp_s).value());
    assertEquals(1, ilp.getGroundFormulaIndices(np_vp_s).value().size());
    System.out.println(scores.getScoreRelation(phrase).value());
  }

  public void testILPBuild() {
    GroundAtomCollection phrases = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);

    System.out.println(formulas.getNewGroundFormulas(np_vp_s).value());

    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());
    ilp.build(formulas, theManLikesTheBoat, scores);

    System.out.println(ilp.getNewConstraints().value());
    System.out.println(ilp.getNewVars().value());
    assertEquals(4, ilp.getNewConstraints().value().size());
    assertEquals(4, ilp.getConstraints().value().size());
    assertEquals(4, ilp.getNewVars().value().size());
    assertEquals(4, ilp.getVars().value().size());
    assertTrue(ilp.changed());

  }

  public void testEvaluation() {
    GroundAtoms gold = signature.createGroundAtoms();
    GroundAtoms guess = signature.createGroundAtoms();

    gold.getGroundAtomsOf(phrase).addGroundAtom(0, 1, "NP");
    gold.getGroundAtomsOf(phrase).addGroundAtom(2, 4, "VP");
    gold.getGroundAtomsOf(phrase).addGroundAtom(0, 4, "S");

    guess.getGroundAtomsOf(phrase).addGroundAtom(0, 1, "NP");
    guess.getGroundAtomsOf(phrase).addGroundAtom(2, 4, "VP");
    guess.getGroundAtomsOf(phrase).addGroundAtom(0, 4, "NP");

    Evaluation evaluation = new Evaluation(model);
    evaluation.evaluate(gold, guess);

    System.out.println(evaluation.getRecall(phrase));
    System.out.println(evaluation.getPrecision(phrase));
    assertEquals(2.0 / 3.0, evaluation.getRecall(phrase));
    assertEquals(2.0 / 3.0, evaluation.getPrecision(phrase));
    assertEquals(2.0 / 3.0, evaluation.getF1(phrase));

    System.out.println(evaluation);
  }


  public void testILPSolvers() {
    GroundAtomCollection phrases = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);

    Scores scores = new Scores(model, weights);
    scores.score(features, theManLikesTheBoat);
    scores.addScore(phrase, 0.5, 2, 4, "VP");

    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());
    ilp.build(formulas, theManLikesTheBoat, scores);

    ILPSolver solver = new ILPSolverLpSolve();
    solver.init();
    solver.add(ilp.getNewVars(), ilp.getNewConstraints());
    RelationVariable result = solver.solve();
    System.out.println(result.value());
    assertTrue(result.contains(0, 1.0));
    assertTrue(result.contains(1, 1.0));
    assertTrue(result.contains(2, 1.0));
    assertTrue(result.contains(3, 1.0));
    assertFalse(result.contains(4, 1.0));
  }

  public void testIntegerLinearProgram() {
    GroundAtomCollection phrases = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(2, 4, "VP");

    //scores.addScore(phrase, 0.5, 2, 4, "VP");

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);

    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());
    ilp.build(formulas, theManLikesTheBoat, scores);

    System.out.println(ilp);
    System.out.println(ilp.indexToVariableString(0));
    System.out.println(ilp.indexToVariableString(1));
    System.out.println(ilp.indexToPredicateString(1));
    System.out.println(ilp.indexToPredicateString(0));
    System.out.println(ilp.getVariableIndex(phrase, 0, 1, "NP"));
    System.out.println(ilp.toLpSolveFormat());
    System.out.println(ilp.allConstraintsFor(phrase, 0, 1, "NP"));

    GroundAtoms solution = signature.createGroundAtoms();
    //these should be removed (the solver gives them a zero value
    solution.getGroundAtomsOf(phrase).addGroundAtom(2, 4, "VP");
    solution.getGroundAtomsOf(phrase).addGroundAtom(0, 4, "S");
    //this should remain in because it is not part of the ilp (it doesn't violate anything)
    solution.getGroundAtomsOf(phrase).addGroundAtom(0, 3, "S");

    ilp.solve(solution);

    RelationVariable tree = solution.getGroundAtomsOf(phrase).getRelationVariable();
    System.out.println(tree.value());
    assertTrue(tree.contains(0, 1, "NP"));
    assertTrue(tree.contains(0, 3, "S"));
    assertFalse(tree.contains(2, 4, "VP"));
    assertFalse(tree.contains(0, 4, "S"));


  }


  public void testQueryGeneratorDt() {

    System.out.println(factorDT);

    QueryGenerator generator = new QueryGenerator();
    RelationExpression queryDT = generator.generateLocalQuery(factorDT, theManLikesTheBoat, weights);

    System.out.println(queryDT);

    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    RelationVariable rel = interpreter.createRelationVariable(queryDT);

    //ExpressionBuilder exprBuilder = new ExpressionBuilder(TheBeast.getInstance().getNodServer());

    assertEquals(14, rel.value().size());
    assertTrue(rel.contains(0, 0, "NP", 2));
    assertTrue(rel.contains(0, 0, "VP", 3));
    assertFalse(rel.contains(1, 0, "NP", 0));
    System.out.println(rel.value());
  }

  public void testSolution() {
    Solution solution = new Solution(model, weights);
    GroundAtomCollection tokens = solution.getGroundAtoms().getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");

    GroundAtomCollection phrases = solution.getGroundAtoms().getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(3, 4, "NP");
    phrases.addGroundAtom(2, 4, "VP");

    solution.updateGroundFormulas();

    FeatureVector vector = solution.extract();
    SparseVector features = vector.getLocal();
    System.out.println(features.getValuesRelation().value());
    assertTrue(features.contains(0, 2.0));
    assertTrue(features.contains(2, 2.0));
    assertTrue(vector.getFalseVector().contains(4, -1.0));
    assertEquals(2, features.size());

    LocalFeatures local = new LocalFeatures(model, weights);
    LocalFeatureExtractor extractor = new LocalFeatureExtractor(model, weights);
    extractor.extract(theManLikesTheBoat, local);

    //local.extract(theManLikesTheBoat);

    FeatureVector extracted = solution.extract(local);
    features = extracted.getLocal();
    System.out.println(features.getValuesRelation().value());
    assertTrue(features.contains(0, 2.0));
    assertTrue(features.contains(2, 2.0));
    assertTrue(extracted.getFalseVector().contains(4, -1.0));
    assertEquals(2, features.size());

  }

  public void testLEQConstraint() {
    FormulaBuilder builder = new FormulaBuilder(signature);

    //a vbz can not start more than one VP
    //for int i if token(i,_,VBZ) : |int j : phrase(i,j,VP)| <= 1;
    builder.var(Type.INT, "i").quantify();
    builder.var("i").dontCare().term("VBZ").atom(token).condition();
    builder.var(Type.INT, "j").quantify();
    builder.var("i").var("j").term("VP").atom(phrase).var("j").dontCare().dontCare().atom(token).and(2).cardinality();
    builder.term(1).upperBound().cardinalityConstraint(false).formula();
    builder.term(Double.POSITIVE_INFINITY).weight();

    FactorFormula factor = builder.produceFactorFormula();

    GroundAtomCollection phraseAtoms = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phraseAtoms.addGroundAtom(2, 3, "VP");
    phraseAtoms.addGroundAtom(2, 4, "VP");

    System.out.println(factor);

    QueryGenerator generator = new QueryGenerator(weights, theManLikesTheBoat);
    RelationExpression query = generator.generateGlobalFalseQuery(factor, theManLikesTheBoat, weights);
    System.out.println(query);

    Model model = signature.createModel();
    model.addFactorFormula(factor);
    model.addHiddenPredicate(phrase);
    model.addObservedPredicate(token);

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);
    System.out.println(formulas);
    assertTrue(formulas.getFalseGroundFormulas(factor).contains(2));
    assertEquals(1, formulas.getFalseGroundFormulas(factor).value().size());

    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());

    ilp.build(formulas, theManLikesTheBoat, scores);

    assertEquals(1, ilp.getConstraints().value().size());
    assertTrue(ilp.getConstraints().contains(Double.NEGATIVE_INFINITY, 1.0,
            new Object[]{new Object[]{0, 1.0}, new Object[]{1, 1.0}}));

    assertEquals(2, ilp.getVars().value().size());

    System.out.println(ilp.getConstraints().value());

    ILPGrounder grounder = new ILPGrounder(weights, theManLikesTheBoat);

    RelationExpression constraintQuery = grounder.generateConstraintQuery(factor, formulas, false, scores, ilp, model);

    System.out.println(constraintQuery);

  }

  public void testCycleConstraint() {
    FormulaBuilder builder = new FormulaBuilder(signature);

    //a vbz has to have at least one VP starting at its position
    //for int i if token(i,_,VBZ) : |int j : token(j,_,_) & phrase(i,j,VP)| >= 1;
    builder.aclicity(phrase).formula();
    FactorFormula factor = builder.produceFactorFormula();

    GroundAtomCollection phraseAtoms = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phraseAtoms.addGroundAtom(0, 1, "VP");
    phraseAtoms.addGroundAtom(1, 0, "VP");
    phraseAtoms.addGroundAtom(2, 2, "VP");

    System.out.println(factor);

    Model model = signature.createModel();
    model.addFactorFormula(factor);
    model.addHiddenPredicate(phrase);

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);
    System.out.println(formulas);

    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());

    ilp.build(formulas, theManLikesTheBoat, scores);

    System.out.println(ilp.toLpSolveFormat());
    System.out.println(ilp);

    assertEquals(2, ilp.getConstraints().value().size());
    assertTrue(ilp.getConstraints().contains(Double.NEGATIVE_INFINITY, 1.0, new Object[]{
            new Object[]{0, 1.0},
            new Object[]{1, 1.0}}));
    assertTrue(ilp.getConstraints().contains(Double.NEGATIVE_INFINITY, 0.0, new Object[]{
            new Object[]{2, 1.0}}));

    //phraseAtoms.clear();
    phraseAtoms.addGroundAtom(3, 4, "VP");
    phraseAtoms.addGroundAtom(4, 3, "VP");

    formulas.update(theManLikesTheBoat);
    System.out.println(formulas);
    //ilp.init(scores);
    ilp.update(formulas, theManLikesTheBoat);
    System.out.println(ilp.toLpSolveFormat());

  }


  public void testGEQConstraint() {
    FormulaBuilder builder = new FormulaBuilder(signature);

    //a vbz has to have at least one VP starting at its position
    //for int i if token(i,_,VBZ) : |int j : token(j,_,_) & phrase(i,j,VP)| >= 1;
    builder.var(Type.INT, "i").quantify();
    builder.var("i").dontCare().term("VBZ").atom(token).condition();
    builder.var(Type.INT, "j").quantify();
    builder.var("i").var("j").term("VP").atom(phrase).var("j").dontCare().dontCare().atom(token).and(2).cardinality();
    builder.term(1).lowerBound().cardinalityConstraint(false).formula();
    builder.term(Double.POSITIVE_INFINITY).weight();

    FactorFormula factor = builder.produceFactorFormula();

    GroundAtomCollection phraseAtoms = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phraseAtoms.addGroundAtom(3, 3, "VP");
    phraseAtoms.addGroundAtom(3, 4, "VP");

    System.out.println(factor);

    QueryGenerator generator = new QueryGenerator(weights, theManLikesTheBoat);
    RelationExpression query = generator.generateGlobalFalseQuery(factor, theManLikesTheBoat, weights);
    System.out.println(query);

    Model model = signature.createModel();
    model.addFactorFormula(factor);
    model.addHiddenPredicate(phrase);
    model.addObservedPredicate(token);

    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(theManLikesTheBoat);
    System.out.println(formulas);
    assertEquals(1, formulas.getFalseGroundFormulas(factor).value().size());
    assertTrue(formulas.getFalseGroundFormulas(factor).contains(2));

    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());

    ilp.init(scores);
    ilp.update(formulas, theManLikesTheBoat);

    assertEquals(1, ilp.getConstraints().value().size());
    System.out.println(ilp.getConstraints().value());


    assertTrue(ilp.getConstraints().contains(1.0, Double.POSITIVE_INFINITY, new Object[]{
            new Object[]{0, 1.0},
            new Object[]{1, 1.0},
            new Object[]{2, 1.0},
            new Object[]{3, 1.0},
            new Object[]{4, 1.0}}));

    assertEquals(5, ilp.getVars().value().size());


    ILPGrounder grounder = new ILPGrounder(weights, theManLikesTheBoat);

    RelationExpression constraintQuery = grounder.generateConstraintQuery(factor, formulas, false, scores, ilp, model);

    System.out.println(constraintQuery);

  }


  public void testDeterministicConstraint() {
    FormulaBuilder builder = new FormulaBuilder(signature);

    builder.var(Type.INT, "b1").var(Type.INT, "e1").var(label, "l1");
    builder.var(Type.INT, "b2").var(Type.INT, "e2").var(label, "l2");
    builder.quantify();
    builder.var("b1").var("b2").intLEQ().var("b2").var("e1").intLEQ();
    builder.var("b1").var("b2").inequality().var("e1").var("e2").inequality().or(2).and(3).condition();
    builder.var("b1").var("e1").var("l1").atom("phrase");
    builder.var("b2").var("e2").var("l2").atom("phrase");
    builder.and(2).formula();
    builder.term(Double.NEGATIVE_INFINITY).weight();

    FactorFormula formula = builder.produceFactorFormula();
    System.out.println(formula);

    GroundAtomCollection phraseAtoms = theManLikesTheBoat.getGroundAtomsOf(phrase);
    phraseAtoms.addGroundAtom(0, 2, "NP");
    phraseAtoms.addGroundAtom(2, 4, "VP");

    Model model = signature.createModel();
    Weights weights = signature.createWeights();
    model.addFactorFormula(formula);
    model.addHiddenPredicate(phrase);

    GroundFormulas groundFormulas = new GroundFormulas(model, weights);
    groundFormulas.update(theManLikesTheBoat);

    System.out.println(groundFormulas);
    RelationVariable trueFormulas = groundFormulas.getTrueGroundFormulas(formula);
    assertEquals(1, trueFormulas.value().size());
    assertTrue(trueFormulas.contains(0, 2, "NP", 2, 4, "VP"));

    //now lets get some constraints out of this
    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());
    Scores scores = new Scores(model, weights);
    scores.addScore(phrase, 2.0, 0, 2, "NP");
    scores.addScore(phrase, 3.0, 2, 4, "VP");
    System.out.println(scores.getScoreRelation(phrase).value());
    ilp.build(groundFormulas, theManLikesTheBoat, scores);

    RelationVariable constraints = ilp.getConstraints();
    RelationVariable vars = ilp.getVars();
    System.out.println(constraints.value());
    System.out.println(vars.value());

    assertEquals(1, constraints.value().size());
    assertEquals(2, vars.value().size());

    assertTrue(constraints.contains(-1.0, Double.POSITIVE_INFINITY,
            new Object[]{new Object[]{0, -1.0}, new Object[]{1, -1.0}}));

    GroundAtoms solution = signature.createGroundAtoms();
    ilp.solve(solution);

    System.out.println(solution);

    assertEquals(1, solution.getGroundAtomsOf(phrase).size());
    assertTrue(solution.getGroundAtomsOf(phrase).containsAtom(2, 4, "VP"));

    //now the CuttingPlaneSolver
    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);
    solver.setObservation(theManLikesTheBoat);
    solver.setScores(scores);
    solver.setInititalSolution(theManLikesTheBoat);
    solver.solve(2);
    //solver.solve(theManLikesTheBoat,1);
    System.out.println(solver.getBestAtoms());
    //Solution result = new Solution(model, weights);
    //solver.solve(theManLikesTheBoat, scores, result);

    assertEquals(1, solver.getBestAtoms().getGroundAtomsOf(phrase).size());
    assertTrue(solver.getBestAtoms().getGroundAtomsOf(phrase).containsAtom(2, 4, "VP"));

    //System.out.println(result.getGroundAtoms());

  }

  public void testFeatureCollector() {
    GroundAtoms instance = signature.createGroundAtoms();
    GroundAtomCollection tokens = instance.getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");

    GroundAtomCollection phrases = instance.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(3, 4, "NP");
    phrases.addGroundAtom(2, 4, "VP");
    phrases.addGroundAtom(0, 4, "S");

    RandomAccessCorpus corpus = new RandomAccessCorpus(signature, 1);
    corpus.add(instance);

    Weights weights = signature.createWeights();
    FeatureCollector collector = new FeatureCollector(model, weights);
    collector.collect(corpus);
    weights.save(System.out);
    System.out.println(weights.getRelation(weightFunction1).value());
    System.out.println(weights.getRelation(weightFunction2).value());
    assertEquals(6, weights.getFeatureCount());
    assertEquals(0.0, weights.getWeight(weightFunction1, "DT", "NP"));
    assertEquals(0.0, weights.getWeight(weightFunction2, "NP"));
    assertTrue(weights.getIndex(weightFunction3, "NP", "VP", "S") != -1);
  }

  public void testOnlineLearner() throws IOException {
    GroundAtoms instance = signature.createGroundAtoms();
    GroundAtomCollection tokens = instance.getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");

    GroundAtomCollection phrases = instance.getGroundAtomsOf(phrase);
    phrases.addGroundAtom(0, 1, "NP");
    phrases.addGroundAtom(3, 4, "NP");
    phrases.addGroundAtom(2, 4, "VP");
    phrases.addGroundAtom(0, 4, "S");

    RandomAccessCorpus corpus = new RandomAccessCorpus(signature, 1);
    corpus.add(instance);

    Weights weights = signature.createWeights();
    weights.addWeight(weightFunction1, 0.0, "DT", "NP");
    weights.addWeight(weightFunction1, 0.0, "VBZ", "VP");
    weights.addWeight(weightFunction2, 0.0, "NP");
    weights.addWeight(weightFunction2, 0.0, "VP");
    weights.addWeight(weightFunction3, 0.0, "NP", "VP", "S");

    OnlineLearner learner = new OnlineLearner(model, weights);
    learner.setNumEpochs(1);
    IntegerLinearProgram ilp = new IntegerLinearProgram(new ILPSolverLpSolve());
    CuttingPlaneSolver cpSolver = new CuttingPlaneSolver(ilp);
    learner.setSolver(cpSolver);
    learner.setUpdateRule(new PerceptronUpdateRule());
    learner.setMaxCandidates(1);
    learner.setUseGreedy(true);
    File file = new File(toString());
    file.delete();
    TrainingInstances instances = new TrainingInstances(file, new LocalFeatureExtractor(model, weights), corpus,
            1000000, new QuietProgressReporter());
    learner.learn(instances);

    //learner.learn(corpus);
    weights.save(System.out);
    assertEquals(2.0, weights.getWeight(weightFunction1, "DT", "NP"));
    assertEquals(1.0, weights.getWeight(weightFunction1, "VBZ", "VP"));
    assertEquals(2.0, weights.getWeight(weightFunction2, "NP"));
    assertEquals(0.0, weights.getWeight(weightFunction2, "VP"));
    assertEquals(0.0, weights.getWeight(weightFunction3, "NP", "VP", "S"));
    System.out.println(weights.getWeights().value());

    //lets use the weights for inference on the original sentence
    CuttingPlaneSolver solver = new CuttingPlaneSolver();
    solver.configure(model, weights);
    solver.setObservation(instance);
    solver.solve(1);
    System.out.println(solver.getBestAtoms());
    System.out.println(solver.getBestFormulas());

    //now use the new weights to train on

    learner.setProfiler(new TreeProfiler());
    learner.learn(instances);
    System.out.println(learner.getProfiler());
    //learner.learn(corpus);
    System.out.println(weights.getWeights().value());

    assertEquals(-3.0, weights.getWeight(weightFunction1, "DT", "NP"));
    assertEquals(-1.0, weights.getWeight(weightFunction1, "VBZ", "VP"));
    assertEquals(-3.0, weights.getWeight(weightFunction2, "NP"));
    assertEquals(0.0, weights.getWeight(weightFunction2, "VP"));
    //unfortunate: the solver turns the S phrases on (even while having a zero score). Now it does!  
    assertEquals(3.0, weights.getWeight(weightFunction3, "NP", "VP", "S"));

    file.delete();
  }

  public void testAcyclicity() {
    UserPredicate link = signature.createPredicate("link", Type.INT, Type.INT);
    GroundAtoms sentence = signature.createGroundAtoms();
    GroundAtomCollection tokens = sentence.getGroundAtomsOf(token);
    tokens.addGroundAtom(0, "the", "DT");
    tokens.addGroundAtom(1, "man", "NN");
    tokens.addGroundAtom(2, "likes", "VBZ");
    tokens.addGroundAtom(3, "the", "DT");
    tokens.addGroundAtom(4, "boat", "NN");

    GroundAtomCollection links = sentence.getGroundAtomsOf(link);
    links.addGroundAtom(0, 1);
    links.addGroundAtom(1, 0);
    links.addGroundAtom(2, 3);
    links.addGroundAtom(3, 4);
    links.addGroundAtom(4, 2);

    FactorFormula noCycles = builder.aclicity(link).formula().produceFactorFormula();

    Model model = signature.createModel();
    model.addFactorFormula(noCycles);
    model.addHiddenPredicate(link);

    Weights weights = signature.createWeights();


    GroundFormulas formulas = new GroundFormulas(model, weights);
    formulas.update(sentence);
    System.out.println(formulas);

    RelationVariable cycles = formulas.getCycles(link);
    assertEquals(2, cycles.value().size());
    //assertTrue(cycles.contains(new Object[]{new Object[]{0,1}, new Object[]{new Object[]{1,0}}}));

    IntegerLinearProgram ilp = new IntegerLinearProgram(model, weights, new ILPSolverLpSolve());

    Scores scores = new Scores(model, weights);
    scores.addScore(link, 5.0, 1, 0);
    scores.addScore(link, 5.0, 1, 0);

    ilp.build(formulas, sentence, scores);

    System.out.println(ilp.getConstraints().value());
    assertTrue(ilp.getConstraints().contains(Double.NEGATIVE_INFINITY, 1.0,
            new Object[]{new Object[]{0, 1.0}, new Object[]{1, 1.0}}));
    assertTrue(ilp.getConstraints().contains(Double.NEGATIVE_INFINITY, 2.0,
            new Object[]{new Object[]{2, 1.0}, new Object[]{3, 1.0}, new Object[]{4, 1.0}}));

    ilp.solve(sentence);
    System.out.println(sentence);

    assertEquals(1, sentence.getGroundAtomsOf(link).size());
    assertTrue(sentence.getGroundAtomsOf(link).containsAtom(1, 0));
  }


}
