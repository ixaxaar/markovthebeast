//bias feature
weight w_bias: Chunk -> Double-;
factor:
  for Int b, Int e, Chunk c
  if word(b,_) & word(e,_) & e >= b
  add [chunk(b,e,c)] * w_bias(c);

//length feature
weight w_length: Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c
  if word(b,_) & word(e,_) & e >= b
  add[chunk(b,e,c)] * w_length(bins(0,5,10,e-b),c);

//word features
weight w_word_i: Word x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Word w_i
  if word(b,_) & word(e,_) & word(i,w_i) & b <= i & i <= e
  //add [chunk(b,e,c)] * w_word_i(w_i, i - b, 0,c);
  add [chunk(b,e,c)] * w_word_i(w_i, i - b, bins(0,5,10,e-b),c);

weight w_bi_word: Word x Word x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Word w1, Word w2
  if word(b,_) & word(e,_) & m >= b & m <= e - 1 & word(m,w1) & word(m+1,w2)
  add [chunk(b,e,c)] * w_bi_word(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_word: Word x Word x Word x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Word w1, Word w2, Word w3
  if word(b,_) & word(e,_) & m >= b & m <= e - 2 & word(m,w1) & word(m+1,w2) & word(m+2,w3)
  add [chunk(b,e,c)] * w_tri_word(w1,w2,w3,bins(0,5,10,e-b), c);

weight w_before_word: Word x Int x Chunk-> Double;
factor:
  for Int b, Int e, Chunk c, Word w
  if word(b,_) & word(e,_) & e >= b & word(b-1,w)
  add [chunk(b,e,c)] * w_before_word(w,bins(0,5,10,e-b),c);

weight w_after_word: Word x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Word w
  if word(b,_) & word(e,_) & e >= b & word(e+1,w)
  add [chunk(b,e,c)] * w_after_word(w,bins(0,5,10,e-b),c);

//case features
weight w_case_i: Case x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Case w_i
  if case(b,_) & case(e,_) & case(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_case_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_case: Case x Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Case w1, Case w2
  if case(b,_) & case(e,_) & m >= b & m <= e - 1 & case(m,w1) & case(m+1,w2)
  add [chunk(b,e,c)] * w_bi_case(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_case: Case x Case x Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Case w1, Case w2, Case w3
  if case(b,_) & case(e,_) & m >= b & m <= e - 2 & case(m,w1) & case(m+1,w2) & case(m+2,w3)
  add [chunk(b,e,c)] * w_tri_case(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_case: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Case w
  if case(b,_) & case(e,_) & e >= b & case(b-1,w)
  add [chunk(b,e,c)] * w_before_case(w,bins(0,5,10,e-b),c);

weight w_after_case: Case x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Case w
  if case(b,_) & case(e,_) & e >= b & case(e+1,w)
  add [chunk(b,e,c)] * w_after_case(w,bins(0,5,10,e-b),c);

//stopword features
weight w_stopword_i: Inset x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Inset w_i
  if stopword(b,_) & stopword(e,_) & stopword(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_stopword_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_stopword: Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2
  if stopword(b,_) & stopword(e,_) & m >= b & m <= e - 1 & stopword(m,w1) & stopword(m+1,w2)
  add [chunk(b,e,c)] * w_bi_stopword(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_stopword: Inset x Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2, Inset w3
  if stopword(b,_) & stopword(e,_) & m >= b & m <= e - 2 & stopword(m,w1) & stopword(m+1,w2) & stopword(m+2,w3)
  add [chunk(b,e,c)] * w_tri_stopword(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_stopword: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if stopword(b,_) & stopword(e,_) & e >= b & stopword(b-1,w)
  add [chunk(b,e,c)] * w_before_stopword(w,bins(0,5,10,e-b),c);

weight w_after_stopword: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if stopword(b,_) & stopword(e,_) & e >= b & stopword(e+1,w)
  add [chunk(b,e,c)] * w_after_stopword(w,bins(0,5,10,e-b),c);

//----------------------------
//name features
weight w_name_i: Inset x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Inset w_i
  if name(b,_) & name(e,_) & name(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_name_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_name: Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2
  if name(b,_) & name(e,_) & m >= b & m <= e - 1 & name(m,w1) & name(m+1,w2)
  add [chunk(b,e,c)] * w_bi_name(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_name: Inset x Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2, Inset w3
  if name(b,_) & name(e,_) & m >= b & m <= e - 2 & name(m,w1) & name(m+1,w2) & name(m+2,w3)
  add [chunk(b,e,c)] * w_tri_name(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_name: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if name(b,_) & name(e,_) & e >= b & name(b-1,w)
  add [chunk(b,e,c)] * w_before_name(w,bins(0,5,10,e-b),c);

weight w_after_name: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if name(b,_) & name(e,_) & e >= b & name(e+1,w)
  add [chunk(b,e,c)] * w_after_name(w,bins(0,5,10,e-b),c);

//----------------------------
//orgname features
weight w_orgname_i: Inset x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Inset w_i
  if orgname(b,_) & orgname(e,_) & orgname(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_orgname_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_orgname: Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2
  if orgname(b,_) & orgname(e,_) & m >= b & m <= e - 1 & orgname(m,w1) & orgname(m+1,w2)
  add [chunk(b,e,c)] * w_bi_orgname(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_orgname: Inset x Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2, Inset w3
  if orgname(b,_) & orgname(e,_) & m >= b & m <= e - 2 & orgname(m,w1) & orgname(m+1,w2) & orgname(m+2,w3)
  add [chunk(b,e,c)] * w_tri_orgname(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_orgname: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if orgname(b,_) & orgname(e,_) & e >= b & orgname(b-1,w)
  add [chunk(b,e,c)] * w_before_orgname(w,bins(0,5,10,e-b),c);

weight w_after_orgname: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if orgname(b,_) & orgname(e,_) & e >= b & orgname(e+1,w)
  add [chunk(b,e,c)] * w_after_orgname(w,bins(0,5,10,e-b),c);

//----------------------------
//placename features
weight w_placename_i: Inset x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Inset w_i
  if placename(b,_) & placename(e,_) & placename(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_placename_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_placename: Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2
  if placename(b,_) & placename(e,_) & m >= b & m <= e - 1 & placename(m,w1) & placename(m+1,w2)
  add [chunk(b,e,c)] * w_bi_placename(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_placename: Inset x Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2, Inset w3
  if placename(b,_) & placename(e,_) & m >= b & m <= e - 2 & placename(m,w1) & placename(m+1,w2) & placename(m+2,w3)
  add [chunk(b,e,c)] * w_tri_placename(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_placename: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if placename(b,_) & placename(e,_) & e >= b & placename(b-1,w)
  add [chunk(b,e,c)] * w_before_placename(w,bins(0,5,10,e-b),c);

weight w_after_placename: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if placename(b,_) & placename(e,_) & e >= b & placename(e+1,w)
  add [chunk(b,e,c)] * w_after_placename(w,bins(0,5,10,e-b),c);

//----------------------------
//company features
weight w_company_i: Inset x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Inset w_i
  if company(b,_) & company(e,_) & company(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_company_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_company: Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2
  if company(b,_) & company(e,_) & m >= b & m <= e - 1 & company(m,w1) & company(m+1,w2)
  add [chunk(b,e,c)] * w_bi_company(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_company: Inset x Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2, Inset w3
  if company(b,_) & company(e,_) & m >= b & m <= e - 2 & company(m,w1) & company(m+1,w2) & company(m+2,w3)
  add [chunk(b,e,c)] * w_tri_company(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_company: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if company(b,_) & company(e,_) & e >= b & company(b-1,w)
  add [chunk(b,e,c)] * w_before_company(w,bins(0,5,10,e-b),c);

weight w_after_company: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if company(b,_) & company(e,_) & e >= b & company(e+1,w)
  add [chunk(b,e,c)] * w_after_company(w,bins(0,5,10,e-b),c);

//----------------------------
//firstname features
weight w_firstname_i: Inset x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Inset w_i
  if firstname(b,_) & firstname(e,_) & firstname(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_firstname_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_firstname: Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2
  if firstname(b,_) & firstname(e,_) & m >= b & m <= e - 1 & firstname(m,w1) & firstname(m+1,w2)
  add [chunk(b,e,c)] * w_bi_firstname(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_firstname: Inset x Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2, Inset w3
  if firstname(b,_) & firstname(e,_) & m >= b & m <= e - 2 & firstname(m,w1) & firstname(m+1,w2) & firstname(m+2,w3)
  add [chunk(b,e,c)] * w_tri_firstname(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_firstname: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if firstname(b,_) & firstname(e,_) & e >= b & firstname(b-1,w)
  add [chunk(b,e,c)] * w_before_firstname(w,bins(0,5,10,e-b),c);

weight w_after_firstname: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if firstname(b,_) & firstname(e,_) & e >= b & firstname(e+1,w)
  add [chunk(b,e,c)] * w_after_firstname(w,bins(0,5,10,e-b),c);

//----------------------------
//lastname features
weight w_lastname_i: Inset x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Inset w_i
  if lastname(b,_) & lastname(e,_) & lastname(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_lastname_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_lastname: Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2
  if lastname(b,_) & lastname(e,_) & m >= b & m <= e - 1 & lastname(m,w1) & lastname(m+1,w2)
  add [chunk(b,e,c)] * w_bi_lastname(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_lastname: Inset x Inset x Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Inset w1, Inset w2, Inset w3
  if lastname(b,_) & lastname(e,_) & m >= b & m <= e - 2 & lastname(m,w1) & lastname(m+1,w2) & lastname(m+2,w3)
  add [chunk(b,e,c)] * w_tri_lastname(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_lastname: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if lastname(b,_) & lastname(e,_) & e >= b & lastname(b-1,w)
  add [chunk(b,e,c)] * w_before_lastname(w,bins(0,5,10,e-b),c);

weight w_after_lastname: Inset x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Inset w
  if lastname(b,_) & lastname(e,_) & e >= b & lastname(e+1,w)
  add [chunk(b,e,c)] * w_after_lastname(w,bins(0,5,10,e-b),c);

//----------------------------




//postfix 1 features
weight w_postfix1_i: Postfix1 x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Postfix1 w_i
  if postfix1(b,_) & postfix1(e,_) & postfix1(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_postfix1_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_postfix1: Postfix1 x Postfix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Postfix1 w1, Postfix1 w2
  if postfix1(b,_) & postfix1(e,_) & m >= b & m <= e - 1 & postfix1(m,w1) & postfix1(m+1,w2)
  add [chunk(b,e,c)] * w_bi_postfix1(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_postfix1: Postfix1 x Postfix1 x Postfix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Postfix1 w1, Postfix1 w2, Postfix1 w3
  if postfix1(b,_) & postfix1(e,_) & m >= b & m <= e - 2 & postfix1(m,w1) & postfix1(m+1,w2) & postfix1(m+2,w3)
  add [chunk(b,e,c)] * w_tri_postfix1(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_postfix1: Postfix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Postfix1 w
  if postfix1(b,_) & postfix1(e,_) & e >= b & postfix1(b-1,w)
  add [chunk(b,e,c)] * w_before_postfix1(w,bins(0,5,10,e-b),c);

weight w_after_postfix1: Postfix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Postfix1 w
  if postfix1(b,_) & postfix1(e,_) & e >= b & postfix1(e+1,w)
  add [chunk(b,e,c)] * w_after_postfix1(w,bins(0,5,10,e-b),c);

//postfix 2 features
weight w_postfix2_i: Postfix2 x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Postfix2 w_i
  if postfix2(b,_) & postfix2(e,_) & postfix2(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_postfix2_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_postfix2: Postfix2 x Postfix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Postfix2 w1, Postfix2 w2
  if postfix2(b,_) & postfix2(e,_) & m >= b & m <= e - 1 & postfix2(m,w1) & postfix2(m+1,w2)
  add [chunk(b,e,c)] * w_bi_postfix2(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_postfix2: Postfix2 x Postfix2 x Postfix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Postfix2 w1, Postfix2 w2, Postfix2 w3
  if postfix2(b,_) & postfix2(e,_) & m >= b & m <= e - 2 & postfix2(m,w1) & postfix2(m+1,w2) & postfix2(m+2,w3)
  add [chunk(b,e,c)] * w_tri_postfix2(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_postfix2: Postfix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Postfix2 w
  if postfix2(b,_) & postfix2(e,_) & e >= b & postfix2(b-1,w)
  add [chunk(b,e,c)] * w_before_postfix2(w,bins(0,5,10,e-b),c);

weight w_after_postfix2: Postfix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Postfix2 w
  if postfix2(b,_) & postfix2(e,_) & e >= b & postfix2(e+1,w)
  add [chunk(b,e,c)] * w_after_postfix2(w,bins(0,5,10,e-b),c);

//postfix 3 features
weight w_postfix3_i: Postfix3 x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Postfix3 w_i
  if postfix3(b,_) & postfix3(e,_) & postfix3(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_postfix3_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_postfix3: Postfix3 x Postfix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Postfix3 w1, Postfix3 w2
  if postfix3(b,_) & postfix3(e,_) & m >= b & m <= e - 1 & postfix3(m,w1) & postfix3(m+1,w2)
  add [chunk(b,e,c)] * w_bi_postfix3(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_postfix3: Postfix3 x Postfix3 x Postfix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Postfix3 w1, Postfix3 w2, Postfix3 w3
  if postfix3(b,_) & postfix3(e,_) & m >= b & m <= e - 2 & postfix3(m,w1) & postfix3(m+1,w2) & postfix3(m+2,w3)
  add [chunk(b,e,c)] * w_tri_postfix3(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_postfix3: Postfix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Postfix3 w
  if postfix3(b,_) & postfix3(e,_) & e >= b & postfix3(b-1,w)
  add [chunk(b,e,c)] * w_before_postfix3(w,bins(0,5,10,e-b),c);

weight w_after_postfix3: Postfix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Postfix3 w
  if postfix3(b,_) & postfix3(e,_) & e >= b & postfix3(e+1,w)
  add [chunk(b,e,c)] * w_after_postfix3(w,bins(0,5,10,e-b),c);

//prefix 1 features
weight w_prefix1_i: Prefix1 x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Prefix1 w_i
  if prefix1(b,_) & prefix1(e,_) & prefix1(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_prefix1_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_prefix1: Prefix1 x Prefix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Prefix1 w1, Prefix1 w2
  if prefix1(b,_) & prefix1(e,_) & m >= b & m <= e - 1 & prefix1(m,w1) & prefix1(m+1,w2)
  add [chunk(b,e,c)] * w_bi_prefix1(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_prefix1: Prefix1 x Prefix1 x Prefix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Prefix1 w1, Prefix1 w2, Prefix1 w3
  if prefix1(b,_) & prefix1(e,_) & m >= b & m <= e - 2 & prefix1(m,w1) & prefix1(m+1,w2) & prefix1(m+2,w3)
  add [chunk(b,e,c)] * w_tri_prefix1(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_prefix1: Prefix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Prefix1 w
  if prefix1(b,_) & prefix1(e,_) & e >= b & prefix1(b-1,w)
  add [chunk(b,e,c)] * w_before_prefix1(w,bins(0,5,10,e-b),c);

weight w_after_prefix1: Prefix1 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Prefix1 w
  if prefix1(b,_) & prefix1(e,_) & e >= b & prefix1(e+1,w)
  add [chunk(b,e,c)] * w_after_prefix1(w,bins(0,5,10,e-b),c);

//prefix 2 features
weight w_prefix2_i: Prefix2 x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Prefix2 w_i
  if prefix2(b,_) & prefix2(e,_) & prefix2(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_prefix2_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_prefix2: Prefix2 x Prefix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Prefix2 w1, Prefix2 w2
  if prefix2(b,_) & prefix2(e,_) & m >= b & m <= e - 1 & prefix2(m,w1) & prefix2(m+1,w2)
  add [chunk(b,e,c)] * w_bi_prefix2(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_prefix2: Prefix2 x Prefix2 x Prefix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Prefix2 w1, Prefix2 w2, Prefix2 w3
  if prefix2(b,_) & prefix2(e,_) & m >= b & m <= e - 2 & prefix2(m,w1) & prefix2(m+1,w2) & prefix2(m+2,w3)
  add [chunk(b,e,c)] * w_tri_prefix2(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_prefix2: Prefix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Prefix2 w
  if prefix2(b,_) & prefix2(e,_) & e >= b & prefix2(b-1,w)
  add [chunk(b,e,c)] * w_before_prefix2(w,bins(0,5,10,e-b),c);

weight w_after_prefix2: Prefix2 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Prefix2 w
  if prefix2(b,_) & prefix2(e,_) & e >= b & prefix2(e+1,w)
  add [chunk(b,e,c)] * w_after_prefix2(w,bins(0,5,10,e-b),c);

//prefix 3 features
weight w_prefix3_i: Prefix3 x Int x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int i, Chunk c, Prefix3 w_i
  if prefix3(b,_) & prefix3(e,_) & prefix3(i,w_i) & b <= i & i <= e
  add [chunk(b,e,c)] * w_prefix3_i(w_i, i - b,bins(0,5,10,e-b), c);

weight w_bi_prefix3: Prefix3 x Prefix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Prefix3 w1, Prefix3 w2
  if prefix3(b,_) & prefix3(e,_) & m >= b & m <= e - 1 & prefix3(m,w1) & prefix3(m+1,w2)
  add [chunk(b,e,c)] * w_bi_prefix3(w1,w2,bins(0,5,10,e-b),c);

weight w_tri_prefix3: Prefix3 x Prefix3 x Prefix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Int m, Chunk c, Prefix3 w1, Prefix3 w2, Prefix3 w3
  if prefix3(b,_) & prefix3(e,_) & m >= b & m <= e - 2 & prefix3(m,w1) & prefix3(m+1,w2) & prefix3(m+2,w3)
  add [chunk(b,e,c)] * w_tri_prefix3(w1,w2,w3, bins(0,5,10,e-b),c);

weight w_before_prefix3: Prefix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Prefix3 w
  if prefix3(b,_) & prefix3(e,_) & e >= b & prefix3(b-1,w)
  add [chunk(b,e,c)] * w_before_prefix3(w,bins(0,5,10,e-b),c);

weight w_after_prefix3: Prefix3 x Int x Chunk -> Double;
factor:
  for Int b, Int e, Chunk c, Prefix3 w
  if prefix3(b,_) & prefix3(e,_) & e >= b & prefix3(e+1,w)
  add [chunk(b,e,c)] * w_after_prefix3(w,bins(0,5,10,e-b),c);
