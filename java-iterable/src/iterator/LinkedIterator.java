package iterator;
import java.util.Iterator;
import java.util.function.Function;

public class LinkedIterator<T> implements Iterator<T> {

	Function<T, Iterable<T>>[] childrensBuilders;
	Iterator<T>[] childrens;
	T next;
	Iterable<T> root;

	boolean isInit = false;

	@SuppressWarnings("unchecked")
	public LinkedIterator(Iterable<T> root, Function<T, Iterable<T>>... childrensBuilders) {
		this.root = root;
		this.childrensBuilders = childrensBuilders;
		this.childrens = new Iterator[childrensBuilders.length+1];
	}

	private void init() {
		this.childrens[0] = root.iterator();
		isInit = true;
		prepare();
	}

	@Override
	public boolean hasNext() {
		if (!isInit) {
			init();
		}
		return next != null;
	}

	private void prepare() {
		// Prepare the next element
		int index = childrens.length-1;
		while (index < childrens.length) {
			if (childrens[index] == null) {
				index--;
			} else {
				if (childrens[index].hasNext()) {
					T interChild = childrens[index].next();
					if (index < childrens.length-1) {
						childrens[index+1] = childrensBuilders[index].apply(interChild).iterator();
					} else {
						this.next = interChild;
					}
					index ++;
				} else {
					childrens[index] = null;
					index--;
					if (index < 0) {
						this.next = null;
						break;
					}
				}
			}
		}
	}

	@Override
	public T next() {
		if (!isInit) {
			init();
		}
		if (next == null) {
			throw new IllegalStateException("No more elements to iterate over.");
		}
		T next = this.next;
		prepare();
		return next;
	}
}
