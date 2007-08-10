package thebeast.pml;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 09-Aug-2007 Time: 22:23:58
 */
public interface CorpusEvaluationFunction {

  void start();

  void evaluate(GroundAtoms gold, GroundAtoms guess);

  double getResult();

}
