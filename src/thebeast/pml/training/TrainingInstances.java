package thebeast.pml.training;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.variable.IntVariable;
import thebeast.pml.*;
import thebeast.pml.corpora.Corpus;
import thebeast.util.ProgressReporter;

import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * This object represents a list of training instances (gold feature vector, gold solution (+observation) and local
 * features). It can be configured to only contain a subset of all training instances in memory and stream in and out
 * instances when they are needed.
 */
public class TrainingInstances extends AbstractCollection<TrainingInstance> {

  private ArrayList<TrainingInstance> active;
  private int size;
  private int byteSize = 0;
  private FileSource fileSource;
  private int activeCount;
  private boolean iterating = false;
  private Model model;
  private Weights weights;
  private boolean verbose = false;
  private File file;
  private Signature signature;
  private boolean loadedFromFile;
  private boolean saveFeatures;
  private Stack<LocalFeatures> allFeatures = new Stack<LocalFeatures>();
  private Stack<LocalFeatures> usableFeatures = new Stack<LocalFeatures>();
  private Stack<FeatureVector> allVectors = new Stack<FeatureVector>();
  private Stack<FeatureVector> usableVectors = new Stack<FeatureVector>();


  public TrainingInstances(Model model, File file, int maxByteSize, boolean saveFeatures) {
    this.saveFeatures = saveFeatures;
    this.file = file;
    this.signature = model.getSignature();
    this.model = model;

    try {
      fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
      IntVariable sizeVar = TheBeast.getInstance().getNodServer().interpreter().createIntVariable();
      fileSource.read(sizeVar);
      size = sizeVar.value().getInt();
      byteSize = 0;
      active = new ArrayList<TrainingInstance>(10000);
      for (int i = 0; i < size && byteSize < maxByteSize; ++i) {
        TrainingInstance trainingInstance = new TrainingInstance(signature.createGroundAtoms(),
                saveFeatures ? new LocalFeatures(model, weights) : null, new FeatureVector());
        trainingInstance.read(fileSource);
        byteSize += trainingInstance.getMemoryUsage();
        active.add(trainingInstance);
      }
      activeCount = active.size();
      loadedFromFile = true;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public boolean isSaveFeatures() {
    return saveFeatures;
  }

  public void setSaveFeatures(boolean saveFeatures) {
    this.saveFeatures = saveFeatures;
  }

  public TrainingInstances() {
  }

  public TrainingInstances(File file, LocalFeatureExtractor extractor, boolean saveFeatures,
                           Corpus corpus, int maxByteSize, ProgressReporter reporter) throws IOException {
    this.saveFeatures = saveFeatures;
    Signature signature = extractor.getModel().getSignature();
    Solution solution = new Solution(extractor.getModel(), extractor.getWeights());
    FileSink fileSink = TheBeast.getInstance().getNodServer().createSink(file, 1024);
    active = new ArrayList<TrainingInstance>(10000);
    this.size = corpus.size();
    this.file = file;
    ExpressionBuilder builder = TheBeast.getInstance().getNodServer().expressionBuilder();
    builder.num(size);
    Interpreter interpreter = TheBeast.getInstance().getNodServer().interpreter();
    fileSink.write(interpreter.createIntVariable(builder.getInt()));
    int numDumps = 0;
    reporter.started();
    for (GroundAtoms atoms : corpus) {
      int memUsage = atoms.getMemoryUsage();
      if (byteSize + memUsage > maxByteSize) {
        activeCount += active.size();
        dump(fileSink);
        ++numDumps;
        byteSize = 0;
      }
      solution.load(atoms);
      model = extractor.getModel();
      weights = extractor.getWeights();
      LocalFeatures features;
      FeatureVector vector;
      if (usableFeatures.isEmpty()){
        features = new LocalFeatures(model, weights);
        vector = new FeatureVector();
        allFeatures.add(features);
        allVectors.add(vector);
      } else {
        features = usableFeatures.pop();
        vector = usableVectors.pop();
      }
      if (saveFeatures){
        extractor.extract(atoms, features);
        solution.extractInPlace(features,vector);
      } else {
        solution.load(atoms);
        solution.extractInPlace(vector);
      }

      //TrainingInstance instance = new TrainingInstance(atoms, features, solution.extract(features));
      TrainingInstance instance = new TrainingInstance(atoms, saveFeatures ? features : null, vector);
      active.add(instance);
      byteSize += instance.getMemoryUsage();
      //++size;
      reporter.progressed();
    }
    activeCount = numDumps == 0 ? size : activeCount / numDumps;
    dump(fileSink);
    for (int i = 0; i < activeCount; ++i) {
      active.add(new TrainingInstance(signature.createGroundAtoms(),
              saveFeatures ? new LocalFeatures(model, weights) : null, new FeatureVector()));
    }
    fileSink.flush();
    fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
    reporter.finished();
  }

  private void dump(FileSink fileSink) throws IOException {
    for (TrainingInstance instance : active) {
      instance.write(fileSink);
    }
    //fileSink.flush();
    //clear features and make them available for reuse
    for (LocalFeatures features : allFeatures){
      features.clear();
      usableFeatures.add(features);
    }
    for (FeatureVector vector: allVectors){
      vector.clear();
      usableVectors.add(vector);
    }
    if (verbose) System.out.print(">");
    active.clear();
  }

  public synchronized Iterator<TrainingInstance> iterator() {
    if (iterating)
      throw new RuntimeException("Dumped Corpus can only have one active iterator at a time!");
    iterating = true;
    try {
      if (!loadedFromFile) {
        fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
        fileSource.read(TheBeast.getInstance().getNodServer().interpreter().createIntVariable());
      }
      if (activeCount >= size) {
        if (!loadedFromFile) for (int i = 0; i < size; ++i) {
          TrainingInstance instance = active.get(i);
          instance.read(fileSource);
        }
        iterating = false;
        loadedFromFile = false;
        if (verbose) System.out.print("<");
        return active.subList(0, size).iterator();
      } else {
        if (!loadedFromFile) for (TrainingInstance instance : active) {
          instance.read(fileSource);
        }
        if (verbose) System.out.print("<");
        loadedFromFile = false;        
        return new Iterator<TrainingInstance>() {
          Iterator<TrainingInstance> delegate = active.iterator();
          int current = 0;

          public boolean hasNext() {
            return current < size;
          }

          public TrainingInstance next() {
            if (delegate.hasNext()) {
              ++current;
              if (!hasNext()) iterating = false;
              return delegate.next();
            }
            update();
            ++current;
            if (!hasNext()) iterating = false;

            return delegate.next();
          }

          public void update() {
            try {
              if (activeCount >= size - current) {
                for (int i = 0; i < size - current; ++i) {
                  TrainingInstance instance = active.get(i);
                  instance.read(fileSource);
                }
                delegate = active.subList(0, size - current).iterator();
              } else {
                for (TrainingInstance instance : active) {
                  instance.read(fileSource);
                }
                delegate = active.iterator();
              }
              if (verbose) System.out.print("<");
            } catch (IOException e) {
              e.printStackTrace();
            }

          }

          public void remove() {

          }
        };
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  public int size() {
    return size;
  }

  public int getUsedMemory() {
    int usage = 0;
    for (TrainingInstance instance : active) {
      usage += instance.getMemoryUsage();
    }
    return usage;
  }
}
