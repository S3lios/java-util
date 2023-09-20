package src.parser;

import java.util.Iterator;
import java.util.List;

import iterator.BreadthIterator;
import iterator.EmptyIterator;
import src.lexer.Token;
import src.lexer.TokenInstance;

public class NTSymbol implements Symbol {

	List<Rule> rules;
	String name;

	public NTSymbol(String name){
		this.rules = new java.util.ArrayList<>();
		this.name = name;
	}

	public NTSymbol addRule(Rule rule) {
		rules.add(rule);
		return this;
	}

	@Override
	public Object getName() {
		return name;
	}

	@Override
	public Iterable<Result> parse(List<TokenInstance> input, int startIndex, int endIndex) {

		return new Iterable<Result>() {
			
			@Override
			public Iterator<Result> iterator() {

				if (startIndex >= endIndex) {
					return new EmptyIterator<>();
				}

				@SuppressWarnings("unchecked")
				Iterable<Result>[] iters = new Iterable[rules.size()];
				for (int i = 0; i < rules.size(); i++) {
					iters[i] = rules.get(i).parse(input, startIndex, endIndex);
				}
				return new BreadthIterator<Result>(iters);
			}
		};
	}

	@Override
	public Iterable<Result> parse(List<TokenInstance> input, int startIndex, int endIndex, Token firstToken) {
		if (firstToken == null) {
			return parse(input, startIndex, endIndex);
		}


		return new Iterable<Result>() {
			
			@Override
			public Iterator<Result> iterator() {

				int lastTokenIndex = startIndex;

				for (int i = startIndex; i < endIndex; i++) {
					if (input.get(i).getInstanceOf() == firstToken) {
						lastTokenIndex = i;
					}
				}

				if (startIndex >= endIndex) {
					return new EmptyIterator<>();
				}

				@SuppressWarnings("unchecked")
				Iterable<Result>[] iters = new Iterable[rules.size()];
				for (int i = 0; i < rules.size(); i++) {
					iters[i] = rules.get(i).parse(input, startIndex, lastTokenIndex);
				}
				
				return new BreadthIterator<Result>(iters);
			}
		};
	}


	public String toString() {
		return name;
	}


}
