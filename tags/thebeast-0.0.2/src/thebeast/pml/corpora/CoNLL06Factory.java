package thebeast.pml.corpora;

import thebeast.pml.Signature;
import thebeast.pml.UserPredicate;

import java.io.File;

/**
 * @author Sebastian Riedel
 */
public class CoNLL06Factory implements CorpusFactory {


  public static final TabFormatCorpus.Generator
          GENERATOR = new TabFormatCorpus.Generator();


  static {
    GENERATOR.addTokenCollector(1, "Word", true, new Quote(), "ROOT");
    GENERATOR.addTokenCollector(1, "Prefix", true, new Pipeline(new Prefix(5), new Quote()), "ROOT");
    GENERATOR.addTokenCollector(3, "Cpos", true, new Quote(), "ROOT");
    GENERATOR.addTokenCollector(4, "Pos", true, new Quote(), "ROOT");
    GENERATOR.addTokenCollector(7, "Dep", true, new Itself());
  }

  public Corpus createCorpus(Signature signature, File file) {
    UserPredicate word = (UserPredicate) signature.getPredicate("word");
    UserPredicate pos = (UserPredicate) signature.getPredicate("pos");
    UserPredicate link = (UserPredicate) signature.getPredicate("link");
    UserPredicate dep = (UserPredicate) signature.getPredicate("dep");
    UserPredicate cpos = (UserPredicate) signature.getPredicate("cpos");
    UserPredicate prefix = (UserPredicate) signature.getPredicate("prefix");

    AttributeExtractor words = new AttributeExtractor(word, 2);
    words.addMapping(0, 0);
    words.addMapping(1, 1, new Quote());
    //words.addToQuote(1);

    AttributeExtractor prefixes = new AttributeExtractor(prefix, 2);
    prefixes.addMapping(0, 0);
    prefixes.addMapping(1, 1, new Pipeline(new Prefix(5), new Quote()));

    AttributeExtractor cpostags = new AttributeExtractor(cpos, 2);
    cpostags.addMapping(0, 0);
    cpostags.addMapping(3, 1);

    AttributeExtractor postags = new AttributeExtractor(pos, 2);
    postags.addMapping(0, 0);
    postags.addMapping(4, 1);

    AttributeExtractor links = new AttributeExtractor(link, 2);
    links.addMapping(0, 1);
    links.addMapping(6, 0);

    AttributeExtractor deps = new AttributeExtractor(dep, 3);
    deps.addMapping(0, 1);
    deps.addMapping(6, 0);
    deps.addMapping(7, 2);

    TabFormatCorpus corpus = new TabFormatCorpus(signature, file);
    corpus.addExtractor(prefixes);
    corpus.addExtractor(words);
    corpus.addExtractor(cpostags);
    corpus.addExtractor(postags);
    corpus.addExtractor(links);
    corpus.addExtractor(deps);

    corpus.addConstantAtom(prefix, 0, "ROOT");
    corpus.addConstantAtom(word, 0, "ROOT");
    corpus.addConstantAtom(pos, 0, "ROOT");
    corpus.addConstantAtom(cpos, 0, "ROOT");

    corpus.addWriter(word, new TokenFeatureWriter(0, 0, 0));
    corpus.addWriter(word, new TokenFeatureWriter(1, 0, 1, new Dequote()));
    corpus.addWriter(word, new ConstantWriter(2, 0, "_"));
    corpus.addWriter(cpos, new TokenFeatureWriter(3, 0, 1, new Dequote()));
    corpus.addWriter(pos, new TokenFeatureWriter(4, 0, 1, new Dequote()));
    corpus.addWriter(word, new ConstantWriter(5, 0, "_"));
    corpus.addWriter(link, new TokenFeatureWriter(6, 1, 0));
    corpus.addWriter(dep, new TokenFeatureWriter(7, 1, 2));
    corpus.addWriter(link, new TokenFeatureWriter(8, 1, 0));
    corpus.addWriter(dep, new TokenFeatureWriter(9, 1, 2));

    return corpus;
  }


}
