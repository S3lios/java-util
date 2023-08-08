package javax.swing.plot.object.axis;

/**
 * A logarithmic axis.
 * 
 * @author S3lios
 * @see Axis
 */
public class LogAxis extends Axis {

	double min = 1;
	double max = Math.pow(10, 9);
	
	/**
	 * Creates a new <code>LogAxis</code> with default values.
	 * Min is defined as 1 and max as 10^9.
	 */
	public LogAxis() {
		super();
		this.setGridSize(10);
	}

	/**
	 * Creates a new <code>LogAxis</code> with the specified range.
	 * @param min the minimum value of the axis
	 * @param max the maximum value of the axis
	 */
	public LogAxis(int min, int max) {
		this();
		this.min = min;
		this.max = max;
	}


    @Override
    public Double[][] getGrid() {
        int grid_size = super.getGridSize();
        Double[][] grid = new Double[grid_size][2];

        Double deltaPlot = 1.0/(grid_size-1);
        Double deltaReal = Math.pow(max/min, 1.0/(grid_size-1));

        for (int i = 0; i < grid_size; i++) {
            grid[i][0] = i * deltaPlot;
            grid[i][1] = min * Math.pow(deltaReal, i);
        }

        return grid;
    }

	@Override
	public String getGridLabel() {
		// TODO : Implement
		return null;
	}

    @Override
    public double getPosition(double value) {
        return Math.log(value/min) / Math.log(max/min);
    }

    @Override
    public void scroll(double position, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scroll'");
    }

    @Override
    public void zoom(double position, double amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'zoom'");
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
