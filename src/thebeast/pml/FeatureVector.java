package thebeast.pml;

import thebeast.nod.FileSink;
import thebeast.nod.FileSource;

import java.io.IOException;

/**
 * A feature vector contains three sparse vectors, one for features with free weights, one for features with
 * non-negative weights and one for features with non-positive weights.
 */
public class FeatureVector {

  private SparseVector free, nonnegative, nonpositive;

  public FeatureVector() {
    free = new SparseVector();
    nonnegative = new SparseVector();
    nonpositive = new SparseVector();
  }

  public SparseVector getFree() {
    return free;
  }

  public SparseVector getNonnegative() {
    return nonnegative;
  }

  public SparseVector getNonpositive() {
    return nonpositive;
  }

  public void write(FileSink sink) throws IOException {
    free.write(sink);
    nonnegative.write(sink);
    nonpositive.write(sink);
  }

  public void read(FileSource source) throws IOException {
    free.read(source);
    nonnegative.read(source);
    nonpositive.read(source);
  }

  public int getMemoryUsage(){
    return free.getMemoryUsage() + nonnegative.getMemoryUsage() + nonpositive.getMemoryUsage();
  }

  public void load(FeatureVector vector){
    free.load(vector.getFree());
    nonnegative.load(vector.getNonnegative());
    nonpositive.load(vector.getNonpositive());
  }

}
