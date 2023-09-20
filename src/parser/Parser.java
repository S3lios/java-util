package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import lexer.TokenInstance;

public class Parser {

	Symbol startSymbol;

	public Parser(Symbol startSymbol) {
		this.startSymbol = startSymbol;
	}

	public Object parse(List<TokenInstance> input) {
		int i = 0;
		List<TokenInstance> inputCopy = new ArrayList<TokenInstance>(input);
		Iterator<Result> it = startSymbol.parse(inputCopy, 0, inputCopy.size()).iterator();
		while (it.hasNext()) {
			i ++;
			Result r = it.next();
			if (r.getIndex() == inputCopy.size()) {
				System.out.println("Parse success after " + i + " iterations");
				return r.getObject();
			}
		}
		System.out.println("Parse failed after " + i + " iterations");
		return null;
	}

}
