package iterable;

public class Range implements Iterable<Double> {

	double start;
	double end;
	double step;

	public Range(double start, double end, double step) {
		this.start = start;
		this.end = end;
		this.step = step;
	}

	public Range(double start, double end) {
		this(start, end, 1);
	}

	public Range(double end) {
		this(0, end, 1);
	}

	@Override
	public java.util.Iterator<Double> iterator() {
		return new java.util.Iterator<Double>() {
			double current = start;
			int i = 0;
			final int iMax = (int) ((end - start) / step);

			@Override
			public boolean hasNext() {
				return i < iMax;
			}

			@Override
			public Double next() {
				double result = current;
				i++;
				current = start + i * step;
				return result;
			}
		};
	}


	public double[] map(java.util.function.Function<Double, Double> f) {
		double[] result = new double[(int) ((end - start) / step)];
		int i = 0;
		for (double x : this) {
			result[i] = f.apply(x);
			i++;
		}
		return result;
	}

	public double[] toArray() {
		return this.map(x -> x);
	}


}
