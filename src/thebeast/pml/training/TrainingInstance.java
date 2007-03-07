package thebeast.pml.training;

import thebeast.pml.*;
import thebeast.nod.FileSink;
import thebeast.nod.FileSource;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Feb-2007 Time: 12:28:40
 */
public class TrainingInstance implements Dumpable {

  private FeatureVector gold;
  private GroundAtoms data;
  private LocalFeatures features;

  public TrainingInstance(GroundAtoms data, LocalFeatures features, FeatureVector gold) {
    this.data = data;
    this.features = features;
    this.gold = gold;
  }

  public GroundAtoms getData() {
    return data;
  }

  public LocalFeatures getFeatures() {
    return features;
  }

  public FeatureVector getGold() {
    return gold;
  }

  public int getMemoryUsage(){
    return data.getMemoryUsage() + features.getMemoryUsage() + gold.getMemoryUsage();
  }

  public void write(FileSink fileSink) throws IOException {
    data.write(fileSink);
    features.write(fileSink);
    gold.write(fileSink);
  }

  public void read(FileSource fileSource) throws IOException {
    data.read(fileSource);
    features.read(fileSource);
    gold.read(fileSource);
  }
}
