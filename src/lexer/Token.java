package src.lexer;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.parser.Result;
import src.parser.Symbol;

public class Token implements Symbol {

	public static Token TK_INTEGER = new Token("TK_INT", "[0-9]+");
    public static Token TK_FLOAT = new Token("TK_FLOAT", "[0-9]+\\.[0-9]+");
    public static Token TK_STRING = new Token("TK_STRING", "\".*\"");
    public static Token TK_IDENTIFIER = new Token("TK_IDENTIFIER", "[a-zA-Z_][a-zA-Z0-9_]*");
    public static Token TK_PLUS = new Token("TK_PLUS", "\\+");
    public static Token TK_MINUS = new Token("TK_MINUS", "-");
    public static Token TK_STAR = new Token("TK_STAR", "\\*");
    public static Token TK_SLASH = new Token("TK_SLASH", "/");
    public static Token TK_PERCENT = new Token("TK_PERCENT", "%");
    public static Token TK_EQUALS = new Token("TK_EQUALS", "=");
    public static Token TK_LPAREN = new Token("TK_LPAREN", "\\(");
    public static Token TK_RPAREN = new Token("TK_RPAREN", "\\)");
    public static Token TK_LBRACE = new Token("TK_LBRACE", "\\{");
    public static Token TK_RBRACE = new Token("TK_RBRACE", "\\}");
    public static Token TK_LBRACKET = new Token("TK_LBRACKET", "\\[");
    public static Token TK_RBRACKET = new Token("TK_RBRACKET", "\\]");
    public static Token TK_COMMA = new Token("TK_COMMA", ",");
    public static Token TK_COLON = new Token("TK_COLON", ":");
    public static Token TK_SEMICOLON = new Token("TK_SEMICOLON", ";");
    public static Token TK_DOT = new Token("TK_DOT", "\\.");
    public static Token TK_ARROW = new Token("TK_ARROW", "->");
    public static Token TK_IF = new Token("TK_IF", "if");
    public static Token TK_ELSE = new Token("TK_ELSE", "else");
    public static Token TK_WHILE = new Token("TK_WHILE", "while");
    public static Token TK_FOR = new Token("TK_FOR", "for");
    public static Token TK_RETURN = new Token("TK_RETURN", "return");
    public static Token TK_BREAK = new Token("TK_BREAK", "break");
    public static Token TK_CONTINUE = new Token("TK_CONTINUE", "continue");
    public static Token TK_TRUE = new Token("TK_TRUE", "true");
    public static Token TK_FALSE = new Token("TK_FALSE", "false");
    public static Token TK_QMARK = new Token("TK_QMARK", "\\?");
    public static Token TK_EXCLAMATION = new Token("TK_EXCLAMATION", "!");
    public static Token TK_POWER = new Token("TK_POWER", "\\^");
    public static Token TK_AMPERSAND = new Token("TK_AMPERSAND", "&");
    public static Token TK_PIPE = new Token("TK_PIPE", "\\|");
    public static Token TK_TILDE = new Token("TK_TILDE", "~");
    public static Token TK_LESS = new Token("TK_LESS", "<");
    public static Token TK_GREATER = new Token("TK_GREATER", ">");
    public static Token TK_EQUAL = new Token("TK_LESS_EQUAL", "=");
    public static Token TK_LESS_EQUAL = new Token("TK_LESS_EQUAL", "<=");
    public static Token TK_GREATER_EQUAL = new Token("TK_GREATER_EQUAL", ">=");

    String name;
    Pattern pattern;
    
    public Token(String name, String regex) {
        this.name = name;
        this.pattern = Pattern.compile("^" + regex);
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern.pattern();
    }

    TokenInstance generateInstance(String value, int line, int column) {
        return new TokenInstance(this, value, line, column);
    }

	String match(String input) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

	@Override
	public Iterable<Result> parse(List<TokenInstance> input, int startIndex, int endIndex) {
		return new Iterable<Result>() {
			@Override
			public Iterator<Result> iterator() {
				return new Iterator<Result>() {
					final int index = startIndex;
					boolean given = false;
					
					@Override
					public boolean hasNext() {
						return !given && input.size() > index && input.get(index).getInstanceOf() == Token.this;
					}

					@Override
					public Result next() {
						if (!given && input.size() > index && input.get(index).getInstanceOf() == Token.this) {
							given = true;
							Result result = new Result(input.get(index).value, index + 1);
							return result;
						} else {
							return null;
						}
					}
				};
			}
		};
	}

	@Override
	public Iterable<Result> parse(List<TokenInstance> input, int startIndex, int endIndex, Token firstToken) {
		return parse(input, startIndex, endIndex);
	}
}