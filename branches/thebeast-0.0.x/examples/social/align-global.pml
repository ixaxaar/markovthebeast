// (soft) global formula that rewards solutions where words are translated in a
// diagonal fashion
weight w_diag: Double+;
factor: for Int s, Int t 
	if src_word(s,_) & src_word(s+1,_) & tgt_word(t,_) & tgt_word(t+1,_)
	add [align(s,t) => align(s+1,t+1)] * w_diag;
