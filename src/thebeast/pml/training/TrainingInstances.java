package thebeast.pml.training;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;
import thebeast.pml.*;
import thebeast.util.ProgressReporter;

import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

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


  public TrainingInstances() {
  }

  public TrainingInstances(File file, LocalFeatureExtractor extractor,
                           Iterator<GroundAtoms> corpus, int maxByteSize, ProgressReporter reporter) throws IOException {
    Signature signature = extractor.getModel().getSignature();
    Solution solution = new Solution(extractor.getModel(), extractor.getWeights());
    FileSink fileSink = TheBeast.getInstance().getNodServer().createSink(file, 1024);
    active = new ArrayList<TrainingInstance>(10000);
    this.size = 0;
    int numDumps = 0;
    reporter.started();
    while (corpus.hasNext()) {
      GroundAtoms atoms = corpus.next();
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
      LocalFeatures features = new LocalFeatures(model, weights);
      extractor.extract(atoms, features);
      TrainingInstance instance = new TrainingInstance(atoms, features, solution.extract(features));
      active.add(instance);
      byteSize += instance.getMemoryUsage();
      ++size;
      reporter.progressed();
    }
    activeCount = numDumps == 0 ? size : activeCount / numDumps;
    dump(fileSink);
    for (int i = 0; i < activeCount; ++i) {
      active.add(new TrainingInstance(signature.createGroundAtoms(),
              new LocalFeatures(model, weights), new SparseVector()));
    }
    fileSink.flush();
    fileSource = TheBeast.getInstance().getNodServer().createSource(file, 1024);
    reporter.finished();
  }

  public void add(GroundAtoms data, LocalFeatures features, SparseVector gold) {
    TrainingInstance trainingInstance = new TrainingInstance(data, features, gold);
    //active.add(trainingInstance);
  }

  private void dump(FileSink fileSink) throws IOException {
    for (TrainingInstance instance : active) {
      instance.write(fileSink);
    }
    if (verbose) System.out.print(">");
    active.clear();
  }

  public synchronized Iterator<TrainingInstance> iterator() {
    if (iterating)
      throw new RuntimeException("Dumped Corpus can only have one active iterator at a time!");
    iterating = true;
    fileSource.reset();
    //fill up initial actives
    try {
      if (activeCount >= size) {
        for (int i = 0; i < size; ++i) {
          TrainingInstance instance = active.get(i);
          instance.read(fileSource);
        }
        iterating = false;
        if (verbose) System.out.print("<");
        return active.subList(0, size).iterator();
      } else {
        for (TrainingInstance instance : active) {
          instance.read(fileSource);
        }
        System.out.print("<");
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
