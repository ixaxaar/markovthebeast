package thebeast.pml.parser;

import java_cup.runtime.Symbol;
%%
%cup
%line
%state COMMENT
%state MLCOMMENT
%state MLMAYEND
%char
%eofval{
 	return (new Symbol(sym.EOF));
%eofval}
%{

private Symbol symbol(int type) {
   return new Symbol(type, yychar,yyline,yytext());
}
private Symbol symbol(int type, Object value) {
   return new Symbol(type, yychar, yyline, value);
}
%}
%%
<YYINITIAL> "type" { return symbol(sym.TYPE); }
<YYINITIAL> "predicate" { return symbol(sym.PREDICATE); }
<YYINITIAL> "arg" { return symbol(sym.ARG); }
<YYINITIAL> "sequential" { return symbol(sym.SEQ); }
<YYINITIAL> "weight" { return symbol(sym.WEIGHT); }
<YYINITIAL> "weights" { return symbol(sym.WEIGHTS); }
<YYINITIAL> "..." { return symbol(sym.ELLIPSIS); }
<YYINITIAL> "hidden" { return symbol(sym.HIDDEN); }
<YYINITIAL> "acyclic" { return symbol(sym.ACYCLIC); }
<YYINITIAL> "factor" { return symbol(sym.FACTOR); }
<YYINITIAL> "bins" { return symbol(sym.BINS); }
<YYINITIAL> "clear" { return symbol(sym.CLEAR); }
<YYINITIAL> "observed" { return symbol(sym.OBSERVED); }
<YYINITIAL> "for" { return symbol(sym.FOR); }
<YYINITIAL> "set" { return symbol(sym.SET); }
<YYINITIAL> "inf" { return symbol(sym.INF); }
<YYINITIAL> "add" { return symbol(sym.ADD); }
<YYINITIAL> "include" { return symbol(sym.INCLUDE); }
<YYINITIAL> "atoms" { return symbol(sym.ATOMS); }
<YYINITIAL> "types" { return symbol(sym.TYPES); }
<YYINITIAL> "corpus" { return symbol(sym.CORPUS); }
<YYINITIAL> "scores" { return symbol(sym.SCORES); }
<YYINITIAL> "from" { return symbol(sym.FROM); }
<YYINITIAL> "to" { return symbol(sym.TO); }
<YYINITIAL> "print" { return symbol(sym.PRINT); }
<YYINITIAL> "inspect" { return symbol(sym.INSPECT); }
<YYINITIAL> "solve" { return symbol(sym.SOLVE); }
<YYINITIAL> "greedy" { return symbol(sym.GREEDY); }
<YYINITIAL> "learn" { return symbol(sym.LEARN); }
<YYINITIAL> "test" { return symbol(sym.TEST); }
<YYINITIAL> "epochs" { return symbol(sym.EPOCHS); }
<YYINITIAL> "load" { return symbol(sym.LOAD); }
<YYINITIAL> "save" { return symbol(sym.SAVE); }
<YYINITIAL> "collect" { return symbol(sym.COLLECT); }
<YYINITIAL> "if" { return symbol(sym.IF); }
<YYINITIAL> "next" { return symbol(sym.JUMP); }
<YYINITIAL> "_" { return symbol(sym.UNDERSCORE); }
<YYINITIAL> "^-1" { return symbol(sym.INVERT); }
<YYINITIAL> ";" { return symbol(sym.SEMI); }
<YYINITIAL> ":" { return symbol(sym.COLON); }
<YYINITIAL> ";;" { return symbol(sym.DOUBLESEMI); }
<YYINITIAL> ".." { return symbol(sym.DOUBLEDOT); }
<YYINITIAL> "." { return symbol(sym.DOT); }
<YYINITIAL> "," { return symbol(sym.COMMA); }
<YYINITIAL> "+" { return symbol(sym.PLUS); }
<YYINITIAL> "-" { return symbol(sym.MINUS); }
<YYINITIAL> "*" { return symbol(sym.TIMES); }
<YYINITIAL> "x" { return symbol(sym.X); }
<YYINITIAL> "~" { return symbol(sym.TILDE); }
<YYINITIAL> "#" { return symbol(sym.HASH); }
<YYINITIAL> "@" { return symbol(sym.AT); }
<YYINITIAL> "(" { return symbol(sym.LPAREN); }
<YYINITIAL> ")" { return symbol(sym.RPAREN); }
<YYINITIAL> "<-" { return symbol(sym.PUT); }
<YYINITIAL> "[" { return symbol(sym.LSQPAREN); }
<YYINITIAL> "]" { return symbol(sym.RSQPAREN); }
<YYINITIAL> "{" { return symbol(sym.LCLPAREN); }
<YYINITIAL> "}" { return symbol(sym.RCLPAREN); }
<YYINITIAL> "{*" { return symbol(sym.LCLPARENSTAR); }
<YYINITIAL> "*}" { return symbol(sym.RCLPARENSTAR); }
<YYINITIAL> "{+" { return symbol(sym.LCLPARENPLUS); }
<YYINITIAL> "+}" { return symbol(sym.RCLPARENPLUS); }
<YYINITIAL> "{!" { return symbol(sym.LCLPARENEXCL); }
<YYINITIAL> "!}" { return symbol(sym.RCLPARENEXCL); }
<YYINITIAL> "|" { return symbol(sym.BAR);}
<YYINITIAL> "=" { return symbol(sym.ASSIGN); }
<YYINITIAL> "!" { return symbol(sym.EXCL); }
<YYINITIAL> "==" { return symbol(sym.EQUALS); }
<YYINITIAL> "!=" { return symbol(sym.NOTEQUALS); }
<YYINITIAL> "&" { return symbol(sym.AND); }
<YYINITIAL> "$" { return symbol(sym.DOLLAR); }
<YYINITIAL> "&&" { return symbol(sym.DOUBLEAND); }
<YYINITIAL> "||" { return symbol(sym.DOUBLEBAR); }
<YYINITIAL> "=>" { return symbol(sym.IMPLIES); }
<YYINITIAL> "<=>" { return symbol(sym.EQUIVALENCE); }
<YYINITIAL> "<" { return symbol(sym.LESSTHAN); }
<YYINITIAL> ">" { return symbol(sym.GREATER); }
<YYINITIAL> "<=" { return symbol(sym.LEQ); }
<YYINITIAL> ">=" { return symbol(sym.GEQ); }
<YYINITIAL> "->" { return symbol(sym.ARROW); }
<YYINITIAL> "~>" { return symbol(sym.CURLYARROW); }
<YYINITIAL> "let" { return symbol(sym.LET); }
<YYINITIAL> "function" { return symbol(sym.FUNCTION); }
<YYINITIAL> "lambda" { return symbol(sym.LAMBDA); }
<YYINITIAL> "when" { return symbol(sym.WHEN);}
<YYINITIAL> "in" { return symbol(sym.IN);}
<YYINITIAL> "of" { return symbol(sym.OF);}
<YYINITIAL> "assert" { return symbol(sym.ASSERT);}
<YYINITIAL> "java" { return symbol(sym.JAVA);}
<YYINITIAL> "true" { return symbol(sym.TRUE);}
<YYINITIAL> "false" { return symbol(sym.FALSE);}
<YYINITIAL> "with" { return symbol(sym.WITH);}
<YYINITIAL> "while" { return symbol(sym.WHILE);}
<YYINITIAL> "do" { return symbol(sym.DO);}
<YYINITIAL> "null" { return symbol(sym.NULL);}

