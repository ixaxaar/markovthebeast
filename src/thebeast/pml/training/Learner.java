package thebeast.pml.training;

import thebeast.pml.Model;
import thebeast.pml.corpora.Corpus;
import thebeast.pml.Weights;

/**
 * @author Sebastian Riedel
 */
public interface Learner {

  /**
   * Update the weights given the model and a some training instances.
   *
   * @param instances  the training instances
   */
  void learn(TrainingInstances instances);
}
