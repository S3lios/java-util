package javax.swing.plot.object.axis;


/**
 * An axis is used to define the position of the data on a plot.
 * It is used to convert data values to pixel positions.
 * It is also used to define the grid lines.
 * @author S3lios
 * @see javax.swing.plot.JPlot2D
 */
public abstract class Axis {
	
	/**
	 * The default number of grid lines to draw
	 */
    public static final int GRID_SIZE = 11;

	private int grid_size = Axis.GRID_SIZE;

	/**
	 * Gets the number of grid lines to draw
	 * @return the number of grid lines to draw
	 */
	public int getGridSize() {
		return grid_size;
	}

	/**
	 * Sets the number of grid lines to draw
	 * @param grid_size the number of grid lines to draw
	 */
	public void setGridSize(int grid_size) {
		this.grid_size = grid_size;
	}

	/**
	 * Gets the grid lines.
	 * The grid lines are represented as a 2D array of doubles.
	 * Fist dimension is the value between 0 and 1 of the position of the grid line.
	 * Second dimension is the value of the grid line.
	 * @return the grid lines
	 */
	public abstract Double[][] getGrid();

	/**
	 * Gets the label of the grid. Used to display text next to the axis.
	 * @return the label of the grid
	 */
	public abstract String getGridLabel();

	/**
	 * Gets the position of a value on the axis.
	 * Position is a value between 0 and 1, relative to the axis.
	 * @param value the value to get the position of
	 * @return the position of the value
	 */
	public abstract double getPosition(double value);

	/**
	 * Scrolls the axis.
	 * Define the behavior of the axis when the user scrolls.
	 * @param position the position of the scroll relative to the axis (between 0 and 1)
	 * @param amount the amount of the scroll
	 */
	public abstract void scroll(double position, double amount);

	/**
	 * Zooms the axis.
	 * Define the behavior of the axis when the user zooms.
	 * @param position the position of the zoom relative to the axis (between 0 and 1)
	 * @param amount the amount of the zoom (between -1 and 1)
	 */
	public abstract void zoom(double position, double amount);

	/**
	 * Sets the minimum value of the axis
	 * @param min the minimum value of the axis
	 */
	public abstract void setMin(double min);

	/**
	 * Sets the maximum value of the axis
	 * @param max the maximum value of the axis
	 */
	public abstract void setMax(double max);

	/**
	 * Gets the minimum value of the axis
	 * @return the minimum value of the axis
	 */
	public abstract double getMin();

	/**
	 * Gets the maximum value of the axis
	 * @return the maximum value of the axis
	 */
	public abstract double getMax();
	
}