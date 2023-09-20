package lexer;

import java.util.LinkedList;
import java.util.List;

public class Lexer {

    List<Token> tokens;
    List<Token> ignoredTokens;
    
    public static Lexer createLexer() {
        return new Lexer();
    }

    public Lexer() {
        tokens = new LinkedList<Token>();
        ignoredTokens = new LinkedList<Token>();
    }

    public Lexer addToken(Token token) {

        if (token.getPattern().equals("")) {
            System.err.println("Warning: token " + token.getName() + " has an empty pattern. \n It will never be matched.");
        }

        if (token.getPattern().equals(" ")) {
            System.err.println("Warning: token " + token.getName() + " has a pattern that matches only whitespace. \n It will never be matched.");
        }

        if (token.getPattern().equals("\n")) {
            System.err.println("Warning: token " + token.getName() + " has a pattern that matches only newlines. \n It will never be matched.");
        }

        if (token.getPattern().equals("\t")) {
            System.err.println("Warning: token " + token.getName() + " has a pattern that matches only tabs. \n It will never be matched.");
        }

        if (ignoredTokens.contains(token)) {
            System.err.println("Warning: token " + token.getName() + " is matched and ignored. \n It will not be ignored.");
        }

        for (Token t : tokens) {
            if ( token.getPattern().startsWith(t.getPattern()) ) {
                System.err.println("Warning: token " + t.getName() + " is a prefix of " + token.getName()+". "
                + token.getName() + " will never be matched. \nMaybe you should reorder your tokens and put " + token.getName() + " before " + t.getName() + "?");
            }
        }
        tokens.add(token);
        return this;
    }

    public Lexer ignoreToken(Token token) {
        if (tokens.contains(token)) {
            System.err.println("Warning: token " + token.getName() + " is ignored but also matched. \n It will not be ignored.");
        }
        ignoredTokens.add(token);
        return this;
    }

    public Lexer addToken(String name, String regex) {
        return this.addToken(new Token(name, regex));
    }

    public List<TokenInstance> lex(String input) {
        List<TokenInstance> instances = new LinkedList<TokenInstance>();
        int line = 1;
        int column = 1;

		// Trim whitespace and other non-printable characters
		while (input.length() > 0 && input.charAt(0) <= ' ') {
			if (input.charAt(0) == '\n') {
				line++;
				column = 1;
			} else {
				column++;
			}
			input = input.substring(1);
		}

        while (input.length() > 0) {
			
            boolean match = false;
            for (Token token : tokens) {
                String value = token.match(input);
                if (value != null) {
                    match = true;
                    instances.add(token.generateInstance(value, line, column));
                    input = input.substring(value.length());
                    column += value.length();
                    break;
                }
            }

            boolean ignore = false;
            if (!match) {
                for (Token token : ignoredTokens) {
                    String value = token.match(input);
                    if (value != null) {
                        ignore = true;
                        input = input.substring(value.length());
                        column += value.length();
                        break;
                    }
                }
            }   

            if (!match && !ignore) {
				String nextLexeme = input.split(" ")[0];
                System.err.println("Unexpected lexeme at line " + line + ", column " + column + ": " + nextLexeme);
                System.exit(1);
            }

			// Trim whitespace and other non-printable characters
			while (input.length() > 0 && input.charAt(0) <= ' ') {
				if (input.charAt(0) == '\n') {
					line++;
					column = 1;
				} else {
					column++;
				}
				input = input.substring(1);
			}
        }
        return instances;
    }
}
