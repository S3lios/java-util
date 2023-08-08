package javax.swing.plot.datadrawner;

import java.awt.Graphics;

/**
 * <code>LineDataDrawner</code> is an implementation of <code>DataDrawner2D</code> that draw data as lines.
 * @author S3lios
 * @see javax.swing.plot.JPlot2D
 * @see javax.swing.plot.plot2D.datadrawner.DataDrawner2D
 */
public class LineDataDrawner implements DataDrawner2D {

	/**
	 * Creates a new <code>LineDataDrawner</code> with default values.
	 */
    public LineDataDrawner() {
        super();
    }

    //TODO : Change border to Rectangle
    @Override
    public void draw2D(int[][][] data, Graphics g, int[] border) {

        int top = border[0];
		int left = border[1];
		int bottom = border[2];
		int right = border[3];

        // Draw the data
        for (int dataSet = 0; dataSet < data.length; dataSet++) {
			int[] xData = data[dataSet][0];
			int[] yData = data[dataSet][1];

            for (int i = 0; i < xData.length-1; i++) {
                int x = xData[i];
				int y = yData[i];
                if (x < left || x > right || y < top || y > bottom) {
                    continue;
                }
				int x2 = xData[i+1];
				int y2 = yData[i+1];
                if (x2 < left || x2 > right || y2 < top || y2 > bottom) {
                    continue;
                }
                g.drawLine(x, y, x2, y2);
            } 
        }
		          
    }

}
