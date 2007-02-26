package thebeast.pml.training;

import thebeast.pml.GroundAtoms;
import thebeast.pml.LocalFeatures;
import thebeast.pml.SparseVector;

import java.util.*;
import java.io.File;

/**
 * This object represents a list of training instances (gold feature vector, gold solution (+observation)
 * and local features). It can be configured to only contain a subset of all training instances
 * in memory and stream in and out instances when they are needed.
 */
public class TrainingInstances extends AbstractCollection<TrainingInstance> {

  private int maxByteSize = 100000;
  private int activeCount = 0;
  private int activeSize = 0;
  private File cacheFile;
  //
  private ArrayList<TrainingInstance> active = new ArrayList<TrainingInstance>(10000);

  public void add(GroundAtoms data, LocalFeatures features, SparseVector gold){
    TrainingInstance trainingInstance = new TrainingInstance(data, features, gold);
    activeSize += trainingInstance.getMemoryUsage();
    active.add(trainingInstance);
    if (activeSize > maxByteSize)
      streamOut();
  }

  private void streamOut() {
    for (TrainingInstance instance : active){
      //instance.getData().saveBinary(file)
    }
  }

  private void streamIn(){
    //
  }

  public Iterator<TrainingInstance> iterator() {
    return null;
  }

  public int size() {
    return 0;
  }
}
