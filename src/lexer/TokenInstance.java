package lexer;

public class TokenInstance {
	
    Token instanceOf;
    String value;
    int line;
    int column;
    

    public TokenInstance(Token instanceOf, String value, int line, int column) {
        this.instanceOf = instanceOf;
        this.value = value;
        this.line = line;
        this.column = column;
    }
    
    public Token getInstanceOf() {
        return instanceOf;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return instanceOf.name + ": " + value + " (" + line + ", " + column + ")";
    }
}
