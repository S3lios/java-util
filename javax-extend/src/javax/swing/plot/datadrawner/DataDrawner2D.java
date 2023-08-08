package javax.swing.plot.datadrawner;

import java.awt.Graphics;


/**
 * <code>DataDrawner</code> is an abstract class that draw data in 2D.
 * @author S3lios
 * @see javax.swing.plot.JPlot2D
 * 
 */
public interface DataDrawner2D {

	/**
	 * Draws the data.
	 * 
	 * @param data the data to draw. First dimension is the data set, second dimension is the x or y data. Value are exact pixel position.
	 * @param g the graphics to draw on
	 * @param top the top of the plot. Implementations should not draw data above this value.
	 * @param left the left of the plot. Implementations should not draw data left of this value.
	 * @param bottom the bottom of the plot. Implementations should not draw data below this value.
	 * @param right the right of the plot. Implementations should not draw data right of this value.
	 */
    public void draw2D(int [][][] data, Graphics g, int[] border);
	

}
