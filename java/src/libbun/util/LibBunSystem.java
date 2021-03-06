// ***************************************************************************
// Copyright (c) 2013-2014, Libbun project authors. All rights reserved.
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// *  Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
// *  Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
// TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
// OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
// OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// **************************************************************************

// LangBase is a language-dependent code used in Zen.java

package libbun.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import libbun.ast.BNode;
import libbun.encode.LibBunGenerator;
import libbun.encode.LibBunSourceBuilder;
import libbun.encode.jvm.JavaTypeTable;
import libbun.encode.playground.BunGenerator;
import libbun.lang.bun.BunTypeSafer;
import libbun.parser.BSourceContext;
import libbun.parser.BTokenContext;
import libbun.parser.LibBunGamma;
import libbun.parser.LibBunTokenFuncChain;
import libbun.type.BType;


public class LibBunSystem {

	public final static void _Print(Object msg) {
		System.err.print(msg);
	}

	public final static void _PrintLine(Object msg) {
		System.err.println(msg);
	}

	@BIgnored private final static String _GetStackInfo(int depth) {
		String LineNumber = " ";
		Exception e =  new Exception();
		StackTraceElement[] Elements = e.getStackTrace();
		if(depth < Elements.length) {
			StackTraceElement elem = Elements[depth];
			LineNumber += elem;
		}
		return LineNumber;
	}

	public static boolean DebugMode = false;

	@BIgnored public final static void _SetDebugVerbose(boolean Mode) {
		DebugMode = true;
	}

	public final static void _PrintDebug(String msg) {
		if(DebugMode) {
			LibBunSystem._PrintLine("DEBUG " + LibBunSystem._GetStackInfo(3) + ": " + msg);
		}
	}

	public final static void DebugP(String msg) {
		//if(LibZen.DebugMode) {
		_PrintLine("DEBUG " + LibBunSystem._GetStackInfo(2) + ": " + msg);
		//}
	}

	@BIgnored public final static void _FixMe(Exception e) {
		if(DebugMode) {
			System.err.println("FIXME " + LibBunSystem._GetStackInfo(3) + ": " + e);
			e.printStackTrace();
		}
	}

	public final static void _Assert(boolean TestResult) {
		if (!TestResult) {
			assert TestResult;
			throw new RuntimeException("ASSERTION FAILED");
		}
	}

	public final static void _Exit(int status, String Message) {
		System.err.println("EXIT " + LibBunSystem._GetStackInfo(3) + " " + Message);
		System.exit(status);
	}

	public final static String _GetPlatform() {
		return "Java JVM-" + System.getProperty("java.version");
	}

	public final static String _GetEnv(String Name) {
		return System.getenv(Name);
	}


	public final static boolean _IsFlag(int flag, int flag2) {
		return ((flag & flag2) == flag2);
	}

	public final static int _UnsetFlag(int flag, int flag2) {
		return (flag & (~flag2));
	}

	// String Handling

	//	private final static int	NullChar				= 0;
	//	private final static int	UndefinedChar			= 1;
	private final static int	DigitChar				= 2;
	private final static int	UpperAlphaChar			= 3;
	private final static int	LowerAlphaChar			= 4;
	private final static int	UnderBarChar			= 5;
	private final static int	NewLineChar				= 6;
	private final static int	TabChar					= 7;
	private final static int	SpaceChar				= 8;
	private final static int	OpenParChar				= 9;
	private final static int	CloseParChar			= 10;
	private final static int	OpenBracketChar			= 11;
	private final static int	CloseBracketChar		= 12;
	private final static int	OpenBraceChar			= 13;
	private final static int	CloseBraceChar			= 14;
	private final static int	LessThanChar			= 15;
	private final static int	GreaterThanChar			= 16;
	private final static int	QuoteChar				= 17;
	private final static int	DoubleQuoteChar			= 18;
	private final static int	BackQuoteChar			= 19;
	private final static int	SurprisedChar			= 20;
	private final static int	SharpChar				= 21;
	private final static int	DollarChar				= 22;
	private final static int	PercentChar				= 23;
	private final static int	AndChar					= 24;
	private final static int	StarChar				= 25;
	private final static int	PlusChar				= 26;
	private final static int	CommaChar				= 27;
	private final static int	MinusChar				= 28;
	private final static int	DotChar					= 29;
	private final static int	SlashChar				= 30;
	private final static int	ColonChar				= 31;
	private final static int	SemiColonChar			= 32;
	private final static int	EqualChar				= 33;
	private final static int	QuestionChar			= 34;
	private final static int	AtmarkChar				= 35;
	private final static int	VarChar					= 36;
	private final static int	ChilderChar				= 37;
	private final static int	BackSlashChar			= 38;
	private final static int	HatChar					= 39;
	private final static int	UnicodeChar				= 40;
	private final static int MaxSizeOfChars          = 41;

