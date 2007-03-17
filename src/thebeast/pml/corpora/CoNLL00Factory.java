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
    GENERATOR.addTokenCollector(0, "Hyphen", true, new HasSubstring("-"));
    GENERATOR.addTokenCollector(0, "Prefix1", true, new Pipeline(new Prefix(1), new Quote()));
    GENERATOR.addTokenCollector(0, "Postfix1", true, new Pipeline(new Postfix(1), new Quote()));
    GENERATOR.addTokenCollector(0, "Prefix2", true, new Pipeline(new Prefix(2), new Quote()));
    GENERATOR.addTokenCollector(0, "Postfix2", true, new Pipeline(new Postfix(2), new Quote()));
    GENERATOR.addTokenCollector(0, "Prefix3", true, new Pipeline(new Prefix(3), new Quote()));
    GENERATOR.addTokenCollector(0, "Postfix3", true, new Pipeline(new Postfix(3), new Quote()));
    GENERATOR.addTokenCollector(0, "Prefix4", true, new Pipeline(new Prefix(4), new Quote()));
    GENERATOR.addTokenCollector(0, "Postfix4", true, new Pipeline(new Postfix(4), new Quote()));
    GENERATOR.addTokenCollector(0, "Case", true, new Case());
    GENERATOR.addTokenCollector(0, "Cardinal", true, new IsNumber());
    GENERATOR.addTokenCollector(1, "Pos", true, new Quote());
    GENERATOR.addTokenCollector(2, "Chunk", true, new BIOLabel());
  }

  public Corpus createCorpus(Signature signature, File file) {

    UserPredicate word = (UserPredicate) signature.getPredicate("word");
    UserPredicate pos = (UserPredicate) signature.getPredicate("pos");
    UserPredicate hyphen = (UserPredicate) signature.getPredicate("hyphen");
    UserPredicate prefix1 = (UserPredicate) signature.getPredicate("prefix1");
    UserPredicate postfix1 = (UserPredicate) signature.getPredicate("postfix1");
    UserPredicate prefix2 = (UserPredicate) signature.getPredicate("prefix2");
    UserPredicate postfix2 = (UserPredicate) signature.getPredicate("postfix2");
    UserPredicate prefix3 = (UserPredicate) signature.getPredicate("prefix3");
    UserPredicate postfix3 = (UserPredicate) signature.getPredicate("postfix3");
    UserPredicate prefix4 = (UserPredicate) signature.getPredicate("prefix4");
    UserPredicate postfix4 = (UserPredicate) signature.getPredicate("postfix4");
    UserPredicate cap = (UserPredicate) signature.getPredicate("case");
    UserPredicate isNumber = (UserPredicate) signature.getPredicate("cardinal");
    UserPredicate chunk = (UserPredicate) signature.getPredicate("chunk");
//    UserPredicate maybeNer = (UserPredicate) signature.getPredicate("maybeNer");

    AttributeExtractor words = new AttributeExtractor(word, 2);
    words.addLineNrArg(0);
    words.addMapping(0, 1, new Quote());

    AttributeExtractor hyphens = new AttributeExtractor(hyphen, 2);
    hyphens.addLineNrArg(0);
    hyphens.addMapping(0, 1, new HasSubstring("-"));

    AttributeExtractor prefixes1 = new AttributeExtractor(prefix1, 2);
    prefixes1.addLineNrArg(0);
    prefixes1.addMapping(0, 1, new Pipeline(new Prefix(1), new Quote()));

    AttributeExtractor postfixes1 = new AttributeExtractor(postfix1, 2);
    postfixes1.addLineNrArg(0);
    postfixes1.addMapping(0, 1, new Pipeline(new Postfix(1), new Quote()));

    AttributeExtractor prefixes2 = new AttributeExtractor(prefix2, 2);
    prefixes2.addLineNrArg(0);
    prefixes2.addMapping(0, 1, new Pipeline(new Prefix(2), new Quote()));

    AttributeExtractor postfixes2 = new AttributeExtractor(postfix2, 2);
    postfixes2.addLineNrArg(0);
    postfixes2.addMapping(0, 1, new Pipeline(new Postfix(2), new Quote()));

    AttributeExtractor prefixes3 = new AttributeExtractor(prefix3, 2);
    prefixes3.addLineNrArg(0);
    prefixes3.addMapping(0, 1, new Pipeline(new Prefix(3), new Quote()));

    AttributeExtractor postfixes3 = new AttributeExtractor(postfix3, 2);
    postfixes3.addLineNrArg(0);
    postfixes3.addMapping(0, 1, new Pipeline(new Postfix(3), new Quote()));

    AttributeExtractor prefixes4 = new AttributeExtractor(prefix4, 2);
    prefixes4.addLineNrArg(0);
    prefixes4.addMapping(0, 1, new Pipeline(new Prefix(4), new Quote()));

    AttributeExtractor postfixes4 = new AttributeExtractor(postfix4, 2);
    postfixes4.addLineNrArg(0);
    postfixes4.addMapping(0, 1, new Pipeline(new Postfix(4), new Quote()));


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

//    PhraseStatistics statistics = new PhraseStatistics(0, maybeNer,
//            new PhraseStatistics.LooksLikeNER(), new PhraseStatistics.AsString());

    TabFormatCorpus corpus = new TabFormatCorpus(signature, file);
    corpus.addExtractor(prefixes1);
    corpus.addExtractor(postfixes1);
    corpus.addExtractor(prefixes2);
    corpus.addExtractor(postfixes2);
    corpus.addExtractor(prefixes3);
    corpus.addExtractor(postfixes3);
    corpus.addExtractor(prefixes4);
    corpus.addExtractor(postfixes4);
    corpus.addExtractor(words);
    corpus.addExtractor(hyphens);
    corpus.addExtractor(postags);
    corpus.addExtractor(cases);
    corpus.addExtractor(numbers);
    corpus.addExtractor(chunks);
    //corpus.addExtractor(statistics);

    corpus.addWriter(word, new TokenFeatureWriter(0, 0, 0));
    corpus.addWriter(word, new TokenFeatureWriter(1, 0, 1, new Dequote()));
    corpus.addWriter(word, new ConstantWriter(2, 0, "_"));
    corpus.addWriter(pos, new TokenFeatureWriter(4, 0, 1, new Dequote()));
    corpus.addWriter(word, new ConstantWriter(5, 0, "_"));

    return corpus;
  }


}
