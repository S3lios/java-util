package javax.swing.plot;


import java.awt.Graphics;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.plot.datadrawner.DataDrawner2D;
import javax.swing.plot.datadrawner.LineDataDrawner;
import javax.swing.plot.object.axis.Axis;
import javax.swing.plot.object.axis.LinearAxis;

import awt.Drawner;
import math.Linspace;


/**
 * <code>JPlot2D</code> is a panel that can be used to plot 2D data.
 * J2DPlot is a little inspired by the <a href="https://matplotlib.org/">matplotlib </a> library> and more specifically by the <a href="https://matplotlib.org/api/_as_gen/matplotlib.pyplot.plot.html">plot</a> function.
 * There is also a <code>J3DPlot</code> class that can be used to plot 3D data.
 * @author S3lios
 * @version 1.0
 * @see J3DPlot
 */
public class JPlot2D extends JPlot {
	

	public static final int X_AXIS_TICK = 3;
	public static final int Y_AXIS_TICK = 3;

	public static final int X_AXIS_LABEL_OFFSET = 10;
	public static final int Y_AXIS_LABEL_OFFSET = 35;

	private int x_axis_tick = X_AXIS_TICK;
	private int y_axis_tick = Y_AXIS_TICK;

	private int x_axis_label_offset = X_AXIS_LABEL_OFFSET;
	private int y_axis_label_offset = Y_AXIS_LABEL_OFFSET;

	private boolean invertX = false;
	private boolean invertY = false;

	
	/**
	 * The x axis of the plot.
	 */
	Axis xAxis;

	/**
	 * The y axis of the plot.
	 */
	Axis yAxis;

	/**
	 * The data to plot. The first index is the data set and the second index is the x or y data.
	 */
	double [][][] data;

	/**
	 * The data drawner. Wich is used to draw the data.
	 */
	DataDrawner2D dataDrawner;

	/**
	 * Creates a new <code>J2DPlot</code> with default parameters.
	 * Basic default parameters are:
	 * <ul>
	 * <li>Horizontal axis: linear</li>
	 * <li>Vertical axis: linear</li>
	 * <li>Drawner: line</li>
	 * <li>Grid style: dotted</li>
	 * </ul>
	 * @see Axis
	 * @see DataDrawner2D
	 */
	public JPlot2D() {
		super();
		this.setFocusable(true);
		this.xAxis = new LinearAxis();
		this.yAxis = new LinearAxis();
		this.dataDrawner = new LineDataDrawner();
	}


	
	/**
	 * Resizes the axis to fit the data.
	 * @see #setAutoResize(boolean)
	 * @see #setData(double[][][])
	 */
	public void resize() {
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

		for (int set = 0; set < data.length; set++) {
			for (int i = 0; i < data[set][0].length; i++) {
				if (data[set][0][i] < minX) {
					minX = data[set][0][i];
				}
				if (data[set][0][i] > maxX) {
					maxX = data[set][0][i];
				}
				if (data[set][1][i] < minY) {
					minY = data[set][1][i];
				}
				if (data[set][1][i] > maxY) {
					maxY = data[set][1][i];
				}
			}
		}

		this.xAxis.setMin(minX);
		this.xAxis.setMax(maxX);
		this.yAxis.setMin(minY);
		this.yAxis.setMax(maxY);
		this.repaint();
	}

	/**
	 * Resizes the axis to fit the data if the auto resize is set to true.
	 */ 
	protected void autoResize() {
		if (this.autoResize) {
			double minX = Double.POSITIVE_INFINITY;
			double maxX = Double.NEGATIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;

			for (int set = 0; set < data.length; set++) {
				for (int i = 0; i < data[set][0].length; i++) {
					if (data[set][0][i] < minX) {
						minX = data[set][0][i];
					}
					if (data[set][0][i] > maxX) {
						maxX = data[set][0][i];
					}
					if (data[set][1][i] < minY) {
						minY = data[set][1][i];
					}
					if (data[set][1][i] > maxY) {
						maxY = data[set][1][i];
					}
				}
			}

			this.xAxis.setMin(minX);
			this.xAxis.setMax(maxX);
			this.yAxis.setMin(minY);
			this.yAxis.setMax(maxY);
		}
		this.repaint();
	}
	

	//ANCHOR: Useful method for the class 