	private final static int[]	CharMatrix = /*BeginArray*/{
		/*nul soh stx etx eot enq ack bel*/
		0, 1, 1, 1, 1, 1, 1, 1,
		/*bs ht nl vt np cr so si  */
		1, TabChar, NewLineChar, 1, 1, NewLineChar, 1, 1,
		/*020 dle  021 dc1  022 dc2  023 dc3  024 dc4  025 nak  026 syn  027 etb */
		1, 1, 1, 1, 1, 1, 1, 1,
		/*030 can  031 em   032 sub  033 esc  034 fs   035 gs   036 rs   037 us */
		1, 1, 1, 1, 1, 1, 1, 1,
		/*040 sp   041  !   042  "   043  #   044  $   045  %   046  &   047  ' */
		SpaceChar, SurprisedChar, DoubleQuoteChar, SharpChar, DollarChar, PercentChar, AndChar, QuoteChar,
		/*050  (   051  )   052  *   053  +   054  ,   055  -   056  .   057  / */
		OpenParChar, CloseParChar, StarChar, PlusChar, CommaChar, MinusChar, DotChar, SlashChar,
		/*060  0   061  1   062  2   063  3   064  4   065  5   066  6   067  7 */
		DigitChar, DigitChar, DigitChar, DigitChar, DigitChar, DigitChar, DigitChar, DigitChar,
		/*070  8   071  9   072  :   073  ;   074  <   075  =   076  >   077  ? */
		DigitChar, DigitChar, ColonChar, SemiColonChar, LessThanChar, EqualChar, GreaterThanChar, QuestionChar,
		/*100  @   101  A   102  B   103  C   104  D   105  E   106  F   107  G */
		AtmarkChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar,
		/*110  H   111  I   112  J   113  K   114  L   115  M   116  N   117  O */
		UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar,
		/*120  P   121  Q   122  R   123  S   124  T   125  U   126  V   127  W */
		UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, UpperAlphaChar,
		/*130  X   131  Y   132  Z   133  [   134  \   135  ]   136  ^   137  _ */
		UpperAlphaChar, UpperAlphaChar, UpperAlphaChar, OpenBracketChar, BackSlashChar, CloseBracketChar, HatChar, UnderBarChar,
		/*140  `   141  a   142  b   143  c   144  d   145  e   146  f   147  g */
		BackQuoteChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar,
		/*150  h   151  i   152  j   153  k   154  l   155  m   156  n   157  o */
		LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar,
		/*160  p   161  q   162  r   163  s   164  t   165  u   166  v   167  w */
		LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, LowerAlphaChar,
		/*170  x   171  y   172  z   173  {   174  |   175  }   176  ~   177 del*/
		LowerAlphaChar, LowerAlphaChar, LowerAlphaChar, OpenBraceChar, VarChar, CloseBraceChar, ChilderChar, 1,
	/*EndArray*/};

	public final static int _GetTokenMatrixIndex(char c) {
		if(c < 128) {
			return CharMatrix[c];
		}
		return UnicodeChar;
	}

	public final static LibBunTokenFuncChain[] _NewTokenMatrix() {
		return new LibBunTokenFuncChain[MaxSizeOfChars];
	}


	public final static char _GetChar(String Text, int Pos) {
		return Text.charAt(Pos);
	}

	public final static boolean _IsLetter(char ch) {
		return Character.isLetter(ch);
	}

	public final static boolean _IsDigit(char ch) {
		return Character.isDigit(ch);
	}

	public final static boolean _IsSymbol(char ch) {
		return Character.isLetter(ch) || ch == '_' || ch > 255;
	}

	public final static boolean _IsDigitOrLetter(char ch) {
		return _IsDigit(ch) || _IsLetter(ch);
	}

