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
		129,
		131,
		133
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
		/* 117 */ YY_NOT_ACCEPT,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NOT_ACCEPT,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NOT_ACCEPT,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NOT_ACCEPT,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NOT_ACCEPT,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NOT_ACCEPT,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NOT_ACCEPT,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
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
		/* 275 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"52:9,60,53,52,54:2,52:18,60,44,58,35,48,52,47,59,37,38,33,32,31,27,21,51,56" +
",28,56:8,30,29,39,46,49,52,36,57:26,40,52,41,26,25,52,9,19,8,6,4,22,10,17,7" +
",50,55,15,24,14,18,3,12,5,11,1,13,23,16,20,2,55,42,45,43,34,52,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,276,
"0,1,2,3,4,1,5,6,7,8,1:2,9,10,11,1:4,12,1:2,13,1,14,15,16,17,18,19,1,20,3:2," +
"21,3:2,22,23,1:6,24,1:9,18,1:4,3:5,1,3,1:3,25,3:27,26,3:11,1:7,27,3,28,23,2" +
"9,1,30,31,30,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52" +
",53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77" +
",78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101," +
"102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120" +
",121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,13" +
"9,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,1" +
"58,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176," +
"177,178,179,180,181")[0];

	private int yy_nxt[][] = unpackFromString(182,61,
"1,2,118,244,263,118,121,124,245,175,264,176,118:2,217,177,218,265,127,219,3" +
",4,178,118:2,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,2" +
"7,28,29,220,119,122,30:2,118,8,31,125,128,30,-1:62,118,179,118,180,181,118:" +
"12,32,118:2,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:20,-1,118:4,-1:" +
"2,118,-1:21,118,-1:4,118:3,-1:24,37,-1:6,38,-1:27,38,-1:31,117,-1:82,39,-1:" +
"32,120,-1:6,8,-1:27,8,-1:33,40,-1:74,41,-1:60,42,-1:66,43,-1:38,44,-1:18,45" +
",-1:46,46,47,-1:10,48,-1:59,49,-1:2,50,-1:59,51,-1:61,52,-1:2,53,-1:58,54,-" +
"1:14,55:20,-1,55:4,-1:2,55,-1:21,55,-1:4,55:3,-1:49,56,-1:15,31:20,-1,31:4," +
"-1:2,31,-1:21,31,-1:4,31:3,-1:4,118:5,192,118,255,118:2,256,118:9,-1,60,118" +
":3,-1:2,118,-1:21,118,-1:4,118:3,-1:24,65,-1:67,38,-1:27,38,-1:53,68,-1:12," +
"118:10,82,118:9,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:10,105,118:" +
"9,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:31,67,-1:65,57,-1:17,58,-1:10,1" +
"18:17,33,118:2,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,123:57,59,123:2," +
"-1,118:13,34,118:6,-1,35,118:3,-1:2,118,-1:21,118,-1:4,118:3,-1:4,126:24,-1" +
":25,126,-1:4,126,-1,126,-1,69,126,-1,118:18,271,118,-1,36,118:3,-1:2,118,-1" +
":21,118,-1:4,118:3,-1:4,126:24,-1:25,126,-1:4,126,-1,126,-1:2,126,1,110:52," +
"111,110:7,-1,118:9,61,118:10,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:3,1," +
"112:32,113,112:19,114:2,112:5,114,-1,118:5,62,118:14,-1,118:4,-1:2,118,-1:2" +
"1,118,-1:4,118:3,-1:3,1,115:50,116,115:9,-1,63,118:10,275,118:8,-1,118:4,-1" +
":2,118,-1:21,118,-1:4,118:3,-1:4,64,118:7,196,118:11,-1,118:4,-1:2,118,-1:2" +
"1,118,-1:4,118:3,-1:4,118:4,66,118:15,-1,118:4,-1:2,118,-1:21,118,-1:4,118:" +
"3,-1:4,118:3,70,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,71,118:1" +
"9,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,72,118:16,-1,118:4,-1:2" +
",118,-1:21,118,-1:4,118:3,-1:4,118:3,73,118:16,-1,118:4,-1:2,118,-1:21,118," +
"-1:4,118:3,-1:4,74,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:1" +
"4,75,118:5,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:5,76,118:14,-1,1" +
"18:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:16,77,118:3,-1,118:4,-1:2,118,-" +
"1:21,118,-1:4,118:3,-1:4,118:13,78,118:6,-1,118:4,-1:2,118,-1:21,118,-1:4,1" +
"18:3,-1:4,118:10,79,118:9,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:2" +
"0,-1,118:2,80,118,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:8,81,118:11,-1,118" +
":4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,83,118:19,-1,118:4,-1:2,118,-1:21,118" +
",-1:4,118:3,-1:4,118:19,84,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:" +
"4,85,118:15,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:10,86,118:9,-1," +
"118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,87,118:16,-1,118:4,-1:2,118," +
"-1:21,118,-1:4,118:3,-1:4,118:13,88,118:6,-1,118:4,-1:2,118,-1:21,118,-1:4," +
"118:3,-1:4,118:3,89,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:" +
"3,90,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:10,91,118:9,-1," +
"118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:10,92,118:9,-1,118:4,-1:2,118," +
"-1:21,118,-1:4,118:3,-1:4,93,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3," +
"-1:4,118,94,118:18,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:14,95,11" +
"8:5,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:10,96,118:9,-1,118:4,-1" +
":2,118,-1:21,118,-1:4,118:3,-1:4,118:8,97,118:11,-1,118:4,-1:2,118,-1:21,11" +
"8,-1:4,118:3,-1:4,98,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118" +
":13,99,118:6,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:4,100,118:15,-" +
"1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,101,118:16,-1,118:4,-1:2,1" +
"18,-1:21,118,-1:4,118:3,-1:4,102,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,11" +
"8:3,-1:4,103,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:7,104,1" +
"18:12,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:5,106,118:14,-1,118:4" +
",-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:13,107,118:6,-1,118:4,-1:2,118,-1:2" +
"1,118,-1:4,118:3,-1:4,118:3,108,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118" +
":3,-1:4,118:14,109,118:5,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,223,11" +
"8:3,130,132,118,266,118:2,248,118:9,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3," +
"-1:4,118:3,134,118:3,251,182,118:8,224,118:2,-1,118:4,-1:2,118,-1:21,118,-1" +
":4,118:3,-1:4,118:3,135,118:4,252,118:8,185,118:2,-1,118:4,-1:2,118,-1:21,1" +
"18,-1:4,118:3,-1:4,118:4,189,118:3,225,118:3,272,118:4,136,118:2,-1,118:4,-" +
"1:2,118,-1:21,118,-1:4,118:3,-1:4,118:2,137,118:17,-1,118:4,-1:2,118,-1:21," +
"118,-1:4,118:3,-1:4,118:10,138,118:9,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3" +
",-1:4,118:12,139,118:7,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:20,-" +
"1,118,140,118:2,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:19,141,-1,118:4,-1:2" +
",118,-1:21,118,-1:4,118:3,-1:4,118:14,142,118:5,-1,118:4,-1:2,118,-1:21,118" +
",-1:4,118:3,-1:4,118:8,143,118:11,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1" +
":4,144,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,145,118:2,1" +
"97,118:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:13,146,118:6,-1,1" +
"18:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:17,147,118:2,-1,118:4,-1:2,118," +
"-1:21,118,-1:4,118:3,-1:4,118:20,-1,118,148,118:2,-1:2,118,-1:21,118,-1:4,1" +
"18:3,-1:4,118:13,149,118:6,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:" +
"3,150,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:8,151,118:11,-" +
"1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:20,-1,118:2,152,118,-1:2,118" +
",-1:21,118,-1:4,118:3,-1:4,118:20,-1,118,153,118:2,-1:2,118,-1:21,118,-1:4," +
"118:3,-1:4,118:4,154,118:15,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118" +
":14,155,118:5,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:10,156,118:9," +
"-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:16,157,118:3,-1,118:4,-1:2," +
"118,-1:21,118,-1:4,118:3,-1:4,118:12,158,118:7,-1,118:4,-1:2,118,-1:21,118," +
"-1:4,118:3,-1:4,118:4,159,118:15,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:" +
"4,118:5,160,118:14,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:8,161,11" +
"8:11,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,162,118:16,-1,118:4," +
"-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:5,163,118:14,-1,118:4,-1:2,118,-1:21" +
",118,-1:4,118:3,-1:4,118:16,164,118:3,-1,118:4,-1:2,118,-1:21,118,-1:4,118:" +
"3,-1:4,118:3,165,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:17," +
"166,118:2,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:5,167,118:14,-1,1" +
"18:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:7,168,118:12,-1,118:4,-1:2,118," +
"-1:21,118,-1:4,118:3,-1:4,118:7,169,118:12,-1,118:4,-1:2,118,-1:21,118,-1:4" +
",118:3,-1:4,118:6,170,118:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,11" +
"8:3,171,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:17,172,118:2" +
",-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,173,118:19,-1,118:4,-1:2,118,-" +
"1:21,118,-1:4,118:3,-1:4,118:8,174,118:11,-1,118:4,-1:2,118,-1:21,118,-1:4," +
"118:3,-1:4,118:3,183,118:8,184,118:7,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3" +
",-1:4,118:3,253,118:2,186,118:9,187,118:3,-1,118:4,-1:2,118,-1:21,118,-1:4," +
"118:3,-1:4,118:6,188,118:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118" +
":8,190,118:11,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,273,118:2,1" +
"91,118:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,193,118:16,-1,1" +
"18:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:17,194,118:2,-1,118:4,-1:2,118," +
"-1:21,118,-1:4,118:3,-1:4,118:14,195,118:5,-1,118:4,-1:2,118,-1:21,118,-1:4" +
",118:3,-1:4,118:7,235,118:6,198,118:5,-1,118:4,-1:2,118,-1:21,118,-1:4,118:" +
"3,-1:4,118:7,199,118:12,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:2,2" +
"00,118:17,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,201,118:16,-1,1" +
"18:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,202,118:16,-1,118:4,-1:2,118," +
"-1:21,118,-1:4,118:3,-1:4,118:18,203,118,-1,118:4,-1:2,118,-1:21,118,-1:4,1" +
"18:3,-1:4,118:4,204,118:15,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:" +
"18,205,118,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:9,206,118:10,-1," +
"118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:5,207,118:14,-1,118:4,-1:2,118" +
",-1:21,118,-1:4,118:3,-1:4,208,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,118:" +
"3,-1:4,118:12,209,118:7,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,2" +
"10,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,211,118:16,-1,1" +
"18:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:14,212,118:5,-1,118:4,-1:2,118," +
"-1:21,118,-1:4,118:3,-1:4,118:20,-1,118,213,118:2,-1:2,118,-1:21,118,-1:4,1" +
"18:3,-1:4,118:6,214,118:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:" +
"8,215,118:11,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:6,216,118:13,-" +
"1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:4,221,118:15,-1,118:4,-1:2,1" +
"18,-1:21,118,-1:4,118:3,-1:4,118:14,222,118:2,247,118:2,-1,118:4,-1:2,118,-" +
"1:21,118,-1:4,118:3,-1:4,118:17,226,118:2,-1,118:4,-1:2,118,-1:21,118,-1:4," +
"118:3,-1:4,118:4,227,118:9,257,118:5,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3" +
",-1:4,118:10,228,118:9,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:3,22" +
"9,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:17,230,118:2,-1,11" +
"8:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:17,231,118:2,-1,118:4,-1:2,118,-" +
"1:21,118,-1:4,118:3,-1:4,118:20,-1,118:2,232,118,-1:2,118,-1:21,118,-1:4,11" +
"8:3,-1:4,118:6,233,118:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:5" +
",234,118:14,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:14,236,118:5,-1" +
",118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:2,237,118:17,-1,118:4,-1:2,11" +
"8,-1:21,118,-1:4,118:3,-1:4,118:14,238,118:5,-1,118:4,-1:2,118,-1:21,118,-1" +
":4,118:3,-1:4,118:7,239,118:12,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4," +
"118:4,240,118:15,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,241,118:19,-1," +
"118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:7,242,118:12,-1,118:4,-1:2,118" +
",-1:21,118,-1:4,118:3,-1:4,243,118:19,-1,118:4,-1:2,118,-1:21,118,-1:4,118:" +
"3,-1:4,118:2,246,118:17,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:4,2" +
"49,118:9,250,118:5,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:6,254,11" +
"8:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118,258,118:18,-1,118:4,-1" +
":2,118,-1:21,118,-1:4,118:3,-1:4,118:3,259,118:16,-1,118:4,-1:2,118,-1:21,1" +
"18,-1:4,118:3,-1:4,118:7,260,118:12,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3," +
"-1:4,118:6,261,118:13,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:13,26" +
"2,118:6,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:10,267,118:9,-1,118" +
":4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:13,268,118:6,-1,118:4,-1:2,118,-1" +
":21,118,-1:4,118:3,-1:4,118:5,269,118:14,-1,118:4,-1:2,118,-1:21,118,-1:4,1" +
"18:3,-1:4,118:3,270,118:16,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:4,118:" +
"12,274,118:7,-1,118:4,-1:2,118,-1:21,118,-1:4,118:3,-1:3");

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
						{ return symbol(sym.INF); }
					case -61:
						break;
					case 61:
						{ return symbol(sym.ARG); }
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
						{ return symbol(sym.EQUIVALENCE); }
					case -69:
						break;
					case 69:
						{ return symbol(sym.JAVAID, yytext()); }
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
						{ return symbol(sym.GREEDY); }
					case -95:
						break;
					case 95:
						{ return symbol(sym.GLOBAL); }
					case -96:
						break;
					case 96:
						{ return symbol(sym.SCORES); }
					case -97:
						break;
					case 97:
						{ return symbol(sym.LAMBDA); }
					case -98:
						break;
					case 98:
						{ return symbol(sym.WEIGHT); }
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
						{ return symbol(sym.SEQ); }
					case -110:
						break;
					case 110:
						{
}
					case -111:
						break;
					case 111:
						{
  yybegin(YYINITIAL);
}
					case -112:
						break;
					case 112:
						{
}
					case -113:
						break;
					case 113:
						{
  yybegin(MLMAYEND);
}
					case -114:
						break;
					case 114:
						{
}
					case -115:
						break;
					case 115:
						{
  yybegin(MLCOMMENT);
}
					case -116:
						break;
					case 116:
						{
  yybegin(YYINITIAL);
}
					case -117:
						break;
					case 118:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -118:
						break;
					case 119:
						{ System.err.println("Illegal character: "+yytext()); }
					case -119:
						break;
					case 121:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -120:
						break;
					case 122:
						{ System.err.println("Illegal character: "+yytext()); }
					case -121:
						break;
					case 124:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -122:
						break;
					case 125:
						{ System.err.println("Illegal character: "+yytext()); }
					case -123:
						break;
					case 127:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -124:
						break;
					case 128:
						{ System.err.println("Illegal character: "+yytext()); }
					case -125:
						break;
					case 130:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -126:
						break;
					case 132:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -127:
						break;
					case 134:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -128:
						break;
					case 135:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -129:
						break;
					case 136:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -130:
						break;
					case 137:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -131:
						break;
					case 138:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -132:
						break;
					case 139:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -133:
						break;
					case 140:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -134:
						break;
					case 141:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -135:
						break;
					case 142:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -136:
						break;
					case 143:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -137:
						break;
					case 144:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -138:
						break;
					case 145:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -139:
						break;
					case 146:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -140:
						break;
					case 147:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -141:
						break;
					case 148:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -142:
						break;
					case 149:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -143:
						break;
					case 150:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -144:
						break;
					case 151:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -145:
						break;
					case 152:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -146:
						break;
					case 153:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -147:
						break;
					case 154:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -148:
						break;
					case 155:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -149:
						break;
					case 156:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -150:
						break;
					case 157:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -151:
						break;
					case 158:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -152:
						break;
					case 159:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -153:
						break;
					case 160:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -154:
						break;
					case 161:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -155:
						break;
					case 162:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -156:
						break;
					case 163:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -157:
						break;
					case 164:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -158:
						break;
					case 165:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -159:
						break;
					case 166:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -160:
						break;
					case 167:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -161:
						break;
					case 168:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -162:
						break;
					case 169:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -163:
						break;
					case 170:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -164:
						break;
					case 171:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -165:
						break;
					case 172:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -166:
						break;
					case 173:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -167:
						break;
					case 174:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -168:
						break;
					case 175:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -169:
						break;
					case 176:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -170:
						break;
					case 177:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -171:
						break;
					case 178:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -172:
						break;
					case 179:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -173:
						break;
					case 180:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -174:
						break;
					case 181:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -175:
						break;
					case 182:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -176:
						break;
					case 183:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -177:
						break;
					case 184:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -178:
						break;
					case 185:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -179:
						break;
					case 186:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -180:
						break;
					case 187:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -181:
						break;
					case 188:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -182:
						break;
					case 189:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -183:
						break;
					case 190:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -184:
						break;
					case 191:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -185:
						break;
					case 192:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -186:
						break;
					case 193:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -187:
						break;
					case 194:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -188:
						break;
					case 195:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -189:
						break;
					case 196:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -190:
						break;
					case 197:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -191:
						break;
					case 198:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -192:
						break;
					case 199:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -193:
						break;
					case 200:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -194:
						break;
					case 201:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -195:
						break;
					case 202:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -196:
						break;
					case 203:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -197:
						break;
					case 204:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -198:
						break;
					case 205:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -199:
						break;
					case 206:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -200:
						break;
					case 207:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -201:
						break;
					case 208:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -202:
						break;
					case 209:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -203:
						break;
					case 210:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -204:
						break;
					case 211:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -205:
						break;
					case 212:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -206:
						break;
					case 213:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -207:
						break;
					case 214:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -208:
						break;
					case 215:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -209:
						break;
					case 216:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -210:
						break;
					case 217:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -211:
						break;
					case 218:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -212:
						break;
					case 219:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -213:
						break;
					case 220:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -214:
						break;
					case 221:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -215:
						break;
					case 222:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -216:
						break;
					case 223:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -217:
						break;
					case 224:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -218:
						break;
					case 225:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -219:
						break;
					case 226:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -220:
						break;
					case 227:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -221:
						break;
					case 228:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -222:
						break;
					case 229:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -223:
						break;
					case 230:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -224:
						break;
					case 231:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -225:
						break;
					case 232:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -226:
						break;
					case 233:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -227:
						break;
					case 234:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -228:
						break;
					case 235:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -229:
						break;
					case 236:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -230:
						break;
					case 237:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -231:
						break;
					case 238:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -232:
						break;
					case 239:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -233:
						break;
					case 240:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -234:
						break;
					case 241:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -235:
						break;
					case 242:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -236:
						break;
					case 243:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -237:
						break;
					case 244:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -238:
						break;
					case 245:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -239:
						break;
					case 246:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -240:
						break;
					case 247:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -241:
						break;
					case 248:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -242:
						break;
					case 249:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -243:
						break;
					case 250:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -244:
						break;
					case 251:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -245:
						break;
					case 252:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -246:
						break;
					case 253:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -247:
						break;
					case 254:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -248:
						break;
					case 255:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -249:
						break;
					case 256:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -250:
						break;
					case 257:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -251:
						break;
					case 258:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -252:
						break;
					case 259:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -253:
						break;
					case 260:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -254:
						break;
					case 261:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -255:
						break;
					case 262:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -256:
						break;
					case 263:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -257:
						break;
					case 264:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -258:
						break;
					case 265:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -259:
						break;
					case 266:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -260:
						break;
					case 267:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -261:
						break;
					case 268:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -262:
						break;
					case 269:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -263:
						break;
					case 270:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -264:
						break;
					case 271:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -265:
						break;
					case 272:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -266:
						break;
					case 273:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -267:
						break;
					case 274:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -268:
						break;
					case 275:
						{ return symbol(sym.LOWERCASEID, yytext());}
					case -269:
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