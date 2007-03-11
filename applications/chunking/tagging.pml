//there exists exactly one tag
/*
factor:
  for Int t if word(t,_) : |Pos p : pos(t,p)| >= 1;
*/

factor:
  for Int t if word(t,_) : |Pos p : pos(t,p)| <= 1;  