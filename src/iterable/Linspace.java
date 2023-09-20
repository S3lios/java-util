package iterable;

import java.util.Iterator;

public class Linspace implements Iterable<Double>{

	double start;
	double end;
	int n;

	public Linspace(double start, double end, int n) {
		this.start = start;
		this.end = end;
		this.n = n;
	}

	@Override
	public Iterator<Double> iterator() {
		return new Iterator<Double>() {
			int i = 0;
			double current = start;
			double step = (end - start) / (n - 1);

			@Override
			public boolean hasNext() {
				return i < n;
			}

			@Override
			public Double next() {
				double result = current;
				current += step;
				i++;
				return result;
			}
		};
	}

	public double[] toArray() {
		double[] result = new double[n];
		int i = 0;
		for (double x : this) {
			result[i] = x;
			i++;
		}
		return result;
	}
	
}
