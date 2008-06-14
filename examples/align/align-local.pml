// This formula penalizes or rewards the existence of an alignment between a token pair
weight w_bias: Double;
factor: for Int s, Int t
  if src_word(s,_) & tgt_word(t,_) add [align(s,t)] * w_bias;

// This formula checks whether source and target token a specific source word-target
// word combination
weight w_word: SourceWord x TargetWord -> Double;
factor: for Int s, Int t, SourceWord w_s, TargetWord w_t
  if src_word(s,w_s) & tgt_word(t,w_t) add [align(s,t)] * w_word(w_s,w_t);

// This formula is an example for real valued formulae. The formula tests
// whether the tokens of the word have a IBM model 4 translation probability
// and if so add a score scaled by a weight and the probability for this token pair.
weight w_model4: Double;
factor: for Int s, SourceWord w_s, Int t, TargetWord w_t, Double prob
  if src_word(s,w_s) & tgt_word(t,w_t) & model4(w_s,w_t,prob) add [align(s,t)] * prob * w_model4;

