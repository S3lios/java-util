package javax.swing.panel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import iterable.Grid;
import iterable.Range;

import java.awt.Graphics;

public class J3DPlot extends JPanel {

	List<Double[]> xData; // x coordinates of the points
	List<Double[]> yData; // y coordinates of the points
	List<Double[]> zData; // z coordinates of the points
	List<Integer> colors; // color of the points
	List<String> labels; // labels of the points

	double xMin; // minimum x coordinate
	double xMax; // maximum x coordinate
	double yMin; // minimum y coordinate
	double yMax; // maximum y coordinate
	double zMin; // minimum z coordinate
	double zMax; // maximum z coordinate

	double[] viewPoint; // view point coordinates
	double[] viewDirection; // view direction coordinates
	double[] viewUp; // view up coordinates

	public J3DPlot() {
		this.xMin = Double.POSITIVE_INFINITY;
		this.xMax = Double.NEGATIVE_INFINITY;
		this.yMin = Double.POSITIVE_INFINITY;
		this.yMax = Double.NEGATIVE_INFINITY;
		this.zMin = Double.POSITIVE_INFINITY;
		this.zMax = Double.NEGATIVE_INFINITY;
		this.viewPoint = new double[] { 0.5, 0.5, 0.5 };
		this.viewDirection = new double[] { 0, 0, 1 };
		this.viewUp = new double[] { 0, 1, 0 };
		this.xData = new ArrayList<>();
		this.yData = new ArrayList<>();
		this.zData = new ArrayList<>();
		this.colors = new ArrayList<>();
		this.labels = new ArrayList<>();
	}

	public void plot(double[] xData, double[] yData, double[] zData, Integer colors, String labels) {
		
		if (xData.length != yData.length || xData.length != zData.length) {
			throw new IllegalArgumentException("xData, yData and zData must have the same length");
		}

		Double[] x = new Double[xData.length];
		Double[] y = new Double[yData.length];
		Double[] z = new Double[zData.length];

		for (int i = 0; i < xData.length; i++) {
			this.xMin = Math.min(this.xMin, xData[i]);
			this.xMax = Math.max(this.xMax, xData[i]);
			this.yMin = Math.min(this.yMin, yData[i]);
			this.yMax = Math.max(this.yMax, yData[i]);
			this.zMin = Math.min(this.zMin, zData[i]);
			this.zMax = Math.max(this.zMax, zData[i]);
			x[i] = xData[i];
			y[i] = yData[i];
			z[i] = zData[i];
			viewPoint = new double[] { (xMin + xMax) / 2, (yMin + yMax) / 2, (zMin + zMax) / 2 };
		}
		
		this.xData.add(x);
		this.yData.add(y);
		this.zData.add(z);
		this.colors.add(colors);
		this.labels.add(labels);
		this.repaint();
	}

	public void plot(double[] xData, double[] yData, double[] zData, Integer colors) {
		this.plot(xData, yData, zData, colors, "");
	}

	public void plot(double[] xData, double[] yData, double[] zData, String labels) {
		this.plot(xData, yData, zData, 0, labels);
	}

	public void plot(double[] xData, double[] yData, double[] zData) {
		this.plot(xData, yData, zData, 0, "");
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int width = this.getWidth();
		int height = this.getHeight();

		double[] viewDirection = this.viewDirection;
		double[] viewUp = this.viewUp;
		double[] viewPoint = this.viewPoint;
		double[] viewRight = mul(cross(viewDirection, viewUp), -1);

		double[] min = new double[] { 0, 0, 0 };
		double[] max = new double[] { 1, 1, 1 };
		
		double[] minC;
		double[] maxC;

		
		{
			double[] viewPointToPoint = sub(min, viewPoint);
			double[] projectedPoint = sub(viewPointToPoint, mul(viewDirection, dot(viewPointToPoint, viewDirection)));
			double[] projectedPointInBasis = new double[] { dot(projectedPoint, viewRight), dot(projectedPoint, viewUp) };
			minC = projectedPointInBasis;
		}

		{
			double[] viewPointToPoint = sub(max, viewPoint);
			double[] projectedPoint = sub(viewPointToPoint, mul(viewDirection, dot(viewPointToPoint, viewDirection)));
			double[] projectedPointInBasis = new double[] { dot(projectedPoint, viewRight), dot(projectedPoint, viewUp) };
			maxC = projectedPointInBasis;
		}
		
		double scaleWidth = (maxC[0] - minC[0]) / (max[0] - min[0]);


		for (int i = 0; i < this.xData.size(); i++) {
			Double[] xData = this.xData.get(i);
			Double[] yData = this.yData.get(i);
			Double[] zData = this.zData.get(i);
			Integer colors = this.colors.get(i);
			String labels = this.labels.get(i);

			for (int j = 0; j < xData.length; j++) {
				double[] point = new double[] { (xData[j]-xMin)/(xMax - xMin), (yData[j]-yMin)/(yMax - yMin), (zData[j]-zMin)/(zMax - zMin) };
				
				double[] viewPointToPoint = sub(point, viewPoint);

				//Project point on plan parallel to viewDirection. Skip this step ?
				double[] projectedPoint = sub(viewPointToPoint, mul(viewDirection, dot(viewPointToPoint, viewDirection)));

				// Get the coordinates of the point in the plan in the basis (viewRight, viewUp)
				double[] projectedPointInBasis = new double[] { dot(projectedPoint, viewRight), dot(projectedPoint, viewUp) };

				System.out.println(projectedPointInBasis[0] + " " + projectedPointInBasis[1]);

				int x = (int) (width * (projectedPointInBasis[0] - minC[0]) / (maxC[0] - minC[0]));
				int y = (int) (height * (maxC[1] - projectedPointInBasis[1]) / (maxC[1] - minC[1]));
				g.fillOval(x, y, 5, 5);
			}
		}

	}

	private static double norm(double[] v) {
		return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
	}

	private static double[] add(double[] v1, double[] v2) {
		return new double[] { v1[0] + v2[0],
			v1[1] + v2[1],
			v1[2] + v2[2],
		};
	}

	private static double[] mul(double[] v, double dot) {
		return new double[] { v[0] * dot, v[1] * dot, v[2] * dot };
	}

	private static double dot(double[] v1, double[] v2) {
		return v1[0] * v2[0] + 
			   v1[1] * v2[1] + 
			   v1[2] * v2[2];
	}

	private static double[] sub(double[] v1, double[] v2) {
		return new double[] { v1[0] - v2[0],
							  v1[1] - v2[1],
							  v1[2] - v2[2],
		};
	}

	private static double[] cross(double[] v1, double[] v2) {
		return new double[] { v1[1] * v2[2] - v1[2] * v2[1], 
							  v1[2] * v2[0] - v1[0] * v2[2], 
							  v1[0] * v2[1] - v1[1] * v2[0] };
	}


	public void show() {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);
		this.requestFocus();
		frame.repaint();
	}

	public static void main(String[] args) {

		J3DPlot plot = new J3DPlot();
		Grid grid = new Grid(-1, 1, -1, 1, 100, 100);
		plot.plot(new double[] {0, 0.5, 0, 0.5}, new double[] {0, 1 ,0, 1}, new double[] {0, 0, 1, 1}, 0, "test");
		plot.show();
	}
}
