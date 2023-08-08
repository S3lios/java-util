package iterator;

import java.util.Iterator;

public class EmptyIterator<T> implements Iterator<T> {
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		throw new IllegalStateException("No more elements to iterate over.");
	}
}
