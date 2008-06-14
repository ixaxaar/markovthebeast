weight w_brill: Pos x Pos -> Double;

factor:
  for Int t, Pos p, Pos pBrill, Word w
  if word(t,w) & brill(w,pBrill)
  add [pos(t,p)] * w_brill(pBrill,p);