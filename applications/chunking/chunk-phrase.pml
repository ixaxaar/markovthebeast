/*
weight w_highestfreq: Chunk x Int -> Double;
factor: for Int b, Int e, Int freq, Chunk c, Int freq_bm1, Int freq_b, Int freq_ep1
        if highestfreq(b+1,e,freq) & freq < 100 &
           count(b-1,freq_bm1) & freq_bm1 > 1000 &
           count(b,freq_b) & freq_b > 1000 &
           count(e+1, freq_ep1) & freq_ep1 > 1000
        add [chunk(b,e,c)] * w_highestfreq(c,e-b);

*/

weight w_freq_4: Int x Int x Int x Int x Chunk -> Double;
factor:
  for Int b, Int freq1, Int freq2, Int freq3, Int freq4, Chunk c
  if count(b-1, freq1) & count(b, freq2) & count(b+1,freq3) & count(b+2,freq4)
  add [chunk(b,b+1,c)] * w_freq_4(bins(1000,freq1),bins(1000,freq2),bins(1000,freq3),bins(1000,freq4),c);


weight w_case_4: Case x Case x Case x Case x Chunk -> Double;
factor:
  for Int b, Case case1, Case case2, Case case3, Case case4, Chunk c
  if case(b-1, case1) & case(b, case2) & case(b+1,case3) & case(b+2,case4)
  add [chunk(b,b+1,c)] * w_case_4(case1,case2,case3,case4,c);


/*
weight w_highestfreq_word: Chunk x Word -> Double;
factor: for Int b, Int e, Int freq, Chunk c, Word w
        if highestfreq(b,e,freq) & freq < 100 & word(b,w)
        add [chunk(b,e,c)] * w_highestfreq_word(c,w);
*/
