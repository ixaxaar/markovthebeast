package thebeast.pml.corpora;

/**
 * @author Sebastian Riedel
 */

public class Pipeline implements TokenProcessor {
  TokenProcessor[] processors;

  public Pipeline(TokenProcessor... processors) {
    this.processors = processors;
  }

  public String process(String token) {
    String result = token;
    for (TokenProcessor processor : processors)
      result = processor.process(result);
    return result;
  }
}
