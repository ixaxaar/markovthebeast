type Word: ...;
type Pos: ...;
type Role: ...;

predicate word: Int x Word;
predicate pos: Int x Pos;
predicate role: Int x Int x Role;
predicate unique: Role;

observed: word,pos;
hidden: role;
global: unique;

/* Deterministic formulae */
factor: for Int p, Int a if word(p,_) & word(a,_) : |Role r: role(p,a,r)| <=1;

factor: for Int p, Role r if word(p,_) & unique(r) : |Int a: word(a,_) & role(p,a,r)| <=1;

/* Soft formulae */
weight w_left: Role -> Double;
factor: for Int p, Int a, Role r
  if word(p,_) & word(a,_) & a < p add [role(p,a,r)] * w_left(r);

weight w_pos_pa: Pos x Pos x Role -> Double;
factor: for Int p, Int a, Pos p_pos, Pos a_pos, Role r
  if pos(p,p_pos) & pos(a,a_pos) add [role(p,a,r)] * w_pos_pa(p_pos,a_pos,r);

weight w_pos_p: Pos x Role -> Double;
factor: for Int p, Int a, Pos p_pos, Role r
  if pos(p,p_pos) & pos(a,_) add [role(p,a,r)] * w_pos_p(p_pos,r);

set collector.all.w_pos_p = true;


//set collector.all.w_pos = true;  

load global from "global.atoms";
load corpus from "train.atoms";

collect;

print weights;

save corpus to instances "/tmp/srl-example.instances";

learn for 10 epochs;

print weights;

save weights to dump "srl.weights";

load corpus from "test.atoms";
save corpus to ram;

next; solve; print eval;
next; solve; print eval;

test to "system.atoms";






