package javax.swing.plot.object.axis;

/**
 * A linear axis.
 * 
 * @author S3lios
 * @see Axis
 */
public class LinearAxis extends Axis  {
	
	double min;
	double max;

	double maxZoom = 1000000;

	/**
	 * Creates a new <code>LinearAxis</code> with default values.
	 * Min is defined as 0 and max as 10.
	 */
	public LinearAxis() {
		this(0, 10);
	}
	
	/**
	 * Creates a new <code>LinearAxis</code> with the specified range.
	 * @param min the minimum value of the axis
	 * @param max the maximum value of the axis
	 */
	public LinearAxis(double min, double max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public double getPosition(double value) {
		if (min >= max) return 0;
		return (value - min) / (max - min);
	}

	@Override
	public Double[][] getGrid() {
		int grid_size = super.getGridSize();
		Double[][] grid = new Double[grid_size][2];
		double deltaPlot = 1.0/(grid_size-1);
		double deltaReal = (max - min) / (grid_size - 1);
		for (int i = 0; i < grid_size; i++) {
			grid[i][0] = i * deltaPlot;
			grid[i][1] = min + i * deltaReal;
		}
		return grid;
	}

	@Override
	public String getGridLabel() {
		//TODO : implement
		return null;
	}

	@Override
	public void scroll(double position, double amount) {
		amount = amount/10;
		double delta = ((max - min) * amount);
		min += delta;
		max += delta;
		System.out.println("min: " + min + " max: " + max);
		System.out.println("delta: " + delta + "| max - min: " + String.format("%.2e", max - min));

	}

	@Override
	public void zoom(double position, double amount) {
		
		amount = amount/4 + 1;

		double center = (max + min) / 2;
		double size = (max - min);
		min = center - (size * amount) / 2;
		max = center + (size * amount) / 2;
	}

	@Override
	public void setMin(double min) {
		this.min = min;
	}

	@Override
	public void setMax(double max) {
		this.max = max;
	}

	@Override
	public double getMin() {
		return min;
	}

	@Override
	public double getMax() {
		return max;
	}

	
	
	

	

}
