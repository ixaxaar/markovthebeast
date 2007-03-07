package thebeast.pml.corpora;

import thebeast.pml.Signature;
import thebeast.pml.UserPredicate;

import java.io.File;

/**
 * @author Sebastian Riedel
 */
public class CoNLL00Factory implements CorpusFactory {


  public static final TabFormatCorpus.Generator
          GENERATOR = new TabFormatCorpus.Generator();


  static {
    GENERATOR.addTokenCollector(0, "Word", true, new Quote());
    GENERATOR.addTokenCollector(0, "Prefix", true, new Pipeline(new Prefix(2), new Quote()));
    GENERATOR.addTokenCollector(0, "Postfix", true, new Pipeline(new Postfix(2), new Quote()));
    GENERATOR.addTokenCollector(0, "Case", true, new Case());
    GENERATOR.addTokenCollector(0, "Cardinal", true, new IsNumber());
    GENERATOR.addTokenCollector(1, "Pos", true, new Quote());
    GENERATOR.addTokenCollector(2, "Chunk", true, new BIOLabel());
  }

  public Corpus createCorpus(Signature signature, File file) {
    UserPredicate word = (UserPredicate) signature.getPredicate("word");
    UserPredicate pos = (UserPredicate) signature.getPredicate("pos");
    UserPredicate prefix = (UserPredicate) signature.getPredicate("prefix");
    UserPredicate postfix = (UserPredicate) signature.getPredicate("postfix");
    UserPredicate cap = (UserPredicate) signature.getPredicate("case");
    UserPredicate isNumber = (UserPredicate) signature.getPredicate("cardinal");
    UserPredicate chunk = (UserPredicate) signature.getPredicate("chunk");

    AttributeExtractor words = new AttributeExtractor(word, 2);
    words.addLineNrArg(0);
    words.addMapping(0, 1, new Quote());

    AttributeExtractor prefixes = new AttributeExtractor(prefix, 2);
    prefixes.addLineNrArg(0);
    prefixes.addMapping(0, 1, new Pipeline(new Prefix(2), new Quote()));

    AttributeExtractor postfixes = new AttributeExtractor(postfix, 2);
    postfixes.addLineNrArg(0);
    postfixes.addMapping(0, 1, new Pipeline(new Postfix(2), new Quote()));

    AttributeExtractor cases = new AttributeExtractor(cap, 2);
    cases.addLineNrArg(0);
    cases.addMapping(0, 1, new Case());

    AttributeExtractor numbers = new AttributeExtractor(isNumber, 2);
    numbers.addLineNrArg(0);
    numbers.addMapping(0, 1, new IsNumber());

    AttributeExtractor postags = new AttributeExtractor(pos, 2);
    postags.addLineNrArg(0);
    postags.addMapping(1, 1, new Quote());

    BIOExtractor chunks = new BIOExtractor(2, chunk);
    
    TabFormatCorpus corpus = new TabFormatCorpus(signature, file);
    corpus.addExtractor(prefixes);
    corpus.addExtractor(words);
    corpus.addExtractor(postags);
    corpus.addExtractor(postfixes);
    corpus.addExtractor(cases);
    corpus.addExtractor(numbers);
    corpus.addExtractor(chunks);

    corpus.addWriter(word, new TokenFeatureWriter(0, 0, 0));
    corpus.addWriter(word, new TokenFeatureWriter(1, 0, 1, new Dequote()));
    corpus.addWriter(word, new ConstantWriter(2, 0, "_"));
    corpus.addWriter(pos, new TokenFeatureWriter(4, 0, 1, new Dequote()));
    corpus.addWriter(word, new ConstantWriter(5, 0, "_"));

    return corpus;
  }


}
