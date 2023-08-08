package math;

import java.util.Iterator;

import math.geometry.Vector3D;

public class Grid implements Iterable<Vector3D>{

	int x_n;
	int y_n;
	int z_n;

	double x_min;
	double x_max;
	double y_min;
	double y_max;
	double z_min;
	double z_max;

	public Grid(double x_min, double x_max, double y_min, double y_max, double z_min, double z_max, int x_n, int y_n, int z_n) {
		super();
		if (x_n < 2) { throw new IllegalArgumentException("x_n must be greater than 1"); }
		if (y_n < 2) { throw new IllegalArgumentException("y_n must be greater than 1"); }
		if (z_n < 2) { throw new IllegalArgumentException("z_n must be greater than 1"); }
		this.x_min = x_min;
		this.x_max = x_max;
		this.y_min = y_min;
		this.y_max = y_max;
		this.z_min = z_min;
		this.z_max = z_max;
		this.x_n = x_n;
		this.y_n = y_n;
		this.z_n = z_n;
	}

	public Grid(double x_min, double x_max, double y_min, double y_max, double z_min, double z_max, int n) {
		this(x_min, x_max, y_min, y_max, z_min, z_max, n, n, n);
	}

	public Grid(Linspace x, Linspace y, Linspace z) {
		this(x.start, x.end, y.start, y.end, z.start, z.end, x.n, y.n, z.n);
	}




	@Override
	public Iterator<Vector3D> iterator() {
		return new Iterator<Vector3D>() {

			private int i = 0;
			private int j = 0;
			private int k = 0;
			private double x_step = (x_max - x_min) / (x_n - 1);
			private double y_step = (y_max - y_min) / (y_n - 1);
			private double z_step = (z_max - z_min) / (z_n - 1);

			@Override
			public boolean hasNext() {
				return ( k < z_n - 1 || j < y_n - 1 || i < x_n); 
			}

			@Override
			public Vector3D next() {
				if (i == x_n) {
					i = 0;
					j++;
				}
				if (j == y_n) {
					j = 0;
					k++;
				}
				return new Vector3D(x_min + x_step * i++, y_min + y_step * j, z_min + z_step * k);
			}
		};
	}
	

	public double[][][] toArray() {
		double[][][] array = new double[x_n][y_n][z_n];
		int i = 0;
		int j = 0;
		int k = 0;
		for (Vector3D v : this) {
			array[i][j][k] = v.x;
			if (++i == x_n) {
				i = 0;
				if (++j == y_n) {
					j = 0;
					k++;
				}
			}
		}
		return array;
	}

	public double[][] to3DData() {
		double[][] array = new double[3][x_n * y_n * z_n];
		int i = 0;
		for (Vector3D v : this) {
			array[0][i] = v.x;
			array[1][i] = v.y;
			array[2][i] = v.z;
			i++;
		}
		return array;
	}

}
