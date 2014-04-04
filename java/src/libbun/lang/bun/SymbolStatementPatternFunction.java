package libbun.lang.bun;

import libbun.ast.BGetNameNode;
import libbun.ast.BNode;
import libbun.ast.BSetNameNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SymbolStatementPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BGetNameNode NameNode = new BGetNameNode(ParentNode, NameToken, NameToken.GetText());
		if(TokenContext.IsToken("=")) {
			@Var BNode AssignedNode = new BSetNameNode(ParentNode, null, NameNode);
			AssignedNode = TokenContext.MatchToken(ParentNode, "=", BTokenContext._Required);
			AssignedNode = TokenContext.MatchPattern(AssignedNode, BSetNameNode._Expr, "$Expression$", BTokenContext._Required);
			return AssignedNode;
		}
		else {
			return NameNode;
		}
	}

}
