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


package libbun.parser;
import libbun.ast.BNode;
import libbun.ast.BunBlockNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.encode.LibBunGenerator;
import libbun.type.BClassType;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.BMap;
import libbun.util.BMatchFunction;
import libbun.util.BTokenFunction;
import libbun.util.Nullable;
import libbun.util.Var;

public final class LibBunGamma {
	@BField public final LibBunGenerator   Generator;
	@BField public final BunBlockNode   BlockNode;
	@BField LibBunTokenFuncChain[]       TokenMatrix = null;
	@BField BMap<LibBunSyntax>      SyntaxTable = null;
	@BField BMap<BunLetVarNode>  SymbolTable2 = null;

	public LibBunGamma(LibBunGenerator Generator, BunBlockNode BlockNode) {
		this.BlockNode = BlockNode;   // rootname is null
		this.Generator = Generator;
		assert(this.Generator != null);
	}

	public final LibBunGamma GetParentGamma() {
		if(this.BlockNode != null) {
			@Var BNode Node = this.BlockNode.ParentNode;
			while(Node != null) {
				if(Node instanceof BunBlockNode) {
					@Var BunBlockNode blockNode = (BunBlockNode)Node;
					if(blockNode.NullableGamma != null) {
						return blockNode.NullableGamma;
					}
				}
				Node = Node.ParentNode;
			}
		}
		return null;
	}

	@Override public String toString() {
		return "NS[" + this.BlockNode + "]";
	}

	public final LibBunGamma GetRootGamma() {
		return this.Generator.RootGamma;
	}

	// TokenMatrix
	public final LibBunTokenFuncChain GetTokenFunc(int ZenChar) {
		if(this.TokenMatrix == null) {
			return this.GetParentGamma().GetTokenFunc(ZenChar);
		}
		return this.TokenMatrix[ZenChar];
	}

	private final LibBunTokenFuncChain JoinParentFunc(BTokenFunction Func, LibBunTokenFuncChain Parent) {
		if(Parent != null && Parent.Func == Func) {
			return Parent;
		}
		return new LibBunTokenFuncChain(Func, Parent);
	}

	public final void AppendTokenFunc(String keys, BTokenFunction TokenFunc) {
		if(this.TokenMatrix == null) {
			this.TokenMatrix = BLib._NewTokenMatrix();
			if(this.GetParentGamma() != null) {
				@Var int i = 0;
				while(i < this.TokenMatrix.length) {
					this.TokenMatrix[i] = this.GetParentGamma().GetTokenFunc(i);
					i = i + 1;
				}
			}
		}
		@Var int i = 0;
		while(i < keys.length()) {
			@Var int kchar = BLib._GetTokenMatrixIndex(BLib._GetChar(keys, i));
			this.TokenMatrix[kchar] = this.JoinParentFunc(TokenFunc, this.TokenMatrix[kchar]);
			i = i + 1;
		}
	}

	// Pattern
	public final LibBunSyntax GetSyntaxPattern(String PatternName) {
		@Var LibBunGamma Gamma = this;
		while(Gamma != null) {
			if(Gamma.SyntaxTable != null) {
				return Gamma.SyntaxTable.GetOrNull(PatternName);
			}
			Gamma = Gamma.GetParentGamma();
		}
		return null;
	}

	public final void SetSyntaxPattern(String PatternName, LibBunSyntax Syntax) {
		if(this.SyntaxTable == null) {
			this.SyntaxTable = new BMap<LibBunSyntax>(null);
		}
		this.SyntaxTable.put(PatternName, Syntax);
	}

	public final static String _RightPatternSymbol(String PatternName) {
		return "\t" + PatternName;
	}

	public final LibBunSyntax GetRightSyntaxPattern(String PatternName) {
		return this.GetSyntaxPattern(LibBunGamma._RightPatternSymbol(PatternName));
	}

	private void AppendSyntaxPattern(String PatternName, LibBunSyntax NewPattern) {
		BLib._Assert(NewPattern.ParentPattern == null);
		@Var LibBunSyntax ParentPattern = this.GetSyntaxPattern(PatternName);
		NewPattern.ParentPattern = ParentPattern;
		this.SetSyntaxPattern(PatternName, NewPattern);
	}

	public final void DefineStatement(String PatternName, BMatchFunction MatchFunc) {
		@Var int Alias = PatternName.indexOf(" ");
		@Var String Name = PatternName;
		if(Alias != -1) {
			Name = PatternName.substring(0, Alias);
		}
		@Var LibBunSyntax Pattern = new LibBunSyntax(this, Name, MatchFunc);
		Pattern.IsStatement = true;
		this.AppendSyntaxPattern(Name, Pattern);
		if(Alias != -1) {
			this.DefineStatement(PatternName.substring(Alias+1), MatchFunc);
		}
	}

