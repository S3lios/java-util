package iterator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class FullLinkedIterator<T> implements Iterator<List<T>> {

	Function<T, Iterable<T>>[] childrensBuilders;
	Iterator<T>[] childrens;
	T[] nexts;
	Iterable<T> root;

	boolean isInit = false;

	@SuppressWarnings("unchecked")
	public FullLinkedIterator(Iterable<T> root, Function<T, Iterable<T>>... childrensBuilders) {
		this.root = root;
		this.childrensBuilders = childrensBuilders;
		this.childrens = new Iterator[childrensBuilders.length+1];
		this.nexts = (T[]) new Object[childrensBuilders.length+1];
	}

	@Override
	public boolean hasNext() {
		if (!isInit) {
			init();
		}
		return nexts != null;
	}

	private void init() {
		this.childrens[0] = root.iterator();
		isInit = true;
		prepare();
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
					this.nexts[index] = interChild;
					if (index < childrens.length-1) {
						childrens[index+1] = childrensBuilders[index].apply(interChild).iterator();
					}
					index ++;
				} else {
					childrens[index] = null;
					index--;
					if (index < 0) {
						this.nexts = null;
						break;
					}
				}
			}
		}
	}

	@Override
	public List<T> next() {
		if (!isInit) {
			isInit = true;
			prepare();
		}
		if (nexts == null) {
			throw new IllegalStateException("No more elements to iterate over.");
		}
		List<T> nexts = new java.util.ArrayList<T>(this.nexts.length);
		for (T next : this.nexts) {
			nexts.add(next);
		}
		prepare();
		return nexts;
	}
}
