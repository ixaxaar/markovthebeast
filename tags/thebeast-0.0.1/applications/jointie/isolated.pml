/*** mutual exclusive ***/
factor: for Bib b, Position p if token(_,p,b): |Field f: inField(b,f,p)| <= 1;

/*** word / fld regression ***/
weight w_lex : Token x Field -> Double;
factor: for Bib b, position p, Token t, Field f if token(t,p,b) & !isDate(t) & !isDigit(t)
  add [inField(b,f,p)] * w_lex(t,f);

weight w_date : Field -> Double;
factor: for Bib b, position p, Token t, Field f if token(t,p,b) & isDate(t)
  add [inField(b,f,p)] * w_date(f);

weight w_digit : Field -> Double;
factor: for Bib b, position p, Token t, Field f if token(t,p,b) & isDigit(t)
  add [inField(b,f,p)] * w_digit(f);


/*** signature words ***/
//!LastInitial(c,i) v !LessThan(j,i) v !InField(c,Ftitle,j)
//INVERTED
weight w_lastInitialTitle: Double;
factor: for Bib b, Position j, Position i if lessThan(j,i) & lastInitial(b,i)
  add [inField(b,Ftitle,j)] * w_lastInitialTitle;


//!LastInitial(c,i) v !LessThan(j,i) v !InField(c,Fvenue,j)
//INVERTED
weight w_lastInitialVenue: Double;
factor: for Bib b, Position j, Position i if lessThan(j,i) & lastInitial(b,i)
  add [inField(b,Fvenue,j)] * w_lastInitialVenue;


//!FirstIn(c,i) v !LessThan(i,j) v !InField(c,Fauthor,j)
//INVERTED
weight w_firstInitialAuthor: Double;
factor: for Bib b, Position j, Position i if lessThan(i,j) & firstIn(b,i)
  add [inField(b,Fauthor,j)] * w_firstInitialAuthor;

//!FirstNonAuthorTitleTkn(c,i) v !LessThan(i,j) v !InField(c,Fauthor,j)
//INVERTED
weight w_firstNonAuthorTkn: Double;
factor: for Bib b, Position j, Position i if lessThan(i,j) & firstNonAuthorTitleTkn(b,i)
  add [inField(b,Fauthor,j)] * w_firstNonAuthorTkn;


//!FirstNonAuthorTitleTkn(c,i) v !LessThan(i,j) v !InField(c,Ftitle,j)
//INVERTED
weight w_firstNonTitleTkn: Double;
factor: for Bib b, Position j, Position i if lessThan(i,j) & firstNonAuthorTitleTkn(b,i)
  add [inField(b,Ftitle,j)] * w_firstNonTitleTkn;

// initial either author or editor
//!Token(w,i,c) v !IsAlphaChar(w) v !FollowBy(c,i,TPERIOD) v InField(c,Fauthor,i) v InField(c,Fvenue,i)
// separated into two clauses a la alchemy
weight w_initialAuthor: Double;
factor: for Bib b, Position i, Token w if token(w,i,b) & isAlphaChar(w) & followBy(b,i,TPERIOD)
  add [inField(b,Fauthor,i)] * w_initialAuthor;

weight w_initialVenue: Double;
factor: for Bib b, Position i, Token w if token(w,i,b) & isAlphaChar(w) & followBy(b,i,TPERIOD)
  add [inField(b,Fvenue,i)] * w_initialVenue;


/*** position rule ***/
//Center(c,i)=>InField(c,Ftitle,i)
weight w_center: Double;
factor: for Bib b, Position i if center(b,i) add [inField(b,Ftitle,i)] * w_center;

//!Token(w,P00,c) v IsDigit(w) v InField(c,+f,P00)
weight w_start: Field -> Double;
factor: for Bib b, Token w, Field f if token(w,P00,b) & !isDigit(w) add [inField(b,f,P00)] * w_start(f);

//InField(c,+f,P01)
weight w_second: Field -> Double;
factor: for Field f, Bib b if token(_,_,b) add [inField(b,f,P01)] * w_start(f);

// ------------------------------------------ //
// ER
// ------------------------------------------ //
//SameBib(c,c)
weight w_reflex: Double;
factor: for Bib b add [sameBib(b,b)] * w_reflex;

//SameBib(c1,c2)
weight w_bibPrior: Double-;
factor: for Bib b1, Bib b2 add [sameBib(b1,b2)] * w_bibPrior;

//SimilarTitle(c1,i1,j1,c2,i2,j2) ^ SimilarVenue(c1,c2) => SameBib(c1,c2)
weight w_similar: Double;
factor: for Bib b1, Bib b2, Position i1, Position i2, Position j1, Position j2
  if similarTitle(b1,i1,j1,b2,i2,j2) & similarVenue(b1,b2) add [sameBib(b1,b2)] * w_similar;


// ------------------------------------------ //
// Consecutive rules: Base
// ------------------------------------------ //
// =>
//Next(j,i) ^ !HasPunc(c,i) ^ InField(c,+f,i) => InField(c,+f,j)
weight w_next: Field -> Double+;
factor: for Bib b, Field f, Position i, Position j if next(j,i) & !hasPunc(b,i)
  add [inField(b,f,i)=>inField(b,f,j)] * w_next(f);

//Next(j,i) ^ HasComma(c,i) ^ InField(c,+f,i) => InField(c,+f,j)
weight w_nextComma: Field -> Double+;
factor: for Bib b, Field f, Position i, Position j if next(j,i) & hasComma(b,i)
  add [inField(b,f,i)=>inField(b,f,j)] * w_nextComma(f);

// <=
//Next(j,i) ^ !HasPunc(c,i) ^ InField(c,+f,j) => InField(c,+f,i)
weight w_prev: Field -> Double+;
factor: for Bib b, Field f, Position i, Position j if next(j,i) & !hasPunc(b,i)
  add [inField(b,f,j)=>inField(b,f,i)] * w_prev(f);

//Next(j,i) ^ HasComma(c,i) ^ InField(c,+f,j) => InField(c,+f,i)
weight w_prevComma: Field -> Double+;
factor: for Bib b, Field f, Position i, Position j if next(j,i) & hasComma(b,i)
  add [inField(b,f,j)=>inField(b,f,i)] * w_prevComma(f);

