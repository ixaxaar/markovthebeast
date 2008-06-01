type Cpos: 
	BR, J, PUNC, ETC, WP, WD, V, I, C, J, PR, MD, EX, WP, R, PO, 
	CD, BR, RB, T, FW, LS, N, D, WR, PD, START;

type VeryCoarse:
	V,N,J,O;


predicate cpos: Pos x Cpos;
predicate verycoarse: Pos x VeryCoarse; 
predicate rare: Word;
predicate brill: Word x Pos;
