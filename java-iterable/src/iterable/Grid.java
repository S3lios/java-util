package iterable;

import java.util.Iterator;

import data.Pair;

public class Grid implements Iterable<Pair<Double, Double>> {

	double xMin;
	double xMax;
	double yMin;
	double yMax;

	int xSize;
	int ySize;

	public Grid(double xMin, double xMax, double yMin, double yMax, int xSize, int ySize) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;

		this.xSize = xSize;
		this.ySize = ySize;
	}

	@Override
	public Iterator<Pair<Double, Double>> iterator() {
		return new Iterator<Pair<Double, Double>>() {
			int i = 0;
			int j = 0;

			@Override
			public boolean hasNext() {
				return i < xSize && j < ySize;
			}

			@Override
			public Pair<Double, Double> next() {
				Pair<Double, Double> result = new Pair<>(xMin + i * (xMax - xMin) / xSize,
						yMin + j * (yMax - yMin) / ySize);
				i++;
				if (i == xSize) {
					i = 0;
					j++;
				}
				return result;
			}
		};
	}

	public double[][] toArray() {
		double[][] result = new double[xSize][ySize];
		int i = 0;
		int j = 0;
		for (Pair<Double, Double> p : this) {
			result[i][j] = p.first;
			i++;
			if (i == xSize) {
				i = 0;
				j++;
			}
		}
		return result;
	}

	public double[][] map(java.util.function.BiFunction<Double, Double, Double> f) {
		double[][] result = new double[xSize][ySize];
		int i = 0;
		int j = 0;
		for (Pair<Double, Double> p : this) {
			result[i][j] = f.apply(p.first, p.second);
			i++;
			if (i == xSize) {
				i = 0;
				j++;
			}
		}
		return result;
	}

	public double[] xArray() {
		double[] result = new double[xSize];
		int i = 0;
		for (Pair<Double, Double> p : this) {
			result[i] = p.first;
			i++;
		}
		return result;
	}

	public double[] yArray() {
		double[] result = new double[ySize];
		int j = 0;
		for (Pair<Double, Double> p : this) {
			result[j] = p.second;
			j++;
		}
		return result;
	}

	public double[] zArray(java.util.function.BiFunction<Double, Double, Double> f) {
		double[] result = new double[xSize * ySize];
		int i = 0;
		for (Pair<Double, Double> p : this) {
			result[i] = f.apply(p.first, p.second);
			i++;
		}
		return result;
	}

	
}
