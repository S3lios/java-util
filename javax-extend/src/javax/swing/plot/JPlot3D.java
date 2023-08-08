package javax.swing.plot;


import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.plot.datadrawner.DataDrawner2D;
import javax.swing.plot.datadrawner.DataDrawner3D;
import javax.swing.plot.datadrawner.DotDataDrawner;
import javax.swing.plot.object.axis.Axis;
import javax.swing.plot.object.axis.LinearAxis;

import math.Mesh;
import math.geometry.Vector3D;


/**
 * <code>JPlot3D</code> is a panel that can be used to plot 3D data.
 * J2DPlot is a little inspired by the <a href="https://matplotlib.org/">matplotlib </a> library> and more specifically by the <a href="https://matplotlib.org/api/_as_gen/matplotlib.pyplot.plot.html">plot</a> function.
 * There is also a <code>JPlot3D</code> class that can be used to plot 3D data.
 * @author S3lios
 * @version 1.0
 * @see J3DPlot
 */
public class JPlot3D extends JPlot implements java.awt.event.KeyListener {
	

	public static final int X_AXIS_TICK = 3;
	public static final int Y_AXIS_TICK = 3;
	public static final int Z_AXIS_TICK = 3;

	public static final int X_AXIS_LABEL_OFFSET = 10;
	public static final int Y_AXIS_LABEL_OFFSET = 35;
	public static final int Z_AXIS_LABEL_OFFSET = 10;

	Vector3D view_direction = new Vector3D(0.1, -0.2, 1);
	Vector3D right_direction = new Vector3D(1,0, 0).projectOnPlane(view_direction);


	/**
	 * The x axis of the plot.
	 */
	Axis xAxis;

	/**
	 * The y axis of the plot.
	 */
	Axis yAxis;

	/**
	 * The z axis of the plot.
	 */
	Axis zAxis;

	/**
	 * The data to plot. The first index is the data set and the second index is the x or y data.
	 */
	double [][][] data;

	/**
	 * The data drawner. Wich is used to draw the data.
	 */
	DataDrawner3D dataDrawner;

	/**
	 * Creates a new <code>JPlot3D</code> with default parameters.
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
	public JPlot3D() {
		super();
		this.setFocusable(true);
		this.xAxis = new LinearAxis();
		this.yAxis = new LinearAxis();
		this.zAxis = new LinearAxis();
		this.dataDrawner = new DotDataDrawner();
		this.addKeyListener(this);
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
		double minZ = Double.POSITIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;

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
				if (data[set][2][i] < minZ) {
					minZ = data[set][2][i];
				}
				if (data[set][2][i] > maxZ) {
					maxZ = data[set][2][i];
				}
			}
		}

		this.xAxis.setMin(minX);
		this.xAxis.setMax(maxX);
		this.yAxis.setMin(minY);
		this.yAxis.setMax(maxY);
		this.zAxis.setMin(minZ);
		this.zAxis.setMax(maxZ);
		this.repaint();
	}

	/**
	 * Resizes the axis to fit the data if the auto resize is set to true.
	 */ 
	protected void autoResize() {
		if (this.autoResize)
			this.resize(); // Repaint is called in resize()
		else
			this.repaint(); 
	}
	

	//ANCHOR: Useful method for the class 


	/**
	 * Sets the data. <p> 
	 * For each data set, <code>x[i]</code> and <code>y[i]</code> should have the same size.
	 * The first index of the array is the data set and the second index is the x, y and z data.
	 * Caution : the data is not copied. If you modify the data after calling this method, the plot will be modified and even corrupted.
	 * @param data the data to set, as a 3D array. First dimension is the data set, second dimension is the point index, and third dimension is the x, y or z value.
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
	 * Sets a unique data set.
	 * Caution : the data is not copied. If you modify the data after calling this method, the plot will be modified and even corrupted.
	 * @param data the data to set, as a 2D array. First dimension is the point index, and second dimension is the x, y and z value.
	 */
	public void setData(double[][] data) {
		this.setData(new double[][][] {data});
	}


	/**
	 * Sets the data with the decomposition of the x and y data.
	 * Caution : the data is not copied. If you modify the data after calling this method, the plot will be modified and even corrupted.
	 * @param xSet x data set. First dimension is the data set, second dimension is the point index.
	 * @param ySet y data set. First dimension is the data set, second dimension is the point index.
	 * @param zSet z data set. First dimension is the data set, second dimension is the point index.
	 */
	public void setData(double[][] xSet, double[][] ySet, double[][] zSet) {
		if (xSet.length != ySet.length || xSet.length != zSet.length) {
			throw new IllegalArgumentException("xSet, ySet and zSet should have the same length");
		}
		data = new double[xSet.length][3][];
		for (int i = 0; i < xSet.length; i++) {
			if (xSet[i].length != ySet[i].length || xSet[i].length != zSet[i].length) {
				throw new IllegalArgumentException("xSet[" + i + "], ySet[" + i + "] and zSet[" + i + "] should have the same length");
			}
			data[i][0] = xSet[i];
			data[i][1] = ySet[i];
			data[i][2] = zSet[i];
		}
		this.autoResize();
	}

	/**
	 * Sets a unique data set with the decomposition of the x, y and z data.
	 * Caution : the data is not copied. If you modify the data after calling this method, the plot will be modified and even corrupted.
	 * @param xSet x data set. First dimension is the point index.
	 * @param ySet y data set. First dimension is the point index.
	 * @param zSet z data set. First dimension is the point index.
	 */
	public void setData(double[] xSet, double[] ySet, double[] zSet) {
		if (xSet.length != ySet.length || xSet.length != zSet.length) {
			throw new IllegalArgumentException("xSet, ySet and zSet should have the same length");
		}
		data = new double[1][3][];
		data[0][0] = xSet;
		data[0][1] = ySet;
		data[0][2] = zSet;
		this.autoResize();
	}



