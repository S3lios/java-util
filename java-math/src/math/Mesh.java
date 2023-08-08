package math;

import java.util.Iterator;
import java.util.function.BiFunction;

import math.geometry.Vector2D;

public class Mesh implements Iterable<Vector2D> {


	double x_min;
	double x_max;
	double y_min;
	double y_max;
	int x_n;
	int y_n;

	public static Mesh create(double x_min, double x_max, double y_min, double y_max, int x_n, int y_n) {
		return new Mesh(x_min, x_max, y_min, y_max, x_n, y_n);
	}

	public Mesh(double x_min, double x_max, double y_min, double y_max, int x_n, int y_n) {
		super();
		if (x_n < 2) { throw new IllegalArgumentException("x_n must be greater than 1"); }
		if (y_n < 2) { throw new IllegalArgumentException("y_n must be greater than 1"); }
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_min = y_min;
		this.y_max = y_max;
		this.x_n = x_n;
		this.y_n = y_n;
	}

	public Mesh(double x_min, double x_max, double y_min, double y_max, int n) {
		this(x_min, x_max, y_min, y_max, n, n);
	}
	
	public Mesh(Linspace x, Linspace y) {
		this(x.start, x.end, y.start, y.end, x.n, y.n);
	}

	@Override
	public Iterator<Vector2D> iterator() {
		return new Iterator<Vector2D>() {

			private int i = 0;
			private int j = 0;
			private double x_step = (x_max - x_min) / (x_n - 1);
			private double y_step = (y_max - y_min) / (y_n - 1);

			@Override
			public boolean hasNext() {
				return (j < y_n-1 || i < x_n);
			}

			@Override
			public Vector2D next() {
				if (i == x_n) {
					i = 0;
					j++;
				}
				return new Vector2D(x_min + x_step * i++, y_min + y_step * j);
			}
		};
	}

	public double[][] value(BiFunction<Double, Double, Double> f) {
		double[][] array = new double[x_n][y_n];
		int i = 0;
		int j = 0;
		for (Vector2D v : this) {
			if (i == x_n) {
				i = 0;
				j++;
			}
			array[i++][j] = f.apply(v.x, v.y);
		}
		return array;
	}

	public double[][] toArray() {
		double[][] array = new double[x_n][y_n];
		int i = 0;
		int j = 0;
		for (Vector2D v : this) {
			if (i == x_n) {
				i = 0;
				j++;
			}
			array[i++][j] = v.x;
		}
		return array;
	}

	public double[][] to3DData(BiFunction<Double, Double, Double> f) {
		double[][] array = new double[3][x_n * y_n];
		int i = 0;
		int j = 0;
		for (Vector2D v : this) {
			if (i == x_n) {
				i = 0;
				j++;
			}
			array[0][i + j * x_n] = v.x;
			array[1][i + j * x_n] = v.y;
			array[2][i + j * x_n] = f.apply(v.x, v.y);
			i++;
		}

		return array;
	}
}
