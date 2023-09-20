package parser;

import java.util.List;

import lexer.Token;
import lexer.TokenInstance;

public interface Symbol {

    Object getName();

	Iterable<Result> parse(List<TokenInstance> input, int startIndex, int endIndex);

	Iterable<Result> parse(List<TokenInstance> input, int startIndex, int endIndex, Token firstToken);

}
