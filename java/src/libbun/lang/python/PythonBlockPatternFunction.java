package libbun.lang.python;

import libbun.ast.BNode;
import libbun.ast.BunBlockNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class PythonBlockPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BlockNode = new BunBlockNode(ParentNode, ParentNode.GetGamma());
		@Var BToken SkipToken = TokenContext.GetToken();
		BlockNode = TokenContext.MatchToken(BlockNode, ":", BTokenContext._Required);
		if(!BlockNode.IsErrorNode()) {
			@Var boolean Remembered = TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent); // init
			@Var int IndentSize = 0;
			while(TokenContext.HasNext()) {
				@Var BToken Token = TokenContext.GetToken();
				if(IndentSize > Token.GetIndentSize()) {
					break;
				}
				IndentSize = Token.GetIndentSize();
				BlockNode = TokenContext.MatchPattern(BlockNode, BNode._AppendIndex, "$Statement$", BTokenContext._Required);
				if(BlockNode.IsErrorNode()) {
					//FIXME: SkipError was deprecated
					//TokenContext.SkipError(SkipToken);
					break;
				}
			}
			TokenContext.SetParseFlag(Remembered);
		}
		return BlockNode;
	}

}
