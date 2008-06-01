package thebeast.pml.corpora;

import thebeast.util.Counter;

/**
 * @author Sebastian Riedel
 */
public class Count implements TokenProcessor {

  private Counter counter;

  public Count(Counter counter) {
    this.counter = counter;
  }

  public Counter getCounter() {
    return counter;
  }

  public String process(String token) {
    return counter.get(token).toString();
  }
}
