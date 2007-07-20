package thebeast.pml.parser;
import java_cup.runtime.Symbol;


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

private Symbol symbol(int type) {
   return new Symbol(type, yychar,yyline,yytext());
}
private Symbol symbol(int type, Object value) {
   return new Symbol(type, yychar, yyline, value);
}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int MLCOMMENT = 2;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int MLMAYEND = 3;
	private final int yy_state_dtrans[] = {
		0,
		132,
		134,
		136
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NOT_ACCEPT,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NOT_ACCEPT,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NOT_ACCEPT,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NOT_ACCEPT,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NOT_ACCEPT,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NOT_ACCEPT,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NOT_ACCEPT,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NOT_ACCEPT,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NO_ANCHOR,
		/* 184 */ YY_NO_ANCHOR,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NO_ANCHOR,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NO_ANCHOR,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR,
		/* 196 */ YY_NO_ANCHOR,
		/* 197 */ YY_NO_ANCHOR,
		/* 198 */ YY_NO_ANCHOR,
		/* 199 */ YY_NO_ANCHOR,
		/* 200 */ YY_NO_ANCHOR,
		/* 201 */ YY_NO_ANCHOR,
		/* 202 */ YY_NO_ANCHOR,
		/* 203 */ YY_NO_ANCHOR,
		/* 204 */ YY_NO_ANCHOR,
		/* 205 */ YY_NO_ANCHOR,
		/* 206 */ YY_NO_ANCHOR,
		/* 207 */ YY_NO_ANCHOR,
		/* 208 */ YY_NO_ANCHOR,
		/* 209 */ YY_NO_ANCHOR,
		/* 210 */ YY_NO_ANCHOR,
		/* 211 */ YY_NO_ANCHOR,
		/* 212 */ YY_NO_ANCHOR,
		/* 213 */ YY_NO_ANCHOR,
		/* 214 */ YY_NO_ANCHOR,
		/* 215 */ YY_NO_ANCHOR,
		/* 216 */ YY_NO_ANCHOR,
		/* 217 */ YY_NO_ANCHOR,
		/* 218 */ YY_NO_ANCHOR,
		/* 219 */ YY_NO_ANCHOR,
		/* 220 */ YY_NO_ANCHOR,
		/* 221 */ YY_NO_ANCHOR,
		/* 222 */ YY_NO_ANCHOR,
		/* 223 */ YY_NO_ANCHOR,
		/* 224 */ YY_NO_ANCHOR,
		/* 225 */ YY_NO_ANCHOR,
		/* 226 */ YY_NO_ANCHOR,
		/* 227 */ YY_NO_ANCHOR,
		/* 228 */ YY_NO_ANCHOR,
		/* 229 */ YY_NO_ANCHOR,
		/* 230 */ YY_NO_ANCHOR,
		/* 231 */ YY_NO_ANCHOR,
		/* 232 */ YY_NO_ANCHOR,
		/* 233 */ YY_NO_ANCHOR,
		/* 234 */ YY_NO_ANCHOR,
		/* 235 */ YY_NO_ANCHOR,
		/* 236 */ YY_NO_ANCHOR,
		/* 237 */ YY_NO_ANCHOR,
		/* 238 */ YY_NO_ANCHOR,
		/* 239 */ YY_NO_ANCHOR,
		/* 240 */ YY_NO_ANCHOR,
		/* 241 */ YY_NO_ANCHOR,
		/* 242 */ YY_NO_ANCHOR,
		/* 243 */ YY_NO_ANCHOR,
		/* 244 */ YY_NO_ANCHOR,
		/* 245 */ YY_NO_ANCHOR,
		/* 246 */ YY_NO_ANCHOR,
		/* 247 */ YY_NO_ANCHOR,
		/* 248 */ YY_NO_ANCHOR,
		/* 249 */ YY_NO_ANCHOR,
		/* 250 */ YY_NO_ANCHOR,
		/* 251 */ YY_NO_ANCHOR,
		/* 252 */ YY_NO_ANCHOR,
		/* 253 */ YY_NO_ANCHOR,
		/* 254 */ YY_NO_ANCHOR,
		/* 255 */ YY_NO_ANCHOR,
		/* 256 */ YY_NO_ANCHOR,
		/* 257 */ YY_NO_ANCHOR,
		/* 258 */ YY_NO_ANCHOR,
		/* 259 */ YY_NO_ANCHOR,
		/* 260 */ YY_NO_ANCHOR,
		/* 261 */ YY_NO_ANCHOR,
		/* 262 */ YY_NO_ANCHOR,
		/* 263 */ YY_NO_ANCHOR,
		/* 264 */ YY_NO_ANCHOR,
		/* 265 */ YY_NO_ANCHOR,
		/* 266 */ YY_NO_ANCHOR,
		/* 267 */ YY_NO_ANCHOR,
		/* 268 */ YY_NO_ANCHOR,
		/* 269 */ YY_NO_ANCHOR,
		/* 270 */ YY_NO_ANCHOR,
		/* 271 */ YY_NO_ANCHOR,
		/* 272 */ YY_NO_ANCHOR,
		/* 273 */ YY_NO_ANCHOR,
		/* 274 */ YY_NO_ANCHOR,
		/* 275 */ YY_NO_ANCHOR,
		/* 276 */ YY_NO_ANCHOR,
		/* 277 */ YY_NO_ANCHOR,
		/* 278 */ YY_NO_ANCHOR,
		/* 279 */ YY_NO_ANCHOR,
		/* 280 */ YY_NO_ANCHOR,
		/* 281 */ YY_NO_ANCHOR,
		/* 282 */ YY_NO_ANCHOR,
		/* 283 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"52:9,54,53,52,54:2,52:18,54,44,58,35,48,52,47,59,37,38,33,32,31,27,21,51,56" +
",28,56:8,30,29,39,46,49,52,36,57:26,40,52,41,26,25,52,9,19,8,6,4,22,16,17,7" +
",50,55,14,24,13,18,3,11,5,10,1,12,23,15,20,2,55,42,45,43,34,52,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,284,
"0,1,2,3,4,1,5,6,7,8,1:2,9,10,11,1:4,12,1:2,13,1,14,15,16,17,18,19,1,20,3:2," +
"21,3:2,22,23,1:6,24,1:9,18,1:5,3:4,1,3,1:3,25,3:25,26,3:14,1:7,27,3,28,23,2" +
"9,1,30,31,32:2,33,34:2,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,5" +
"2,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,7" +
"7,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101" +
",102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,12" +
"0,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,1" +
"39,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157," +
"158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176" +
",177,178,179,180,181,182,183,184,185,186,187")[0];

	private int yy_nxt[][] = unpackFromString(188,60,
"1,2,119,248,268,119,122,125,249,177,178,119:2,220,179,221,269,270,128,222,3" +
",4,180,119:2,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,2" +
"7,28,29,223,120,123,30:2,119,8,31,126,129,-1:61,119,181,119,182,183,119:12," +
"32,119:2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:20,-1,119:4,-1:2,1" +
"19,-1:21,119,-1:4,119:3,-1:23,37,-1:6,38,-1:27,38,-1:30,118,-1:81,39,-1:31," +
"121,-1:6,8,-1:27,8,-1:32,40,-1:73,41,-1:59,42,-1:5,124,-1:59,43,-1:37,44,-1" +
":18,45,-1:45,46,47,-1:10,48,-1:58,49,-1:2,50,-1:58,51,-1:60,52,-1:2,53,-1:5" +
"7,54,-1:13,55:20,-1,55:4,-1:2,55,-1:21,55,-1:4,55:3,-1:48,56,-1:14,31:20,-1" +
",31:4,-1:2,31,-1:21,31,-1:4,31:3,-1:3,119:5,194,119,259,119,260,119:10,-1,6" +
"1,119:3,-1:2,119,-1:21,119,-1:4,119:3,-1:23,65,-1:66,38,-1:27,38,-1:52,69,-" +
"1:11,119:9,82,119:10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:9,105," +
"119:10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:30,67,-1:64,57,-1:17,58,-1" +
":9,119:17,33,119:2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:48,68,-1:14,11" +
"9:12,34,119:7,-1,35,119:3,-1:2,119,-1:21,119,-1:4,119:3,-1:3,127:57,59,127," +
"-1,119:18,277,119,-1,36,119:3,-1:2,119,-1:21,119,-1:4,119:3,-1:3,130:58,60," +
"-1,119:5,62,119:14,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:2,1,111:52,112" +
",111:6,-1,63,119:9,283,119:9,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:2,1," +
"113:32,114,113:19,115:2,113:5,-1,64,119:7,198,119:11,-1,119:4,-1:2,119,-1:2" +
"1,119,-1:4,119:3,-1:2,1,116:50,117,116:8,-1,119:4,66,119:15,-1,119:4,-1:2,1" +
"19,-1:21,119,-1:4,119:3,-1:3,119:3,70,119:16,-1,119:4,-1:2,119,-1:21,119,-1" +
":4,119:3,-1:3,71,119:19,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,7" +
"2,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,73,119:16,-1,119" +
":4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,74,119:19,-1,119:4,-1:2,119,-1:21,119" +
",-1:4,119:3,-1:3,119:13,75,119:6,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:" +
"3,119:5,76,119:14,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:16,77,119" +
":3,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:12,78,119:7,-1,119:4,-1:" +
"2,119,-1:21,119,-1:4,119:3,-1:3,119:9,79,119:10,-1,119:4,-1:2,119,-1:21,119" +
",-1:4,119:3,-1:3,119:20,-1,119:2,80,119,-1:2,119,-1:21,119,-1:4,119:3,-1:3," +
"119:8,81,119:11,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,83,119:19,-1,11" +
"9:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:19,84,-1,119:4,-1:2,119,-1:21,11" +
"9,-1:4,119:3,-1:3,119:4,85,119:15,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1" +
":3,119:9,86,119:10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,87,119" +
":16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:12,88,119:7,-1,119:4,-1" +
":2,119,-1:21,119,-1:4,119:3,-1:3,119:3,89,119:16,-1,119:4,-1:2,119,-1:21,11" +
"9,-1:4,119:3,-1:3,119:3,90,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1" +
":3,119:9,91,119:10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:9,92,119" +
":10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,93,119:19,-1,119:4,-1:2,119" +
",-1:21,119,-1:4,119:3,-1:3,119:9,94,119:10,-1,119:4,-1:2,119,-1:21,119,-1:4" +
",119:3,-1:3,119:8,95,119:11,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,96," +
"119:19,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119,97,119:18,-1,119:4,-" +
"1:2,119,-1:21,119,-1:4,119:3,-1:3,119:13,98,119:6,-1,119:4,-1:2,119,-1:21,1" +
"19,-1:4,119:3,-1:3,119:12,99,119:7,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-" +
"1:3,119:4,100,119:15,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,101," +
"119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,102,119:19,-1,119:4,-1:2" +
",119,-1:21,119,-1:4,119:3,-1:3,103,119:19,-1,119:4,-1:2,119,-1:21,119,-1:4," +
"119:3,-1:3,119:7,104,119:12,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119" +
":5,106,119:14,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:12,107,119:7," +
"-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,108,119:16,-1,119:4,-1:2," +
"119,-1:21,119,-1:4,119:3,-1:3,119,109,119:18,-1,119:4,-1:2,119,-1:21,119,-1" +
":4,119:3,-1:3,119:13,110,119:6,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3," +
"226,119:4,131,119,271,119,252,119,282,119:8,-1,119:4,-1:2,119,-1:21,119,-1:" +
"4,119:3,-1:3,119:3,133,119:3,253,184,119:8,227,119:2,-1,119:4,-1:2,119,-1:2" +
"1,119,-1:4,119:3,-1:3,119:3,135,119:4,254,119:8,187,119:2,-1,119:4,-1:2,119" +
",-1:21,119,-1:4,119:3,-1:3,119:4,191,119:3,228,119:2,278,119:5,137,119:2,-1" +
",119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:2,138,119:17,-1,119:4,-1:2,11" +
"9,-1:21,119,-1:4,119:3,-1:3,119:9,139,119:10,-1,119:4,-1:2,119,-1:21,119,-1" +
":4,119:3,-1:3,119:11,140,119:8,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3," +
"119:20,-1,119,141,119:2,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:19,142,-1,11" +
"9:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:13,143,119:6,-1,119:4,-1:2,119,-" +
"1:21,119,-1:4,119:3,-1:3,119:8,144,119:11,-1,119:4,-1:2,119,-1:21,119,-1:4," +
"119:3,-1:3,145,119:19,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,146" +
",119:2,199,119:13,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:12,147,11" +
"9:7,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:17,148,119:2,-1,119:4,-" +
"1:2,119,-1:21,119,-1:4,119:3,-1:3,119:20,-1,119,149,119:2,-1:2,119,-1:21,11" +
"9,-1:4,119:3,-1:3,119:12,150,119:7,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-" +
"1:3,119:3,151,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:8,152," +
"119:11,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:20,-1,119:2,153,119," +
"-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:20,-1,119,154,119:2,-1:2,119,-1:21,1" +
"19,-1:4,119:3,-1:3,119:4,155,119:15,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3," +
"-1:3,119:13,156,119:6,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:9,157" +
",119:10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:16,158,119:3,-1,119" +
":4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:11,159,119:8,-1,119:4,-1:2,119,-1" +
":21,119,-1:4,119:3,-1:3,119:4,160,119:15,-1,119:4,-1:2,119,-1:21,119,-1:4,1" +
"19:3,-1:3,119:3,161,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:" +
"5,162,119:14,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:16,163,119:3,-" +
"1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:5,164,119:14,-1,119:4,-1:2,1" +
"19,-1:21,119,-1:4,119:3,-1:3,119:8,165,119:11,-1,119:4,-1:2,119,-1:21,119,-" +
"1:4,119:3,-1:3,119:3,166,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3" +
",119:17,167,119:2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:5,168,119" +
":14,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:7,169,119:12,-1,119:4,-" +
"1:2,119,-1:21,119,-1:4,119:3,-1:3,119:7,170,119:12,-1,119:4,-1:2,119,-1:21," +
"119,-1:4,119:3,-1:3,119:6,171,119:13,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3" +
",-1:3,119:3,172,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:17,1" +
"73,119:2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,174,119:19,-1,119:4,-1" +
":2,119,-1:21,119,-1:4,119:3,-1:3,119:4,175,119:15,-1,119:4,-1:2,119,-1:21,1" +
"19,-1:4,119:3,-1:3,119:8,176,119:11,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3," +
"-1:3,119:3,185,119:7,186,119:8,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3," +
"119:3,255,119:2,188,119:9,189,119:3,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3," +
"-1:3,119:6,190,119:13,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:8,192" +
",119:11,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,279,119:2,193,119" +
":13,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,195,119:16,-1,119:4,-" +
"1:2,119,-1:21,119,-1:4,119:3,-1:3,119:17,196,119:2,-1,119:4,-1:2,119,-1:21," +
"119,-1:4,119:3,-1:3,119:13,197,119:6,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3" +
",-1:3,119:7,238,119:5,200,119:6,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3" +
",119:7,201,119:12,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:2,202,119" +
":17,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,203,119:16,-1,119:4,-" +
"1:2,119,-1:21,119,-1:4,119:3,-1:3,119:4,204,119:15,-1,119:4,-1:2,119,-1:21," +
"119,-1:4,119:3,-1:3,119:18,205,119,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-" +
"1:3,119:15,206,119:4,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,207," +
"119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:18,208,119,-1,119:4," +
"-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:5,209,119:14,-1,119:4,-1:2,119,-1:21" +
",119,-1:4,119:3,-1:3,210,119:19,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3" +
",119:11,211,119:8,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,212,119" +
":16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:3,213,119:16,-1,119:4,-" +
"1:2,119,-1:21,119,-1:4,119:3,-1:3,119:13,214,119:6,-1,119:4,-1:2,119,-1:21," +
"119,-1:4,119:3,-1:3,119:20,-1,119,215,119:2,-1:2,119,-1:21,119,-1:4,119:3,-" +
"1:3,119:6,216,119:13,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:8,217," +
"119:11,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:8,218,119:11,-1,119:" +
"4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:6,219,119:13,-1,119:4,-1:2,119,-1:" +
"21,119,-1:4,119:3,-1:3,119:4,224,119:15,-1,119:4,-1:2,119,-1:21,119,-1:4,11" +
"9:3,-1:3,119:13,225,119:3,251,119:2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3," +
"-1:3,119:17,229,119:2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:4,230" +
",119:8,261,119:6,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:9,231,119:" +
"10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:17,232,119:2,-1,119:4,-1" +
":2,119,-1:21,119,-1:4,119:3,-1:3,119:20,-1,119:2,233,119,-1:2,119,-1:21,119" +
",-1:4,119:3,-1:3,119:6,234,119:13,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1" +
":3,119:3,235,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:17,236," +
"119:2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:5,237,119:14,-1,119:4" +
",-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:13,239,119:6,-1,119:4,-1:2,119,-1:2" +
"1,119,-1:4,119:3,-1:3,119:2,240,119:17,-1,119:4,-1:2,119,-1:21,119,-1:4,119" +
":3,-1:3,119:13,241,119:6,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:7," +
"242,119:12,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:4,243,119:15,-1," +
"119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,244,119:19,-1,119:4,-1:2,119,-1:21" +
",119,-1:4,119:3,-1:3,119:7,245,119:12,-1,119:4,-1:2,119,-1:21,119,-1:4,119:" +
"3,-1:3,119:6,246,119:13,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,247,119" +
":19,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:2,250,119:17,-1,119:4,-" +
"1:2,119,-1:21,119,-1:4,119:3,-1:3,119:4,256,119:8,257,119:6,-1,119:4,-1:2,1" +
"19,-1:21,119,-1:4,119:3,-1:3,119:6,258,119:13,-1,119:4,-1:2,119,-1:21,119,-" +
"1:4,119:3,-1:3,119,262,119:18,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,1" +
"19:3,263,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:7,264,119:1" +
"2,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:6,265,119:13,-1,119:4,-1:" +
"2,119,-1:21,119,-1:4,119:3,-1:3,119:13,266,119:6,-1,119:4,-1:2,119,-1:21,11" +
"9,-1:4,119:3,-1:3,119:12,267,119:7,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-" +
"1:3,119:9,272,119:10,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:12,273" +
",119:7,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:5,274,119:14,-1,119:" +
"4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:6,275,119:13,-1,119:4,-1:2,119,-1:" +
"21,119,-1:4,119:3,-1:3,119:3,276,119:16,-1,119:4,-1:2,119,-1:21,119,-1:4,11" +
"9:3,-1:3,119:19,280,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:3,119:11,281," +
"119:8,-1,119:4,-1:2,119,-1:21,119,-1:4,119:3,-1:2");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

 	return (new Symbol(sym.EOF));
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -3:
						break;
					case 3:
						{ return symbol(sym.X); }
					case -4:
						break;
					case 4:
						{ return symbol(sym.DOT); }
					case -5:
						break;
					case 5:
						{ return symbol(sym.UNDERSCORE); }
					case -6:
						break;
					case 6:
						{ System.err.println("Illegal character: "+yytext()); }
					case -7:
						break;
					case 7:
						{ return symbol(sym.MINUS); }
					case -8:
						break;
					case 8:
						{ return symbol(sym.NUMBER, new Integer(yytext())); }
					case -9:
						break;
					case 9:
						{ return symbol(sym.SEMI); }
					case -10:
						break;
					case 10:
						{ return symbol(sym.COLON); }
					case -11:
						break;
					case 11:
						{ return symbol(sym.COMMA); }
					case -12:
						break;
					case 12:
						{ return symbol(sym.PLUS); }
					case -13:
						break;
					case 13:
						{ return symbol(sym.TIMES); }
					case -14:
						break;
					case 14:
						{ return symbol(sym.TILDE); }
					case -15:
						break;
					case 15:
						{ return symbol(sym.HASH); }
					case -16:
						break;
					case 16:
						{ return symbol(sym.AT); }
					case -17:
						break;
					case 17:
						{ return symbol(sym.LPAREN); }
					case -18:
						break;
					case 18:
						{ return symbol(sym.RPAREN); }
					case -19:
						break;
					case 19:
						{ return symbol(sym.LESSTHAN); }
					case -20:
						break;
					case 20:
						{ return symbol(sym.LSQPAREN); }
					case -21:
						break;
					case 21:
						{ return symbol(sym.RSQPAREN); }
					case -22:
						break;
					case 22:
						{ return symbol(sym.LCLPAREN); }
					case -23:
						break;
					case 23:
						{ return symbol(sym.RCLPAREN); }
					case -24:
						break;
					case 24:
						{ return symbol(sym.EXCL); }
					case -25:
						break;
					case 25:
						{ return symbol(sym.BAR);}
					case -26:
						break;
					case 26:
						{ return symbol(sym.ASSIGN); }
					case -27:
						break;
					case 27:
						{ return symbol(sym.AND); }
					case -28:
						break;
					case 28:
						{ return symbol(sym.DOLLAR); }
					case -29:
						break;
					case 29:
						{ return symbol(sym.GREATER); }
					case -30:
						break;
					case 30:
						{ /* ignore white space. */ }
					case -31:
						break;
					case 31:
						{ return symbol(sym.UPPERCASEID, yytext());}
					case -32:
						break;
					case 32:
						{ return symbol(sym.TO); }
					case -33:
						break;
					case 33:
						{ return symbol(sym.DO);}
					case -34:
						break;
					case 34:
						{ return symbol(sym.IN);}
					case -35:
						break;
					case 35:
						{ return symbol(sym.IF); }
					case -36:
						break;
					case 36:
						{ return symbol(sym.OF);}
					case -37:
						break;
					case 37:
						{ return symbol(sym.DOUBLEDOT); }
					case -38:
						break;
					case 38:
						{ return symbol(sym.DOUBLE, new Double(yytext())); }
					case -39:
						break;
					case 39:
						{ return symbol(sym.ARROW); }
					case -40:
						break;
					case 40:
						{ return symbol(sym.DOUBLESEMI); }
					case -41:
						break;
					case 41:
						{ return symbol(sym.RCLPARENPLUS); }
					case -42:
						break;
					case 42:
						{ return symbol(sym.RCLPARENSTAR); }
					case -43:
						break;
					case 43:
						{ return symbol(sym.CURLYARROW); }
					case -44:
						break;
					case 44:
						{ return symbol(sym.PUT); }
					case -45:
						break;
					case 45:
						{ return symbol(sym.LEQ); }
					case -46:
						break;
					case 46:
						{ return symbol(sym.LCLPARENPLUS); }
					case -47:
						break;
					case 47:
						{ return symbol(sym.LCLPARENSTAR); }
					case -48:
						break;
					case 48:
						{ return symbol(sym.LCLPARENEXCL); }
					case -49:
						break;
					case 49:
						{ return symbol(sym.RCLPARENEXCL); }
					case -50:
						break;
					case 50:
						{ return symbol(sym.NOTEQUALS); }
					case -51:
						break;
					case 51:
						{ return symbol(sym.DOUBLEBAR); }
					case -52:
						break;
					case 52:
						{ return symbol(sym.EQUALS); }
					case -53:
						break;
					case 53:
						{ return symbol(sym.IMPLIES); }
					case -54:
						break;
					case 54:
						{ return symbol(sym.DOUBLEAND); }
					case -55:
						break;
					case 55:
						{ return symbol(sym.ARGIDENT, yytext());}
					case -56:
						break;
					case 56:
						{ return symbol(sym.GEQ); }
					case -57:
						break;
					case 57:
						{
  yybegin(MLCOMMENT);
}
					case -58:
						break;
					case 58:
						{
  yybegin(COMMENT);
}
					case -59:
						break;
					case 59:
						{ return symbol(sym.STRING, yytext()); }
					case -60:
						break;
					case 60:
						{ return symbol(sym.STRING, "\"\"" + yytext().substring(1, yytext().length()-1) + "\"\""); }
					case -61:
						break;
					case 61:
						{ return symbol(sym.INF); }
					case -62:
						break;
					case 62:
						{ return symbol(sym.ADD); }
					case -63:
						break;
					case 63:
						{ return symbol(sym.SET); }
					case -64:
						break;
					case 64:
						{ return symbol(sym.LET); }
					case -65:
						break;
					case 65:
						{ return symbol(sym.ELLIPSIS); }
					case -66:
						break;
					case 66:
						{ return symbol(sym.FOR); }
					case -67:
						break;
					case 67:
						{ return symbol(sym.INVERT); }
					case -68:
						break;
					case 68:
						{ return symbol(sym.GEQCLOSURE); }
					case -69:
						break;
					case 69:
						{ return symbol(sym.EQUIVALENCE); }
					case -70:
						break;
					case 70:
						{ return symbol(sym.TYPE); }
					case -71:
						break;
					case 71:
						{ return symbol(sym.TEST); }
					case -72:
						break;
					case 72:
						{ return symbol(sym.TRUE);}
					case -73:
						break;
					case 73:
						{ return symbol(sym.SAVE); }
					case -74:
						break;
					case 74:
						{ return symbol(sym.JUMP); }
					case -75:
						break;
					case 75:
						{ return symbol(sym.NULL);}
					case -76:
						break;
					case 76:
						{ return symbol(sym.LOAD); }
					case -77:
						break;
					case 77:
						{ return symbol(sym.WITH);}
					case -78:
						break;
					case 78:
						{ return symbol(sym.WHEN);}
					case -79:
						break;
					case 79:
						{ return symbol(sym.BINS); }
					case -80:
						break;
					case 80:
						{ return symbol(sym.FROM); }
					case -81:
						break;
					case 81:
						{ return symbol(sym.JAVA);}
					case -82:
						break;
					case 82:
						{ return symbol(sym.TYPES); }
					case -83:
						break;
					case 83:
						{ return symbol(sym.PRINT); }
					case -84:
						break;
					case 84:
						{ return symbol(sym.INDEX); }
					case -85:
						break;
					case 85:
						{ return symbol(sym.CLEAR); }
					case -86:
						break;
					case 86:
						{ return symbol(sym.ATOMS); }
					case -87:
						break;
					case 87:
						{ return symbol(sym.SOLVE); }
					case -88:
						break;
					case 88:
						{ return symbol(sym.LEARN); }
					case -89:
						break;
					case 89:
						{ return symbol(sym.WHILE);}
					case -90:
						break;
					case 90:
						{ return symbol(sym.FALSE);}
					case -91:
						break;
					case 91:
						{ return symbol(sym.EPOCHS); }
					case -92:
						break;
					case 92:
						{ return symbol(sym.CORPUS); }
					case -93:
						break;
					case 93:
						{ return symbol(sym.ASSERT);}
					case -94:
						break;
					case 94:
						{ return symbol(sym.SCORES); }
					case -95:
						break;
					case 95:
						{ return symbol(sym.LAMBDA); }
					case -96:
						break;
					case 96:
						{ return symbol(sym.WEIGHT); }
					case -97:
						break;
					case 97:
						{ return symbol(sym.GREEDY); }
					case -98:
						break;
					case 98:
						{ return symbol(sym.GLOBAL); }
					case -99:
						break;
					case 99:
						{ return symbol(sym.HIDDEN); }
					case -100:
						break;
					case 100:
						{ return symbol(sym.FACTOR); }
					case -101:
						break;
					case 101:
						{ return symbol(sym.INCLUDE); }
					case -102:
						break;
					case 102:
						{ return symbol(sym.INSPECT); }
					case -103:
						break;
					case 103:
						{ return symbol(sym.COLLECT); }
					case -104:
						break;
					case 104:
						{ return symbol(sym.ACYCLIC); }
					case -105:
						break;
					case 105:
						{ return symbol(sym.WEIGHTS); }
					case -106:
						break;
					case 106:
						{ return symbol(sym.OBSERVED); }
					case -107:
						break;
					case 107:
						{ return symbol(sym.FUNCTION); }
					case -108:
						break;
					case 108:
						{ return symbol(sym.PREDICATE); }
					case -109:
						break;
					case 109:
						{ return symbol(sym.AUXILIARY); }
					case -110:
						break;
					case 110:
						{ return symbol(sym.SEQ); }
					case -111:
						break;
					case 111:
						{
}
					case -112:
						break;
					case 112:
						{
  yybegin(YYINITIAL);
}
					case -113:
						break;
					case 113:
						{
}
					case -114:
						break;
					case 114:
						{
  yybegin(MLMAYEND);
}
					case -115:
						break;
					case 115:
						{
}
					case -116:
						break;
					case 116:
						{
  yybegin(MLCOMMENT);
}
					case -117:
						break;
					case 117:
						{
  yybegin(YYINITIAL);
}
					case -118:
						break;
					case 119:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -119:
						break;
					case 120:
						{ System.err.println("Illegal character: "+yytext()); }
					case -120:
						break;
					case 122:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -121:
						break;
					case 123:
						{ System.err.println("Illegal character: "+yytext()); }
					case -122:
						break;
					case 125:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -123:
						break;
					case 126:
						{ System.err.println("Illegal character: "+yytext()); }
					case -124:
						break;
					case 128:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -125:
						break;
					case 129:
						{ System.err.println("Illegal character: "+yytext()); }
					case -126:
						break;
					case 131:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -127:
						break;
					case 133:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -128:
						break;
					case 135:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -129:
						break;
					case 137:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -130:
						break;
					case 138:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -131:
						break;
					case 139:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -132:
						break;
					case 140:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -133:
						break;
					case 141:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -134:
						break;
					case 142:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -135:
						break;
					case 143:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -136:
						break;
					case 144:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -137:
						break;
					case 145:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -138:
						break;
					case 146:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -139:
						break;
					case 147:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -140:
						break;
					case 148:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -141:
						break;
					case 149:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -142:
						break;
					case 150:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -143:
						break;
					case 151:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -144:
						break;
					case 152:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -145:
						break;
					case 153:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -146:
						break;
					case 154:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -147:
						break;
					case 155:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -148:
						break;
					case 156:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -149:
						break;
					case 157:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -150:
						break;
					case 158:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -151:
						break;
					case 159:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -152:
						break;
					case 160:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -153:
						break;
					case 161:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -154:
						break;
					case 162:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -155:
						break;
					case 163:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -156:
						break;
					case 164:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -157:
						break;
					case 165:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -158:
						break;
					case 166:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -159:
						break;
					case 167:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -160:
						break;
					case 168:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -161:
						break;
					case 169:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -162:
						break;
					case 170:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -163:
						break;
					case 171:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -164:
						break;
					case 172:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -165:
						break;
					case 173:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -166:
						break;
					case 174:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -167:
						break;
					case 175:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -168:
						break;
					case 176:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -169:
						break;
					case 177:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -170:
						break;
					case 178:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -171:
						break;
					case 179:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -172:
						break;
					case 180:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -173:
						break;
					case 181:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -174:
						break;
					case 182:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -175:
						break;
					case 183:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -176:
						break;
					case 184:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -177:
						break;
					case 185:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -178:
						break;
					case 186:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -179:
						break;
					case 187:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -180:
						break;
					case 188:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -181:
						break;
					case 189:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -182:
						break;
					case 190:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -183:
						break;
					case 191:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -184:
						break;
					case 192:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -185:
						break;
					case 193:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -186:
						break;
					case 194:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -187:
						break;
					case 195:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -188:
						break;
					case 196:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -189:
						break;
					case 197:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -190:
						break;
					case 198:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -191:
						break;
					case 199:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -192:
						break;
					case 200:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -193:
						break;
					case 201:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -194:
						break;
					case 202:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -195:
						break;
					case 203:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -196:
						break;
					case 204:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -197:
						break;
					case 205:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -198:
						break;
					case 206:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -199:
						break;
					case 207:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -200:
						break;
					case 208:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -201:
						break;
					case 209:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -202:
						break;
					case 210:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -203:
						break;
					case 211:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -204:
						break;
					case 212:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -205:
						break;
					case 213:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -206:
						break;
					case 214:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -207:
						break;
					case 215:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -208:
						break;
					case 216:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -209:
						break;
					case 217:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -210:
						break;
					case 218:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -211:
						break;
					case 219:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -212:
						break;
					case 220:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -213:
						break;
					case 221:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -214:
						break;
					case 222:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -215:
						break;
					case 223:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -216:
						break;
					case 224:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -217:
						break;
					case 225:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -218:
						break;
					case 226:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -219:
						break;
					case 227:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -220:
						break;
					case 228:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -221:
						break;
					case 229:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -222:
						break;
					case 230:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -223:
						break;
					case 231:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -224:
						break;
					case 232:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -225:
						break;
					case 233:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -226:
						break;
					case 234:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -227:
						break;
					case 235:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -228:
						break;
					case 236:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -229:
						break;
					case 237:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -230:
						break;
					case 238:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -231:
						break;
					case 239:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -232:
						break;
					case 240:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -233:
						break;
					case 241:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -234:
						break;
					case 242:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -235:
						break;
					case 243:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -236:
						break;
					case 244:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -237:
						break;
					case 245:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -238:
						break;
					case 246:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -239:
						break;
					case 247:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -240:
						break;
					case 248:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -241:
						break;
					case 249:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -242:
						break;
					case 250:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -243:
						break;
					case 251:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -244:
						break;
					case 252:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -245:
						break;
					case 253:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -246:
						break;
					case 254:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -247:
						break;
					case 255:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -248:
						break;
					case 256:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -249:
						break;
					case 257:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -250:
						break;
					case 258:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -251:
						break;
					case 259:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -252:
						break;
					case 260:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -253:
						break;
					case 261:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -254:
						break;
					case 262:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -255:
						break;
					case 263:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -256:
						break;
					case 264:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -257:
						break;
					case 265:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -258:
						break;
					case 266:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -259:
						break;
					case 267:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -260:
						break;
					case 268:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -261:
						break;
					case 269:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -262:
						break;
					case 270:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -263:
						break;
					case 271:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -264:
						break;
					case 272:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -265:
						break;
					case 273:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -266:
						break;
					case 274:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -267:
						break;
					case 275:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -268:
						break;
					case 276:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -269:
						break;
					case 277:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -270:
						break;
					case 278:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -271:
						break;
					case 279:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -272:
						break;
					case 280:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -273:
						break;
					case 281:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -274:
						break;
					case 282:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -275:
						break;
					case 283:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -276:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
