package javax.swing.plot.datadrawner;

import java.awt.Graphics;

import math.geometry.Vector3D;

public interface DataDrawner3D {

    /**
     * Draws the data.
     * @param x
     * @param y
     * @param z
     * @param view_direction the direction of the view. Supposed to be a unit vector.
     * @return
     */
    public void draw3D(double[][][] data, Graphics g, Vector3D view_direction, Vector3D right_direction, int[] border);

}
