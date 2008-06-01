package markof.theparser;

import java_cup.runtime.Symbol;
%%
%cup
%line
%char
%eofval{
 	return (new Symbol(sym.EOF));
%eofval}
%%
"^-1" { return new Symbol(sym.INVERT); }
";" { return new Symbol(sym.SEMI); }
":" { return new Symbol(sym.COLON); }
";;" { return new Symbol(sym.DOUBLESEMI); }
".." { return new Symbol(sym.DOUBLEDOT); }
"." { return new Symbol(sym.DOT); }
"," { return new Symbol(sym.COMMA); }
"+" { return new Symbol(sym.PLUS); }
"-" { return new Symbol(sym.MINUS); }
"*" { return new Symbol(sym.TIMES); }
"~" { return new Symbol(sym.TILDE); }
"#" { return new Symbol(sym.HASH); }
"@" { return new Symbol(sym.AT); }
"(" { return new Symbol(sym.LPAREN); }
")" { return new Symbol(sym.RPAREN); }
"<-" { return new Symbol(sym.PUT); }
"[" { return new Symbol(sym.LSQPAREN); }
"]" { return new Symbol(sym.RSQPAREN); }
"{" { return new Symbol(sym.LCLPAREN); }
"}" { return new Symbol(sym.RCLPAREN); }
"{*" { return new Symbol(sym.LCLPARENSTAR); }
"*}" { return new Symbol(sym.RCLPARENSTAR); }
"{+" { return new Symbol(sym.LCLPARENPLUS); }
"+}" { return new Symbol(sym.RCLPARENPLUS); }
"{!" { return new Symbol(sym.LCLPARENEXCL); }
"!}" { return new Symbol(sym.RCLPARENEXCL); }
"|" { return new Symbol(sym.BAR);}
"=" { return new Symbol(sym.ASSIGN); }
"!" { return new Symbol(sym.EXCL); }
"==" { return new Symbol(sym.EQUALS); }
"!=" { return new Symbol(sym.NOTEQUALS); }
"&" { return new Symbol(sym.AND); }
"$" { return new Symbol(sym.DOLLAR); }
"&&" { return new Symbol(sym.DOUBLEAND); }
"||" { return new Symbol(sym.DOUBLEBAR); }
"=>" { return new Symbol(sym.IMPLIES); }
"<=>" { return new Symbol(sym.EQUIVALENCE); }
"<" { return new Symbol(sym.LESSTHAN); }
">" { return new Symbol(sym.GREATER); }
"->" { return new Symbol(sym.ARROW); }
"~>" { return new Symbol(sym.CURLYARROW); }
"let" { return new Symbol(sym.LET); }
"function" { return new Symbol(sym.FUNCTION); }
"lambda" { return new Symbol(sym.LAMBDA); }
"when" { return new Symbol(sym.WHEN);}
"in" { return new Symbol(sym.IN);}
"of" { return new Symbol(sym.OF);}
"assert" { return new Symbol(sym.ASSERT);}
"java" { return new Symbol(sym.JAVA);}
"true" { return new Symbol(sym.TRUE);}
"false" { return new Symbol(sym.FALSE);}
"with" { return new Symbol(sym.WITH);}
"while" { return new Symbol(sym.WHILE);}
"do" { return new Symbol(sym.DO);}
"null" { return new Symbol(sym.NULL);}

[A-Z_a-z][A-Z_a-z0-9]* { return new Symbol(sym.IDENT, yytext());}

[$][A-Z_a-z0-9]* { return new Symbol(sym.ARGIDENT, yytext());}

[0-9]+ { return new Symbol(sym.NUMBER, new Integer(yytext())); }

[0-9]*[.][0-9]+ { return new Symbol(sym.DOUBLE, new Double(yytext())); }

[\"][^\"]*[\"] { return new Symbol(sym.STRING, yytext()); }

[\'][a-zA-Z. \t]+[\'] { return new Symbol(sym.JAVAID, yytext()); }


[ \t\r\n\f] { /* ignore white space. */ }

//"/*"[^*]*"*/" { /* multiline comment */ }
"//"(.)* { /* one-line comment */ }


. { System.err.println("Illegal character: "+yytext()); }