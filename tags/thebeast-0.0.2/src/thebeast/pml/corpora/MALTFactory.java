package thebeast.pml.corpora;

import thebeast.pml.Signature;
import thebeast.pml.UserPredicate;

import java.io.File;

/**
 * @author Sebastian Riedel
 */
public class MALTFactory implements CorpusFactory {

  public static final TabFormatCorpus.Generator
          GENERATOR = new TabFormatCorpus.Generator();


   static {
     GENERATOR.addTokenCollector(0, "Word", true, new Quote(), "ROOT");
     GENERATOR.addTokenCollector(0, "Prefix", true, new Pipeline(new Prefix(5), new Quote()), "ROOT");
     GENERATOR.addTokenCollector(1, "Pos", true, new Quote(), "ROOT");
     GENERATOR.addTokenCollector(1, "Cpos", true, new Pipeline(new Prefix(2), new Quote()), "ROOT");
     GENERATOR.addTokenCollector(3, "Dep", true, new Itself());
   }



  public Corpus createCorpus(Signature signature, File file) {
    UserPredicate word = (UserPredicate) signature.getPredicate("word");
    UserPredicate pos = (UserPredicate) signature.getPredicate("pos");
    UserPredicate link = (UserPredicate) signature.getPredicate("link");
    UserPredicate dep = (UserPredicate) signature.getPredicate("dep");
    UserPredicate cpos = (UserPredicate) signature.getPredicate("cpos");
    UserPredicate prefix = (UserPredicate) signature.getPredicate("prefix");

    AttributeExtractor words = new AttributeExtractor(word, 2);
    words.addLineNrArg(0);
    words.addMapping(0, 1, new Quote());
    //words.addToQuote(1);

    AttributeExtractor prefixes = new AttributeExtractor(prefix, 2);
    prefixes.addLineNrArg(0);
    prefixes.addMapping(0, 1, new Pipeline(new Prefix(5), new Quote()));

    AttributeExtractor cpostags = new AttributeExtractor(cpos, 2);
    cpostags.addLineNrArg(0);
    cpostags.addMapping(1, 1, new Pipeline(new Prefix(2), new Quote()));

    AttributeExtractor postags = new AttributeExtractor(pos, 2);
    postags.addLineNrArg(0);
    postags.addMapping(1, 1, new Quote());

    AttributeExtractor links = new AttributeExtractor(link, 2);
    links.addLineNrArg(1);
    links.addMapping(2, 0);

    AttributeExtractor deps = new AttributeExtractor(dep, 3);
    deps.addLineNrArg(1);
    deps.addMapping(2, 0);
    deps.addMapping(3, 2);

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

    return corpus;
  }


}
