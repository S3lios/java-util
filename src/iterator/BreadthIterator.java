package iterator;
import java.util.Iterator;
import java.util.List;

public class BreadthIterator<T> implements Iterator<T> {
	private Iterable<T>[] childrenIterables;
	private final Iterator<T>[] childrens;
	private int index = 0;

	boolean isInit = false;

	@SuppressWarnings("unchecked")
	public BreadthIterator(List<Iterable<T>> children) {
		this.childrenIterables = new Iterable[children.size()];

		for (int i = 0; i < children.size(); i++) {
			this.childrenIterables[i] = children.get(i);
		}

		this.childrens = new Iterator[children.size()];
	}

	@SuppressWarnings("unchecked")
	public BreadthIterator(Iterable<T>... children) {
		this.childrenIterables = new Iterable[children.length];

		for (int i = 0; i < children.length; i++) {
			this.childrenIterables[i] = children[i];
		}

		this.childrens = new Iterator[children.length];
	}

	private void init() {
		for (int i = 0; i < childrenIterables.length; i++) {
			this.childrens[i] = childrenIterables[i].iterator();
		}
		isInit = true;
		rebase();
	}

	@Override
	public boolean hasNext() {
		if (!isInit) {
			init();
		}
		return childrens[index].hasNext();
	}

	private void rebase() {
		int tryIndex = 0;
		while (!childrens[index].hasNext() && tryIndex < childrens.length) {
			index++;
			if (index >= childrens.length) {
				index = 0;
			}

			if (childrens[index].hasNext()) {
				break;
			}

			tryIndex++;
		}
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new IllegalStateException("No more elements to iterate over.");
		}

		T next = childrens[index].next();
		index++;
		if (index >= childrens.length) {
			index = 0;
		}
		rebase();

		return next;
	}

}