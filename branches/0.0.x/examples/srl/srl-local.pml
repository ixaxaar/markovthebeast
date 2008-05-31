// This formula tests whether the argument is to the left of the (SRL) predicate
weight w_left: Role -> Double;
factor: for Int p, Int a, Role r
  if word(p,_) & word(a,_) & a < p add [role(p,a,r)] * w_left(r);

// This formula checks the POS tag of the predicate and argument token
weight w_pos_pa: Pos x Pos x Role -> Double;
factor: for Int p, Int a, Pos p_pos, Pos a_pos, Role r
  if pos(p,p_pos) & pos(a,a_pos) add [role(p,a,r)] * w_pos_pa(p_pos,a_pos,r);

// This formula checks the POS tag of the predicate token
weight w_pos_p: Pos x Role -> Double;
factor: for Int p, Int a, Pos p_pos, Role r
  if pos(p,p_pos) & pos(a,_) add [role(p,a,r)] * w_pos_p(p_pos,r);

// By default features are only considered if they have been seen at least
// once in the training set. For the formula above this means that we cannot
// learn from negative examples. The command below enforces that every possible
// POS tag & role combination (argument to w_pos_p) can have a nonzero weight
set collector.all.w_pos_p = true;