<YYINITIAL> "//" {
  yybegin(COMMENT);
}

<COMMENT> [^\n] {
}

<COMMENT> [\n] {
  yybegin(YYINITIAL);
}

<YYINITIAL> "/*" {
  yybegin(MLCOMMENT);
}

<MLCOMMENT> [ \t\r\n\f] {
}

<MLCOMMENT> [^*] {
}

<MLCOMMENT> "*" {
  yybegin(MLMAYEND);
}

<MLMAYEND> [^/] {
  yybegin(MLCOMMENT);
}

<MLMAYEND> "/" {
  yybegin(YYINITIAL);
}


<YYINITIAL> [a-z][A-Z_a-z0-9]* { return symbol(sym.LOWERCASEID, yytext());}
<YYINITIAL> [A-Z][A-Z_a-z0-9]* { return symbol(sym.UPPERCASEID, yytext());}

<YYINITIAL> [$][A-Z_a-z0-9]* { return symbol(sym.ARGIDENT, yytext());}

<YYINITIAL> [0-9]+ { return symbol(sym.NUMBER, new Integer(yytext())); }

<YYINITIAL> [0-9]*[.][0-9]+ { return symbol(sym.DOUBLE, new Double(yytext())); }

<YYINITIAL> [\"][^\"]*[\"] { return symbol(sym.STRING, yytext()); }

<YYINITIAL> [\'][a-zA-Z. \t]+[\'] { return symbol(sym.JAVAID, yytext()); }


<YYINITIAL> [ \t\r\n\f] { /* ignore white space. */ }


<YYINITIAL> . { System.err.println("Illegal character: "+yytext()); }