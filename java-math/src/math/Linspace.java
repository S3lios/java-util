package math;

import java.util.Iterator;
import java.util.function.Function;


public class Linspace implements Iterable<Double> {

    double start;
    double end;
    int n;

    private Function<Double, Double> f;

	public static Linspace create(double start, double end, int n) {
		return new Linspace(start, end, n);
	}

	public static Linspace create(double start, double end, int n, Function<Double, Double> f) {
		return new Linspace(start, end, n, f);
	}

    public Linspace(double start, double end, int n) {
        super();
		if (n < 2) { throw new IllegalArgumentException("n must be greater than 1"); }
        this.start = start;
        this.end = end;
        this.n = n;
    }

    public Linspace(double start, double end, int n, Function<Double, Double> f) {
        this(start, end, n);
		this.f = f;
    }

	@Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {

            private int i = 0;
            private double step = (end - start) / (n - 1);

            @Override
            public boolean hasNext() {
                return i < n;
            }

            @Override
            public Double next() {
                if (f != null) {
                    return f.apply(start + step * i++);
                }
                return start + step * i++;
            }
        };
    }

	public double[] toArray() {
		double[] array = new double[n];
		int i = 0;
		for (double d : this) {
			array[i++] = d;
		}
		return array;
	}

}