	/**
	 * Sets the data. <p> 
	 * For each data set, <code>x[i]</code> and <code>y[i]</code> should have the same size.
	 * The first index of the array is the data set and the second index is the x or y data.
	 * @param data the data to set, as a 3D array. First dimension is the data set, second dimension is the point index, and third dimension is the x or y value.
	 */
	public void setData(double [][][] data) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][0].length != data[i][1].length) {
				throw new IllegalArgumentException("data[" + i + "][0] and data[" + i + "][1] should have the same length");
			}
		}
		this.data = data;
		this.autoResize();
	}


	/**
	 * Sets the data with the decomposition of the x and y data.
	 * @param xSet x data set. First dimension is the data set, second dimension is the point index.
	 * @param ySet y data set. First dimension is the data set, second dimension is the point index.
	 */
	public void setData(double[][] xSet, double[][] ySet) {
		if (xSet.length != ySet.length) {
			throw new IllegalArgumentException("xSet and ySet should have the same length");
		}
		data = new double[xSet.length][2][];
		for (int i = 0; i < xSet.length; i++) {
			if (xSet[i].length != ySet[i].length) {
				throw new IllegalArgumentException("xSet[" + i + "] and ySet[" + i +  "]should have the same length");
			}
			data[i][0] = xSet[i];
			data[i][1] = ySet[i];
		}
		this.autoResize();
	}

	/**
	 * Sets a unique data set with the decomposition of the x and y data.
	 */
	public void setData(double[] xSet, double[] ySet) {
		if (xSet.length != ySet.length) {
			throw new IllegalArgumentException("xSet and ySet should have the same length");
		}
		data = new double[1][2][];
		data[0][0] = xSet;
		data[0][1] = ySet;
		this.autoResize();
	}



	@Override	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// TODO : refactor each part of the code in a method

		drawBorder(g);
		drawGrid(g);
		drawAxis(g);
		drawData(g);
	}

	private void drawBorder(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		int top = this.top_margin;
		int bottom = height - this.bottom_margin ;
		int left = this.left_margin;
		int right = width - this.right_margin;

		g.drawRect(left, top, right - left, bottom - top);
	}

	private void drawGrid(Graphics g) {

		int width = this.getWidth();
		int height = this.getHeight();

		int top = this.top_margin;
		int bottom = height - this.bottom_margin ;
		int left = this.left_margin;
		int right = width - this.right_margin;

		// Get the grid
		Double[][] xGrid = xAxis.getGrid();
		Double[][] yGrid = yAxis.getGrid();

		int[] xPixelGrid = new int[xGrid.length];
		int[] yPixelGrid = new int[yGrid.length];
		for (int i = 0; i < xGrid.length; i++) {
			xPixelGrid[i] = (int) (xGrid[1][0] * (right - left) + left);
		}

		for (int i = 0; i < yGrid.length; i++) {
			yPixelGrid[i] = (int) (yGrid[1][0] * (bottom - top) + top);
		}


		Drawner d = new Drawner(g);
		
		// Draw the grid
		if (this.grid_style == GRID_STYLE_DOTTED) {
			for (int i = 0; i < xGrid.length; i++) {
				int x = (int) (xGrid[i][0] * (right - left) + left);
				d.drawScatterLine(x, bottom, x, top, this.grid_dotted_line_size, this.grid_dotted_jump_size);
			}

			for (int i = 0; i < yGrid.length; i++) {
				int y = (int) (yGrid[i][0] * (bottom - top) + top);
				d.drawScatterLine(left, y, right, y, this.grid_dotted_line_size, this.grid_dotted_jump_size);
			}
		} else if (this.grid_style == GRID_STYLE_FULL) {
			for (int i = 0; i < xGrid.length; i++) {
				int x = (int) (xGrid[i][0] * (right - left) + left);
				g.drawLine(x, bottom, x, top);
			}

			for (int i = 0; i < yGrid.length; i++) {
				int y = (int) (yGrid[i][0] * (bottom - top) + top);
				g.drawLine(left, y, right, y);
			}
		}

	}

	private void drawAxis(Graphics g) {

		int width = this.getWidth();
		int height = this.getHeight();

		int top = this.top_margin;
		int bottom = height - this.bottom_margin ;
		int left = this.left_margin;
		int right = width - this.right_margin;

		// Get the grid
		Double[][] xGrid = xAxis.getGrid();
		Double[][] yGrid = yAxis.getGrid();

		// Draw the axis
		g.drawLine(left, bottom, right, bottom);
		g.drawLine(left, top, left, bottom);

		for (int i = 0; i < xGrid.length; i++) {
			int x = invertX ? (int) (xGrid[i][0] * (left - right) + right) : (int) (xGrid[i][0] * (right - left) + left);

			// Draw the tick
			g.drawLine(x, bottom, x, bottom + this.x_axis_tick);

			// Draw the label
			if (xGrid[i][1] != null)
				g.drawString(String.format("%.2f", xGrid[i][1]), x - this.x_axis_tick, bottom + this.x_axis_tick + this.x_axis_label_offset);
		}

		String xGridLabel = xAxis.getGridLabel();
		if (xGridLabel != null) {
			g.drawString(xGridLabel, (right - left) / 2 + left, bottom + this.x_axis_tick + this.x_axis_label_offset);
		}


		for (int i = 0; i < yGrid.length; i++) {
			int y = invertY ? (int) (yGrid[i][0] * (bottom - top) + top) : (int) (yGrid[i][0] * (top - bottom) + bottom);

			// Draw the tick
			g.drawLine(left, y, left - this.y_axis_tick, y);

			// Draw the label
			g.drawString(String.format("%.2f", yGrid[i][1]), left - this.y_axis_tick - this.y_axis_label_offset, y);
		}

		String yGridLabel = yAxis.getGridLabel();
		if (yGridLabel != null) {
			g.drawString(yGridLabel, left - this.y_axis_tick - this.y_axis_label_offset, (bottom - top) / 2 + top);
		}
	}

	private void drawData(Graphics g) {
		int[][][] data = new int[this.data.length][2][];

		int width = this.getWidth();
		int height = this.getHeight();

		int top = this.top_margin;
		int bottom = height - this.bottom_margin ;
		int left = this.left_margin;
		int right = width - this.right_margin;

		for (int dataSet = 0; dataSet < data.length; dataSet++) {
			data[dataSet][0] = new int[this.data[dataSet][0].length];
			data[dataSet][1] = new int[this.data[dataSet][1].length];
			
			for (int i = 0; i < this.data[dataSet][0].length; i++) {
				data[dataSet][0][i] = (invertX) ? (int) (xAxis.getPosition(this.data[dataSet][0][i]) * (left - right) + right) :
													(int) (xAxis.getPosition(this.data[dataSet][0][i]) * (right - left) + left);
				data[dataSet][1][i] = (invertY) ? (int) (yAxis.getPosition(this.data[dataSet][1][i]) * (bottom - top) + top) :
													(int) (yAxis.getPosition(this.data[dataSet][1][i]) * (top - bottom) + bottom);
			}
		}

		dataDrawner.draw2D(data, g, new int[] {top, left, bottom, right});
	}

	@Override
	public java.awt.Dimension getMinimumSize() {
		return new java.awt.Dimension(this.left_margin + this.right_margin, this.top_margin + this.bottom_margin);
	}


	// ANCHOR : Getters and setters

	/**
	 * Sets horizontal axis.
	 * @param axis new horizontal axis
	 * @see Axis
	 */
	public void setXAxis(Axis axis) {
		this.xAxis = axis;
		this.repaint();
	}

	/**
	 * Sets vertical axis.
	 * @param axis new vertical axis
	 * @see Axis
	 */
	public void setYAxis(Axis axis) {
		this.yAxis = axis;
		this.repaint();
	}

	/**
	 * Sets the x axis tick.
	 * The x axis tick is the size of the tick on the x axis.
	 * @param tick new x axis tick
	 */
	public void setXAxisTick(int tick) {
		this.x_axis_tick = tick;
		this.repaint();
	}

	/**
	 * Sets the y axis tick.
	 * The y axis tick is the size of the tick on the y axis.
	 * @param tick new y axis tick
	 */
	public void setYAxisTick(int tick) {
		this.y_axis_tick = tick;
		this.repaint();
	}

	/**
	 * Sets the x axis label offset.
	 * The x axis label offset is the offset between the label and the axis.
	 * Labels are the numbers on the axis.
	 * @param offset new x axis label offset
	 */
	public void setXAxisLabelOffset(int offset) {
		this.x_axis_label_offset = offset;
		this.repaint();
	}

	/**
	 * Sets the y axis label offset.
	 * The y axis label offset is the offset between the label and the axis.
	 * @param offset new y axis label offset
	 */
	public void setYAxisLabelOffset(int offset) {
		this.y_axis_label_offset = offset;
		this.repaint();
	}

	/**
	 * Sets the invertion of the x axis.
	 * If the x axis is inverted, the x axis will be drawn from right to left.
	 * @param invert new invertion of the x axis
	 */
	public void setInvertX(boolean invert) {
		this.invertX = invert;
		this.repaint();
	}

	/**
	 * Sets the invertion of the y axis.
	 * If the y axis is inverted, the y axis will be drawn from top to bottom.
	 * @param invert new invertion of the y axis
	 */
	public void setInvertY(boolean invert) {
		this.invertY = invert;
		this.repaint();
	}


	/**
	 * Sets the data drawner.
	 * The data drawner is used to draw the data.
	 * @param dataDrawner new data drawner
	 * @see DataDrawner2D
	 */
	public void setDataDrawner(DataDrawner2D dataDrawner) {
		this.dataDrawner = dataDrawner;
		this.repaint();
	}

	

	public static void main(String[] args) {
		JFrame frame = new JFrame("JPlot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		
		JPlot2D plot = new JPlot2D();

		frame.add(plot);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		Function<Double, Double> f = (x) -> x*x;
		plot.setData(new Linspace(0, 100, 1000).toArray(), new Linspace(0, 100, 1000, f).toArray());

	}



}
