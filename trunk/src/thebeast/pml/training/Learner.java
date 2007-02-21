package thebeast.pml.training;

import thebeast.pml.Model;
import thebeast.pml.corpora.Corpus;
import thebeast.pml.Weights;

/**
 * @author Sebastian Riedel
 */
public interface Learner {
  /**
   * Update the weights given the model and corpus.
   *
   * @param corpus  the corpus to train on.
   */
  void learn(Corpus corpus);

  /**
   * Update weights and generate training instances for later reuse.
   *
   * @param corpus    the corpus to train on.
   * @param instances the container for the training instances this method produces. This can be reused later in {@link
 *                  thebeast.pml.training.Learner#learn(thebeast.pml.Model,thebeast.pml.Weights,thebeast.pml.training.TrainingInstances)}.
   */
  void learn(Corpus corpus, TrainingInstances instances);

  /**
   * Update the weights given the model and a some training instances.
   *
   * @param instances  the training instances
   */
  void learn(TrainingInstances instances);
}
