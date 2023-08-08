package javax.swing.plot.datadrawner;

import java.awt.Graphics;

import awt.Drawner;
import math.geometry.Vector3D;

/**
 * <code>DotDataDrawner</code> is an implementation of <code>DataDrawner2D</code> that draw data as dots.
 * @author S3lios
 * @see javax.swing.plot.JPlot2D
 * @see javax.swing.plot.plot2D.datadrawner.DataDrawner2D
 */
public class DotDataDrawner implements DataDrawner2D, DataDrawner3D {


    private int dot_size = 6;


	/**
	 * Creates a new <code>DotDataDrawner</code> with default values.
	 * Dot size is defined as 4.
	 */
    public DotDataDrawner() {
        super();
    }

	/**
	 * Creates a new <code>DotDataDrawner</code> with the specified dot size.
	 * 
	 * @param dot_size the size of the dots
	 * @see #setDotSize(int)
	 * @see #getDotSize()
	 */
    public DotDataDrawner(int dot_size) {
        super();
        this.dot_size = dot_size;
    }

	/**
	 * Returns the size of the dots.
	 * 
	 * @return the size of the dots
	 * @see #DotDataDrawner(int)
	 * @see #setDotSize(int)
	 */
	public int getDotSize() {
		return dot_size;
	}

	/**
	 * Sets the size of the dots.
	 * 
	 * @param dot_size the size of the dots
	 * @see #DotDataDrawner(int)
	 * @see #getDotSize()
	 */
	public void setDotSize(int dot_size) {
		this.dot_size = dot_size;
	}

    @Override
    public void draw2D(int [][][] data, Graphics g, int[] border) {
		
		int top = border[0];
		int left = border[1];
		int bottom = border[2];
		int right = border[3];

        // Draw the data
        for (int dataSet = 0; dataSet < data.length; dataSet++) {
			int[] xData = data[dataSet][0];
			int[] yData = data[dataSet][1];

            for (int i = 0; i < xData.length; i++) {

				int x = xData[i];
				int y = yData[i];

                if (x < left || x > right || y < top || y > bottom) {
                    continue;
                }
                g.fillOval(x - dot_size/2, y - dot_size/2, dot_size, dot_size);
            } 
        }
		
    }

	@Override
	public void draw3D(double[][][] data, Graphics g, Vector3D view_direction, Vector3D right_direction, int[] border) {

		view_direction = view_direction.normalize();
		right_direction = right_direction.normalize();
		Vector3D up_direction = view_direction.cross(right_direction);

		int top = border[0];
		int left = border[1];
		int bottom = border[2];
		int right = border[3];

		int diff = Math.min(bottom - top, right - left);

		top = (bottom + top)/2 - diff/2;
		bottom = (bottom + top)/2 + diff/2;
		left = (right + left)/2 - diff/2;
		right = (right + left)/2 + diff/2;

		int middle_x = (right + left)/2;
		int middle_y = (bottom + top)/2;

		


		Vector3D plane_center = new Vector3D(0.5, 0.5, 0.5);

		// Transform the box
		Vector3D[] box = new Vector3D[] {
			new Vector3D(0, 0, 0),
			new Vector3D(1, 0, 0),
			new Vector3D(0, 1, 0),
			new Vector3D(0, 0, 1),
			new Vector3D(0.5, 0.5, 0.5),
		};

		double zs[] = new double[box.length];
		for (int i = 0; i < box.length; i++) {
			zs[i] = -box[i].sub(plane_center).dot(view_direction);
		}

		// Plot a arrow to show if the origin is in front or behind the plane
		g.drawOval(right - dot_size -10 , bottom - dot_size -10 , dot_size*2, dot_size*2);
		if (zs[3] >= 0) {
			g.fillOval(right - dot_size/4 -10, bottom - dot_size/4 -10 , dot_size/2, dot_size/2);
		} else {
			g.drawLine(right - dot_size/2 -10, bottom - dot_size/2 -10, right + dot_size/2 -10, bottom + dot_size/2 -10);
			g.drawLine(right - dot_size/2 -10, bottom + dot_size/2 -10, right + dot_size/2 -10, bottom - dot_size/2 -10);
		}


		for (int i = 0; i < box.length; i++) {
			box[i] = box[i].sub(plane_center).projectOnPlane(view_direction).changeBasis(right_direction, up_direction, view_direction).add(plane_center);
		}

		// Fit the box in the screen
		double dist_max = 0;

		for (int i = 1; i < box.length; i++) {
			double dist = box[i].sub(box[4]).length();
			if (dist > dist_max) {
				dist_max = dist;
			}
		}

		

		double scale = diff / Math.sqrt(3);
		
		Vector3D offset = new Vector3D(middle_x, middle_y, 0).sub(box[4].mul(scale));

		for (int i = 0; i < box.length-1; i++) {
			box[i] = box[i].mul(scale).add(offset);

			int size = (int) (dot_size * (1 - zs[i]/3));
			if (size < 1) {
				size = 1;
			}
			g.fillOval((int) box[i].x - size/4, (int) box[i].y - size/4, size/2, size/2);
			
			g.drawString(Integer.toString(i), (int) box[i].x, (int) box[i].y);
				
		}
		if (zs[3] >= 0) {
			g.drawLine((int) box[0].x, (int) box[0].y, (int) box[1].x, (int) box[1].y);
			g.drawLine((int) box[0].x, (int) box[0].y, (int) box[2].x, (int) box[2].y);
			g.drawLine((int) box[0].x, (int) box[0].y, (int) box[3].x, (int) box[3].y);
		} else {
			Drawner drawner = new Drawner(g);
			drawner.drawScatterLine((int) box[0].x, (int) box[0].y, (int) box[1].x, (int) box[1].y, 5, 5);
			drawner.drawScatterLine((int) box[0].x, (int) box[0].y, (int) box[2].x, (int) box[2].y, 5, 5);
			drawner.drawScatterLine((int) box[0].x, (int) box[0].y, (int) box[3].x, (int) box[3].y, 5, 5);
		}
		

		


		// Draw the data
        for (int dataSet = 0; dataSet < data.length; dataSet++) {

			double[] xData = data[dataSet][0];
			double[] yData = data[dataSet][1];
			double[] zData = data[dataSet][2];

			for (int i = 0; i < xData.length; i++) {
				Vector3D point = new Vector3D(xData[i], yData[i], zData[i]);

				Vector3D projectedPoint = point.sub(plane_center).projectOnPlane(view_direction);
				Vector3D basePoint = projectedPoint.changeBasis(right_direction, up_direction, view_direction).add(plane_center);
				Vector3D finalPoint = basePoint.mul(scale).add(offset);

				int x = (int) (finalPoint.x);
				int y = (int) (finalPoint.y);
				double z = -point.sub(plane_center).dot(view_direction);
				int size = (int) (dot_size * (1 - z/3));
				if (size < 1) {
					size = 1;
				}
				
				g.fillOval(x - size/2, y - size/2, size, size);
			}
        }
	}

	



}
