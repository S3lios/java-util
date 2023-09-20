package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import iterator.EmptyIterator;
import iterator.FullLinkedIterator;
import lexer.Token;
import lexer.TokenInstance;

public class Rule {

	List<Symbol> symbols;
	Function<Object[], Object> funcResult;
	Token firstToken;

	public static Rule pattern(Symbol... symbols) {
		return new Rule(symbols);
	}

	Rule(Symbol... symbols) {
		this.symbols = new ArrayList<>(symbols.length);
		for (Symbol s : symbols) {
			if (s instanceof Token) {
				if (firstToken == null) {
					firstToken = (Token) s;
				}
			}
			this.symbols.add(s);
		}
	}

	public Rule result(Function<Object[], Object> funcResult) {
		this.funcResult = funcResult;
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < symbols.size(); i++) {
			sb.append(symbols.get(i).getName());
			if (i < symbols.size() - 1) {
				sb.append(" ");
			}
		}
		
		return sb.toString();
	}

	Iterable<Result> parse(List<TokenInstance> input, int startIndex, int endIndex) {
		return new Iterable<Result>() {
		
			@Override
			public Iterator<Result> iterator() {
				if (startIndex >= endIndex || symbols.size() == 0) {
					return new EmptyIterator<>();
				}

				// Root iterable
				Iterable<Result> root = symbols.get(0).parse(input, startIndex, endIndex, firstToken);

				// Childrens iterables
				@SuppressWarnings("unchecked")
				Function<Object, Iterable<?>>[] childrensBuilders = new Function[symbols.size() - 1];

				for (int i = 1; i < symbols.size(); i++) {
					Symbol s = symbols.get(i);
					childrensBuilders[i - 1] = (r -> s.parse(input, ((Result) r).index, endIndex));
				}
				
				@SuppressWarnings("all")
				Iterator<List<Object>> it =  new FullLinkedIterator(root, childrensBuilders);
				
				return new Iterator<Result>() {
					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public Result next() {
						List<Object> next = it.next();
						Object[] objs = new Object[next.size()];
						for (int i = 0; i < next.size(); i++) {
							objs[i] =((Result) next.get(i)).getObject();
						}
						return new Result(funcResult.apply(objs), ((Result) next.get(next.size()-1)).getIndex());
					}
				};
			}
		};
	}


	
}