	public final void DefineExpression(String PatternName, BMatchFunction MatchFunc) {
		@Var int Alias = PatternName.indexOf(" ");
		@Var String Name = PatternName;
		if(Alias != -1) {
			Name = PatternName.substring(0, Alias);
		}
		@Var LibBunSyntax Pattern = new LibBunSyntax(this, Name, MatchFunc);
		this.AppendSyntaxPattern(Name, Pattern);
		if(Alias != -1) {
			this.DefineExpression(PatternName.substring(Alias+1), MatchFunc);
		}
	}

	public final void DefineRightExpression(String PatternName, BMatchFunction MatchFunc) {
		@Var int Alias = PatternName.indexOf(" ");
		@Var String Name = PatternName;
		if(Alias != -1) {
			Name = PatternName.substring(0, Alias);
		}
		@Var LibBunSyntax Pattern = new LibBunSyntax(this, Name, MatchFunc);
		this.AppendSyntaxPattern(LibBunGamma._RightPatternSymbol(Name), Pattern);
		if(Alias != -1) {
			this.DefineRightExpression(PatternName.substring(Alias+1), MatchFunc);
		}
	}

	public final void SetSymbol(String Symbol, BunLetVarNode EntryNode) {
		if(this.SymbolTable2 == null) {
			this.SymbolTable2 = new BMap<BunLetVarNode>(null);
		}
		this.SymbolTable2.put(Symbol, EntryNode);
	}

	public final BunLetVarNode GetSymbol(String Symbol) {
		@Var LibBunGamma Gamma = this;
		while(Gamma != null) {
			if(Gamma.SymbolTable2 != null) {
				@Var BunLetVarNode EntryNode = Gamma.SymbolTable2.GetOrNull(Symbol);
				if(EntryNode != null) {
					return EntryNode;
				}
			}
			Gamma = Gamma.GetParentGamma();
		}
		return null;
	}

	public final void SetDebugSymbol(String Symbol, BunLetVarNode EntryNode) {
		this.SetSymbol(Symbol, EntryNode);
		BLib._PrintLine("SetSymbol: " + Symbol + " @" + this);
	}

	public final BunLetVarNode GetDebugSymbol(String Symbol) {
		@Var BunLetVarNode Node = this.GetSymbol(Symbol);
		BLib._PrintLine("GetSymbol: " + Symbol + " => " + Node);
		return Node;
	}

	public final int GetNameIndex(String Name) {
		@Var int NameIndex = -1;
		@Var LibBunGamma Gamma = this;
		while(Gamma != null) {
			if(Gamma.SymbolTable2 != null) {
				@Var BNode EntryNode = Gamma.SymbolTable2.GetOrNull(Name);
				if(EntryNode != null) {
					NameIndex = NameIndex + 1;
				}
			}
			Gamma = Gamma.GetParentGamma();
		}
		return NameIndex;
	}

	public final void SetRootSymbol(String Symbol, BunLetVarNode EntryNode) {
		this.GetRootGamma().SetSymbol(Symbol, EntryNode);
	}

	public final BunLetVarNode GetLocalVariable(String Name) {
		@Var BNode EntryNode = this.GetSymbol(Name);
		//System.out.println("var " + VarName + ", entry=" + Entry + ", Gamma=" + this);
		if(EntryNode instanceof BunLetVarNode) {
			return (BunLetVarNode)EntryNode;
		}
		return null;
	}

	// Type
	public final void SetTypeName(String Name, BType Type, @Nullable BToken SourceToken) {
		//@Var ZTypeNode Node = new ZTypeNode(null, SourceToken, Type);
		@Var BunLetVarNode Node = new BunLetVarNode(null, BunLetVarNode._IsReadOnly, Type, Name);
		Node.SourceToken = SourceToken;
		this.SetSymbol(Name, Node);
	}

	public final void SetTypeName(BType Type, @Nullable BToken SourceToken) {
		this.SetTypeName(Type.ShortName, Type, SourceToken);
	}

	public final BType GetType(String TypeName, BToken SourceToken, boolean IsCreation) {
		@Var BunLetVarNode Node = this.GetSymbol(TypeName);
		if(Node != null) {
			return Node.DeclType();
		}
		if(IsCreation && BLib._IsSymbol(BLib._GetChar(TypeName, 0))) {
			@Var BType Type = new BClassType(TypeName, BType.VarType);
			this.GetRootGamma().SetTypeName(TypeName, Type, SourceToken);
			return Type;
		}
		return null;
	}


}