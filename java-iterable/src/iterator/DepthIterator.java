package iterator;
import java.util.Iterator;
import java.util.List;

public class DepthIterator<T> implements Iterator<T> {

	private final Iterator<T>[] childrens;
	private int index = 0;

	@SuppressWarnings("unchecked")
	public DepthIterator(List<Iterable<T>> children) {
		this.childrens = new Iterator[children.size()];
		for (int i = 0; i < children.size(); i++) {
			this.childrens[i] = children.get(i).iterator();
		}
	}

	@SuppressWarnings("unchecked")
	public DepthIterator(Iterable<T>... children) {
		this.childrens = new Iterator[children.length];
		for (int i = 0; i < children.length; i++) {
			this.childrens[i] = children[i].iterator();
		}
	}

	@Override
	public boolean hasNext() {
		return index < childrens.length;
	}

	@Override
	public T next() {
		if (index >= childrens.length) {
			throw new IllegalStateException("No more elements to iterate over.");
		}
		T next = childrens[index].next();
		if (!childrens[index].hasNext()) {
			index++;
		}
		return next;
	}

}
