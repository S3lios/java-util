package math;

import java.util.Iterator;

public class Zeros implements Iterable<Double> {
	int n;
	
	public Zeros(int n) {
		this.n = n;
	}

	public static Zeros create(int n) {
		return new Zeros(n);
	}

	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			int i = 0;
			
			@Override
			public boolean hasNext() {
				return i < n;
			}

			@Override
			public Double next() {
				return 0.0;
			}
		};
	}

	
	public double[] toArray() {
		double[] array = new double[n];
		for (int i = 0; i < n; i++) {
			array[i] = 0.0;
		}
		return array;
	}

}