	//	public final static boolean _EqualsString(String s, String s2) {
	//		return s.equals(s2);
	//	}
	//
	//	public final static String _SubString(String Text, long StartIdx, long EndIdx) {
	//		return Text.substring((int)StartIdx, (int)EndIdx);
	//	}

	public final static String _JoinStrings(String Unit, int Times) {
		@Var String s = "";
		@Var int i = 0;
		while(i < Times) {
			s = s + Unit;
			i = i + 1;
		}
		return s;
	}

	public static final String _UnquoteString(String Text) {
		StringBuilder sb = new StringBuilder();
		@Var char quote = _GetChar(Text, 0);
		@Var int i = 0;
		@Var int Length = Text.length();
		if(quote == '"' || quote == '\'') {
			i = 1;
			Length -= 1;
		}
		else {
			quote = '\0';
		}
		while(i < Length) {
			@Var char ch = _GetChar(Text, i);
			if(ch == '\\') {
				i = i + 1;
				char next = _GetChar(Text, i);
				switch (next) {
				case 't':
					ch = '\t';
					break;
				case 'n':
					ch = '\n';
					break;
				case '"':
					ch = '"';
					break;
				case '\'':
					ch = '\'';
					break;
				case '\\':
					ch = '\\';
					break;
				default:
					ch = next;
					break;
				}
			}
			sb.append(ch);
			i = i + 1;
		}
		return sb.toString();
	}

	public static final String _QuoteString(String Text) {
		StringBuilder sb = new StringBuilder();
		sb.append('"');
		@Var int i = 0;
		for(; i < Text.length(); i = i + 1) {
			@Var char ch = _GetChar(Text, i);
			if(ch == '\n') {
				sb.append("\\n");
			}
			else if(ch == '\t') {
				sb.append("\\t");
			}
			else if(ch == '"') {
				sb.append("\\\"");
			}
			else if(ch == '\\') {
				sb.append("\\\\");
			}
			else {
				sb.append(ch);
			}
		}
		sb.append('"');
		return sb.toString();
	}

	public final static long _ParseInt(String Text) {
		try {
			return Long.parseLong(Text);
		}
		catch(NumberFormatException e) {
			//ZLogger.VerboseException(e);
		}
		return 0L;
	}

	public final static double _ParseFloat(String Text) {
		try {
			return Double.parseDouble(Text);
		}
		catch(NumberFormatException e) {
			//ZLogger.VerboseException(e);
		}
		return 0.0;
	}

	public final static String _Stringify(Object Value) {
		if(Value == null) {
			return "null";
		}
		else if(Value instanceof String) {
			return _QuoteString(Value.toString());
		}
		else {
			return Value.toString();
		}
	}

	public final static String _AnotherName(String s) {
		@Var char ch = s.charAt(0);
		if(Character.isUpperCase(ch)) {
			ch = Character.toLowerCase(ch);
		}
		else {
			ch = Character.toUpperCase(ch);
		}
		return String.valueOf(ch) + s.substring(1);
	}

	private final static String[] StringMatrix = {
		"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "0", "4",
		"a", "s", "d", "f", "g", "h", "j", "k", "l", "9", "1", "6",
		"z", "x", "c", "v", "b", "n", "m", "7", "5", "3", "2", "8",
	};

	public final static String _Stringfy(int number) {
		int d = number % StringMatrix.length;
		number = number / StringMatrix.length;
		int c = number % StringMatrix.length;
		number = number / StringMatrix.length;
		return StringMatrix[number] + StringMatrix[c] + StringMatrix[d];
	}

	public final static <T> int _Size(T[] List) {
		if(List == null) {
			return 0;
		}
		return List.length;
	}

	public final static <T> int _Size(BArray<?> List) {
		if(List == null) {
			return 0;
		}
		return List.size();
	}

	public final static boolean HasFile(String Path) {
		if(LibBunSystem.class.getResource(Path) != null) {
			return true;
		}
		return new File(Path).exists();
	}

	final static String FormatFilePath(String FileName) {
		String Path = FileName;
		if(HasFile(Path)) {
			return Path;
		}
		String Home = System.getProperty("GREENTEA_HOME");
		if(Home != null) {
			Path = Home + FileName;
		}
		if(HasFile(Path)) {
			return Path;
		}
		return FileName;
	}