	@Override	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBorder(g);
		drawData(g);
	}

	/**
	 * Draws the border of the plot.
	 * @param g the graphics to draw on
	 */
	private void drawBorder(Graphics g) {
		int width = this.getWidth();
		int height = this.getHeight();

		int top = this.top_margin;
		int bottom = height - this.bottom_margin ;
		int left = this.left_margin;
		int right = width - this.right_margin;

		g.drawRect(left, top, right - left, bottom - top);
	}

	/**
	 * Draws the data. Use the data drawner to draw the data.
	 * @param g the graphics to draw on
	 */
	private void drawData(Graphics g) {

		if (this.data == null)
			return;

		double[][][] data = new double[this.data.length][3][];

		int width = this.getWidth();
		int height = this.getHeight();

		int top = this.top_margin;
		int bottom = height - this.bottom_margin ;
		int left = this.left_margin;
		int right = width - this.right_margin;

		int[] border = new int[] {top, left, bottom, right};

		for (int dataSet = 0; dataSet < data.length; dataSet++) {
			data[dataSet][0] = new double[this.data[dataSet][0].length];
			data[dataSet][1] = new double[this.data[dataSet][1].length];
			data[dataSet][2] = new double[this.data[dataSet][2].length];
			
			for (int i = 0; i < this.data[dataSet][0].length; i++) {
				data[dataSet][0][i] = xAxis.getPosition(this.data[dataSet][0][i]);
				data[dataSet][1][i] = yAxis.getPosition(this.data[dataSet][1][i]);
				data[dataSet][2][i] = zAxis.getPosition(this.data[dataSet][2][i]);
			}
		}

		//FIXME : pixel grid
		dataDrawner.draw3D(data, g, view_direction, right_direction, border);
	}

	@Override
	public java.awt.Dimension getMinimumSize() {
		return new java.awt.Dimension(this.left_margin + this.right_margin, this.top_margin + this.bottom_margin);
	}

	// ANCHOR : Getters and setters

	/**
	 * Gets the 3D view direction.
	 * @return the view_direction
	 */
	public Vector3D getView_direction() {
		return view_direction;
	}

	/**
	 * Sets the view direction for the 3D view.
	 * @param view_direction the view_direction to set
	 */
	public void setView_direction(Vector3D view_direction) {
		this.view_direction = view_direction;
	}

	/**
	 * Gets the right direction of the 3D view.
	 * @return the right_direction
	 */
	public Vector3D getRight_direction() {
		return right_direction;
	}

	/**
	 * Sets the right direction for the 3D view.
	 * @param right_direction the right_direction to set
	 */
	public void setRight_direction(Vector3D right_direction) {
		this.right_direction = right_direction;
	}

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
	 * Sets the z axis.
	 * @param axis new z axis
	 * @see Axis
	 */
	public void setZAxis(Axis axis) {
		this.zAxis = axis;
		this.repaint();
	}

	/**
	 * Sets the data drawner.
	 * The data drawner is used to draw the data.
	 * @param dataDrawner new data drawner
	 * @see DataDrawner3D
	 */
	public void setDataDrawner(DataDrawner3D dataDrawner) {
		this.dataDrawner = dataDrawner;
		this.repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println(e.getKeyChar());

		Vector3D top_direction = this.view_direction.cross(this.right_direction);


		if (e.getKeyChar() == 'z') {
			this.view_direction = this.view_direction.rotate(this.right_direction, 0.1).normalize();
			this.right_direction = top_direction.cross(this.view_direction).normalize();
		} else if (e.getKeyChar() == 's') {
			this.view_direction = this.view_direction.rotate(this.right_direction, -0.1).normalize();
			this.right_direction = top_direction.cross(this.view_direction).normalize();
		} else if (e.getKeyChar() == 'q') {
			this.view_direction = this.view_direction.rotate(top_direction, 0.1).normalize();
			this.right_direction = top_direction.cross(this.view_direction).normalize();
		} else if (e.getKeyChar() == 'd') {
			this.view_direction = this.view_direction.rotate(top_direction, -0.1).normalize();
			this.right_direction = top_direction.cross(this.view_direction).normalize();
		} else if (e.getKeyChar() == 'a') {
			this.right_direction = this.right_direction.rotate(this.view_direction, 0.1).normalize();
		} else if (e.getKeyChar() == 'e') {
			this.right_direction = this.right_direction.rotate(this.view_direction, -0.1).normalize();
		}
		System.out.println(this.view_direction);
		this.repaint();
	}


	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}


	public static void main(String[] args) {
		JFrame frame = new JFrame("JPlot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 800);
		
		JPlot3D plot = new JPlot3D();

		frame.add(plot);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		Mesh mesh = Mesh.create(0, 3.15, 0, 3.15, 50, 50);

		plot.setView_direction(new Vector3D(1, 1, 1));
		plot.setRight_direction(new Vector3D(0, 1, 0).projectOnPlane(plot.getView_direction()));


		plot.setData(mesh.to3DData((x, y) -> Math.sin(x+y)));
		//plot.setData(Linspace.create(0, 10, 10).toArray(),Linspace.create(0, 10, 10).toArray(), Zeros.create(10).toArray());
		

	}



	



}
