package awt;

import java.awt.Graphics;

import math.geometry.Vector2D;



public class Drawner {

	private Graphics g;

	public Drawner(Graphics g) {
		this.g = g;
	}
	
	public void drawScatterLine(int x1, int y1, int x2, int y2, int lineSize,  int jumpSize) {
		Vector2D v1 = new Vector2D(x1, y1);
		Vector2D v2 = new Vector2D(x2, y2);

		Vector2D v = v2.sub(v1);

		double n = v.length() / (jumpSize+lineSize);

		for (int i = 0; i < n; i+= 2) {
			Vector2D v3 = v1.add(v.mul(i/n));
			Vector2D v4 = v1.add(v.mul((i+1)/n));
			drawLine((int)v3.x, (int)v3.y, (int)v4.x, (int)v4.y);
		}
	}


	/** GRAPHICS FUNCTION */
	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	public void drawString(String str, int x, int y) {
		g.drawString(str, x, y);
	}

	public void drawPoint(int x, int y) {
		g.drawLine(x, y, x, y);
	}

	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	public void fillOval(int x, int y, int width, int height) {
		g.fillOval(x, y, width, height);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		g.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolygon(xPoints, yPoints, nPoints);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		g.drawPolyline(xPoints, yPoints, nPoints);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		g.draw3DRect(x, y, width, height, raised);
	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		g.fill3DRect(x, y, width, height, raised);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		g.drawChars(data, offset, length, x, y);
	}

	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		g.drawBytes(data, offset, length, x, y);
	}

	public void translate(int x, int y) {
		g.translate(x, y);
	}

	/** END GRAPHICS FUNCTION */

	
}