	public final static String _LoadTextFile(String FileName) {
		//ZLogger.VerboseLog(ZLogger.VerboseFile, "loading " + FileName);
		InputStream Stream = LibBunSystem.class.getResourceAsStream("/" + FileName);
		if (Stream == null) {
			File f = new File(LibBunSystem.FormatFilePath(FileName));
			try {
				Stream = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(Stream));
		String buffer = "";
		try {
			int buflen = 4096;
			int readed = 0;
			char[] buf = new char[buflen];
			StringBuilder builder = new StringBuilder();
			while ((readed = reader.read(buf, 0, buflen)) >= 0) {
				builder.append(buf, 0, readed);
			}
			buffer = builder.toString();
		} catch (IOException e) {
			return null;
		}
		return buffer;
	}

	public static void _LoadInlineLibrary(String Path, BunMap<String> SymbolMap, String Delim) {
		try {
			InputStream Stream = LibBunSystem.class.getResourceAsStream("/" + Path);
			if (Stream == null) {
				File f = new File(LibBunSystem.FormatFilePath(Path));
				Stream = new FileInputStream(f);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(Stream));
			String line;
			String Key = null;
			String Required = null;
			StringBuilder sb = null;
			while((line = reader.readLine()) != null) {
				if(line.startsWith(Delim)) {
					if(Key != null && sb != null) {
						//System.out.println("'"+Key+"': \n" + sb.toString());
						SymbolMap.put(Key, sb.toString());
						if(Required != null) {
							SymbolMap.put(Key+";", Required);
						}
					}
					Key = line.substring(Delim.length()).trim();
					Required = null;
					int loc = Key.indexOf(';');
					if(loc > 0) {
						Required = Key.substring(loc+1);
						Key = Key.substring(0, loc);
					}
					sb = new StringBuilder();
					continue;
				}
				if(sb != null) {
					sb.append(line);
					sb.append("\n");
				}
			}
			if(Key != null && sb != null) {
				//System.out.println("'"+Key+"': \n" + sb.toString());
				SymbolMap.put(Key, sb.toString());
				if(Required != null) {
					SymbolMap.put(Key+";", Required);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			LibBunSystem._Exit(1, " Cannot load inline library: " + Path);
		}
	}


	public static String _SourceBuilderToString(LibBunSourceBuilder Builder) {
		return LibBunSystem._SourceBuilderToString(Builder, 0, Builder.SourceList.size());
	}

	public static String _SourceBuilderToString(LibBunSourceBuilder Builder, int BeginIndex, int EndIndex) {
		StringBuilder builder = new StringBuilder();
		for(int i = BeginIndex; i < EndIndex; i = i + 1) {
			builder.append(Builder.SourceList.ArrayValues[i]);
		}
		return builder.toString();
	}

	public final static void _WriteTo(String FileName, BArray<LibBunSourceBuilder> List) {
		if(FileName == null) {
			@Var int i = 0;
			while(i < List.size()) {
				@Var LibBunSourceBuilder Builder = List.ArrayValues[i];
				System.out.println(Builder.toString());
				Builder.Clear();
				i = i + 1;
			}
		}
		else {
			try {
				BufferedWriter w = new BufferedWriter(new FileWriter(FileName));
				@Var int i = 0;
				while(i < List.size()) {
					@Var LibBunSourceBuilder Builder = List.ArrayValues[i];
					w.write(Builder.toString());
					w.write("\n\n");
					Builder.Clear();
					i = i + 1;
				}
				w.close();
			}
			catch(IOException e) {
				LibBunSystem._Exit(1, "cannot to write: " + e);
			}
		}
	}

	// HighLevel Library

	public final static boolean _ApplyTokenFunc(BTokenFunction TokenFunc, BSourceContext SourceContext) {
		return TokenFunc.Invoke(SourceContext);
	}

	public final static BNode _ApplyMatchFunc(BMatchFunction MatchFunc, BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return MatchFunc.Invoke(ParentNode, TokenContext, LeftNode);
	}

	private final static BunMap<Class<?>> ClassMap = new BunMap<Class<?>>(null);

	static {
		ClassMap.put("syntax::bun", libbun.lang.bun.BunGrammar.class);
		ClassMap.put("syntax::bun.extra", libbun.lang.bun.extra.BunExtraGrammar.class);
		ClassMap.put("syntax::bun.regex", libbun.lang.bun.regexp.RegExpGrammar.class);
		ClassMap.put("syntax::bun.shell", libbun.lang.bun.shell.ShellGrammar.class);

		ClassMap.put("syntax::lisp",   libbun.lang.lisp.LispGrammar.class);
		ClassMap.put("syntax::konoha", libbun.lang.konoha.KonohaGrammar.class);
		ClassMap.put("syntax::python", libbun.lang.python.PythonGrammar.class);

		// source code by file extension
		ClassMap.put("bun", libbun.encode.playground.BunGenerator.class);
		ClassMap.put("c",   libbun.encode.playground.CGenerator.class);
		ClassMap.put("cl",  libbun.encode.playground.CommonLispGenerator.class);
		ClassMap.put("cs",  libbun.encode.playground.CSharpGenerator.class);
		ClassMap.put("erl", libbun.encode.erlang.ErlangGenerator.class);

		ClassMap.put("hs",  libbun.encode.haskell.HaskellSourceGenerator.class);
		ClassMap.put("java", libbun.encode.playground.JavaGenerator.class);
		ClassMap.put("js",  libbun.encode.playground.JavaScriptGenerator.class);

		ClassMap.put("lua",  libbun.encode.devel.LuaGenerator.class);

		ClassMap.put("pl",  libbun.encode.obsolete.PerlGenerator.class);
		ClassMap.put("py", libbun.encode.playground.PythonGenerator.class);
		ClassMap.put("r", libbun.encode.playground.RGenerator.class);
		ClassMap.put("rb", libbun.encode.devel.RubyGenerator.class);

		ClassMap.put("zen", libbun.encode.playground.BunGenerator.class);

		//
		ClassMap.put("ssac", libbun.encode.devel.SSACGenerator.class);

		// engine
		ClassMap.put("jvm", libbun.encode.jvm.AsmJavaGenerator.class);
		ClassMap.put("debug-jvm", libbun.encode.jvm.DebugAsmGenerator.class);
		ClassMap.put("dump-jvm", libbun.encode.jvm.ByteCodePrinter.class);
		ClassMap.put("ll", libbun.encode.llvm.LLVMSourceGenerator.class);

	}

	public final static boolean _LoadGrammar(LibBunGamma Gamma, String ClassName) {
		try {
			@Var Class<?> GrammarClass =  ClassMap.GetOrNull(ClassName.toLowerCase());
			if(GrammarClass == null) {
				GrammarClass = Class.forName(ClassName);
			}
			@Var Method LoaderMethod = GrammarClass.getMethod("LoadGrammar", LibBunGamma.class);
			LoaderMethod.invoke(null, Gamma);
			return true;
		} catch (Exception e) { // naming
			e.printStackTrace();
		}
		return false;
	}

	public final static LibBunGenerator _LoadGenerator(String ClassName, String OutputFile) {
		if (ClassName != null) {
			try {
				Class<?> GeneratorClass = ClassMap.GetOrNull(ClassName.toLowerCase());
				if(GeneratorClass == null) {
					GeneratorClass = Class.forName(ClassName);
				}
				return (LibBunGenerator) GeneratorClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LibBunSystem._PrintLine("unknown target: " + ClassName);
		return new BunGenerator();
	}

	public final static LibBunGenerator _InitGenerator(@Nullable String ClassName, String GrammarClass) {
		@Var LibBunGenerator Generator = LibBunSystem._LoadGenerator(ClassName, null);
		LibBunSystem._LoadGrammar(Generator.RootGamma, GrammarClass);
		Generator.SetTypeChecker(new BunTypeSafer(Generator));
		Generator.RequireLibrary("common", null);
		return Generator;
	}

	//
	public final static BType GetNativeType(Class<?> NativeClass) {
		return JavaTypeTable.GetBunType(NativeClass);
	}

	// Type
	public final static String _GetClassName(Object Value) {
		return Value.getClass().getSimpleName();
	}

	public final static Class<?> GetClassOfValue(Object Value) {
		return Value.getClass();
	}

	public final static String[] _GreekNames = {
		/*"Alpha"*/ "\u03B1", "\u03B2", "\u03B3"
	};

	public final static BType[] _NewTypeArray(int Size) {
		return new BType[Size];
	}

	public final static BNode[] _NewNodeArray(int Size) {
		return new BNode[Size];
	}

	public final static void _ArrayCopy(Object src, int sIndex, Object dst, int dIndex, int length) {
		System.arraycopy(src, sIndex, dst, dIndex, length);
	}


}
