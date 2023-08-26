package javax.swing.panel;


import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import iterable.Range;

public class JPlot extends JPanel implements MouseInputListener, MouseWheelListener{

	private static class InfoPoint {
		double x;
		double y;
	
		String label;
	
		public InfoPoint(double x2, double y2, String label) {
			this.x = x2;
			this.y = y2;
			this.label = label;
		}
	}


	List<Double[]> xData; // xData[i][j] = x coordinate of jth point of ith curve
	List<Double[]> yData; // yData[i][j] = y coordinate of jth point of ith curve
	List<Integer> colors; // colors[i] = color of ith curve
	List<String> labels; // labels[i] = label of ith curve

	Set<InfoPoint> infoPoints = new HashSet<>();

	int[] borders = new int[] {25, 25, 25, 25}; // borders[i] = border size of ith side (left, top, right, bottom)
	int markSize = 5;

	boolean showAxes = true;

	int moosPosX = 0;
	int moosPosY = 0;

	private double minX = Double.POSITIVE_INFINITY;
	private double maxX = Double.NEGATIVE_INFINITY;
	private double minY = Double.POSITIVE_INFINITY;
	private double maxY = Double.NEGATIVE_INFINITY;

	public JPlot() {
		super();
		this.xData = new ArrayList<>();
		this.yData = new ArrayList<>();
		this.colors = new ArrayList<>();
		this.labels = new ArrayList<>();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.setCursor( this.getToolkit().createCustomCursor(
		new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
		new Point(),
		null ) );
		this.setBackground(new java.awt.Color(255, 255, 255));
	}

	public void show() {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);
		this.requestFocus();

		// Add action clear to the context menu
		Menu contextMenu = new Menu("Clear");
		MenuItem clear = new MenuItem("Clear");
		clear.addActionListener(e -> {this.clear(); frame.repaint();});
		contextMenu.add(clear);
		frame.setMenuBar(new MenuBar());
		frame.getMenuBar().add(contextMenu);

		frame.repaint();


	}



	public void plot(double[] x, double[] y, int color, String label) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("x and y must have the same length");
		}

		Double[] xD = new Double[x.length];
		Double[] yD = new Double[y.length];
		for (int i = 0; i < x.length; i++) {
			xD[i] = x[i];
			yD[i] = y[i];
			if (x[i] < minX) minX = x[i];
			if (x[i] > maxX) maxX = x[i];
			if (y[i] < minY) minY = y[i];
			if (y[i] > maxY) maxY = y[i];
		}
		this.xData.add(xD);
		this.yData.add(yD);
		this.colors.add(color);
		this.labels.add(label);
		this.repaint();
	}

	public void plot(double[] x, double[] y, int color) {
		this.plot(x, y, color, "");
	}

	public void plot(double[] x, double[] y) {
		this.plot(x, y, 0x000000, "");
	}

	public void plot(double[] x, double[] y, String label) {
		this.plot(x, y, 0x000000, label);
	}

	public void plot(double[] y, String label) {
		double[] x = new double[y.length];
		for (int i = 0; i < y.length; i++) {
			x[i] = i;
		}
		this.plot(x, y, 0x000000, label);
	}

	public void clear() {
		this.xData.clear();
		this.yData.clear();
		this.colors.clear();
		this.labels.clear();
		this.minX = Double.POSITIVE_INFINITY;
		this.maxX = Double.NEGATIVE_INFINITY;
		this.minY = Double.POSITIVE_INFINITY;
		this.maxY = Double.NEGATIVE_INFINITY;
		this.infoPoints.clear();
		this.repaint();
	}

	public void setShowAxes(boolean showAxes) {
		this.showAxes = showAxes;
		this.repaint();
	}

	public void remove(int index) {
		this.xData.remove(index);
		this.yData.remove(index);
		this.colors.remove(index);
		this.labels.remove(index);
		this.repaint();
	}

	public void setBorders(int left, int top, int right, int bottom) {
		this.borders = new int[] {left, top, right, bottom};
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int nbSet = this.xData.size();

		double scaleX = (this.getWidth() - borders[0] - borders[2]) / (maxX - minX);
		double scaleY = (this.getHeight() - borders[1] - borders[3]) / (maxY - minY);

		for (int i = 0; i < nbSet; i++) {
			g.setColor(new java.awt.Color(this.colors.get(i)));

			for (int j = 0; j < this.xData.get(i).length - 1; j++) {
				// Plot with 0,0 in the bottom left corner
				int x1 = (int) ((this.xData.get(i)[j] - minX) * scaleX + this.borders[0]);
				int y1 = (int) ((maxY - this.yData.get(i)[j]) * scaleY + this.borders[1]);
				int x2 = (int) ((this.xData.get(i)[j + 1] - minX) * scaleX + this.borders[0]);
				int y2 = (int) ((maxY - this.yData.get(i)[j + 1]) * scaleY + this.borders[1]);

				if (x1 < this.borders[0] || x1 > this.getWidth() - this.borders[2] || y1 < this.borders[1] || y1 > this.getHeight() - this.borders[3]) continue;
				if (x2 < this.borders[0] || x2 > this.getWidth() - this.borders[2] || y2 < this.borders[1] || y2 > this.getHeight() - this.borders[3]) continue;
				g.drawLine(x1, y1, x2, y2);
			}
		}


		// box label
		int labelBoxWidth = 0;
		int labelBoxHeight = 0;

		for (int i = 0; i < nbSet; i++) {
			int sizeLabel = g.getFontMetrics().stringWidth(this.labels.get(i));
			if (sizeLabel > labelBoxWidth) labelBoxWidth = sizeLabel;
			labelBoxHeight += g.getFontMetrics().getHeight();
		}

		labelBoxWidth += 10;
		labelBoxHeight += 10;
		int labelBoxX = this.getWidth() - labelBoxWidth - borders[2];
		int labelBoxY = borders[1];	


		g.setColor(new java.awt.Color(0, 0, 0));
		g.drawRect(labelBoxX, labelBoxY, labelBoxWidth, labelBoxHeight);
		g.setColor(new java.awt.Color(255, 255, 255));
		g.fillRect(labelBoxX+1, labelBoxY+1, labelBoxWidth-1, labelBoxHeight-1);

		for (int i = 0; i < nbSet; i++) {
			g.setColor(new java.awt.Color(this.colors.get(i)));
			g.drawString(this.labels.get(i), labelBoxX + 5, labelBoxY + g.getFontMetrics().getHeight() * (i + 1));
		}




		if (!this.showAxes) return;

		g.setColor(new java.awt.Color(0, 0, 0));
		g.drawLine(this.borders[0], this.getHeight() - this.borders[3], this.getWidth() - this.borders[2], this.getHeight() - this.borders[3]);
		g.drawLine(this.borders[0], this.borders[1], this.borders[0], this.getHeight() - this.borders[3]);
		g.drawLine(this.getWidth() - this.borders[2], this.borders[1], this.getWidth() - this.borders[2], this.getHeight() - this.borders[3]);
		g.drawLine(this.borders[0], this.borders[1], this.getWidth() - this.borders[2], this.borders[1]);
		double[] xMarks = mark(minX, maxX);
		double[] yMarks = mark(minY, maxY);

		int x1;
		int y1;
		int x2;
		int y2;
		int x3;
		int y3;


		boolean inter = true;
		y1 = this.getHeight() - this.borders[3];
		if (g.getFontMetrics().getHeight() + markSize < this.borders[3]) {
			y2 = y1 + markSize;
			y3 = y1 + g.getFontMetrics().getHeight();
			inter = false;
		} else {
			y2 = y1 - markSize;
			y3 = y1 - markSize;
		}

		for (double x : xMarks) {
			x1 = (int) ((x - minX) * scaleX + this.borders[0]);
			if (x1 < borders[0] || x1 > this.getWidth() - borders[2]) continue;
			g.drawLine(x1, y1, x1, y2);
			int size = g.getFontMetrics().stringWidth(String.valueOf(x));
			if (x1 - size / 2 < borders[0] || x1 + size / 2 > this.getWidth() - borders[3]) continue;
			g.drawString(String.valueOf(x), x1 - size / 2, y3);
		}


		if (inter) {
			x1 = this.borders[0];
			x2 = x1 + markSize;
			x3 = x1 + markSize + 2;
		} else {
			x1 = this.borders[0];
			x2 = x1 - markSize;
			x3 = 0;
		}

		for (double y : yMarks) {
			y1 = (int) ((maxY - y) * scaleY + this.borders[1]);
			if (y1 < borders[1] || y1 > this.getHeight() - borders[3]) continue;
			g.drawLine(x1, y1, x2, y1);
			int size = g.getFontMetrics().getHeight();
			y3 = y1 + size/4;
			g.drawString(String.valueOf(y), x3, y3);
		}


		// Draw the mouse position
		g.setColor(new java.awt.Color(0, 0, 0));
		g.drawLine(this.moosPosX, this.getHeight() - this.borders[3], this.moosPosX, this.borders[1]);
		g.drawLine(this.borders[0], this.moosPosY, this.getWidth() - this.borders[2], this.moosPosY);

		double x = (this.moosPosX - this.borders[0]) / scaleX + minX;
		double y = maxY - (this.moosPosY - this.borders[1]) / scaleY;
		String str = String.format("(%.2f   %.2f)", x, y);
		int size = g.getFontMetrics().stringWidth(str);
		g.drawString(str, this.moosPosX - size / 2, this.moosPosY - 5);

		for (InfoPoint infoPoint : this.infoPoints) {
			g.setColor(new java.awt.Color(0, 0, 0));
			//g.drawLine(infoPoint.x, this.getHeight() - this.borders[3], infoPoint.x, this.borders[1]);
			//g.drawLine(this.borders[0], infoPoint.y, this.getWidth() - this.borders[2], infoPoint.y);
			
			int xPos = (int) ((infoPoint.x - minX) * scaleX + this.borders[0]);
			int yPos = (int) ((maxY - infoPoint.y) * scaleY + this.borders[1]);
			g.drawString(infoPoint.label, xPos, yPos);
			g.fillOval(xPos - 2, yPos - 2, 4, 4);
		}
	}

	private static double[] mark(double min, double max) {
		int pow = (int) Math.floor(Math.log10(max - min));
		int n = (int) Math.ceil((max - min) / Math.pow(10, pow));

		if (Math.abs(min + n * Math.pow(10, pow) - max) < 0.01) {
			n++;
		}

		double[] result = new double[n];

		for (int i = 0; i < n; i++) {
			result[i] = Math.ceil(min) + i * Math.pow(10, pow);
		}

		return result;
	}


	private void scretchX(double factor, int x) {
		double x0 = (x - this.borders[0]) / ((this.getWidth() - this.borders[0] - this.borders[2]) / (maxX - minX)) + minX;
		this.minX = (this.minX - x0) * factor + x0;
		this.maxX = (this.maxX - x0) * factor + x0;
		this.repaint();
	}

	private void scretchY(double factor, int y) {
		double y0 = maxY - (y - this.borders[1]) / ((this.getHeight() - this.borders[1] - this.borders[3]) / (maxY - minY));
		this.minY = (this.minY - y0) * factor + y0;
		this.maxY = (this.maxY - y0) * factor + y0;
		this.repaint();
	}

	private void rescale(double factor, int x, int y) {
		this.scretchX(factor, x);
		this.scretchY(factor, y);
	}

	private void shift(int startX, int startY, int endX, int endY) {
		double dx = (endX - startX) / ((this.getWidth() - this.borders[0] - this.borders[2]) / (maxX - minX));
		double dy = (endY - startY) / ((this.getHeight() - this.borders[1] - this.borders[3]) / (maxY - minY));
		this.minX -= dx;
		this.maxX -= dx;
		this.minY += dy;
		this.maxY += dy;
		this.repaint();
	}

	public static void main(String[] args) {
		JPlot plot = new JPlot();
		Range range = new Range(0, 100, 0.03);
		plot.plot(range.map(x -> x), range.map(x -> Math.sin(x)), 0x0000FF, "sin(x)");
		plot.plot(range.map(x -> x), range.map(x -> Math.cos(x)), 0xFF0000, "cos(x)");
		//plot.plot(range.map(x -> x), range.map(x -> Math.sin(1/x)), 0x00BB00, "sincos(x)");
		plot.show();
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		shift(this.moosPosX, this.moosPosY, e.getX(), e.getY());

		this.moosPosX = e.getX();
		this.moosPosY = e.getY();


		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.moosPosX = e.getX();
		this.moosPosY = e.getY();
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		double x = (e.getX() - this.borders[0]) / ((this.getWidth() - this.borders[0] - this.borders[2]) / (maxX - minX)) + minX;
		double y = maxY - (e.getY() - this.borders[1]) / ((this.getHeight() - this.borders[1] - this.borders[3]) / (maxY - minY));
		if (e.getButton() == MouseEvent.BUTTON1) {
			this.infoPoints.add(new InfoPoint(x, y, String.format("(%.2f   %.2f)", x, y)));
			this.repaint();
			return;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			for (InfoPoint infoPoint : this.infoPoints) {
				int ipX = (int) ((infoPoint.x - minX) * ((this.getWidth() - this.borders[0] - this.borders[2]) / (maxX - minX)) + this.borders[0]);
				int ipY = (int) ((maxY - infoPoint.y) * ((this.getHeight() - this.borders[1] - this.borders[3]) / (maxY - minY)) + this.borders[1]);
				if (Math.pow(ipX - e.getX(), 2) + Math.pow(ipY - e.getY(), 2) < 100) {
					this.infoPoints.remove(infoPoint);
					this.repaint();
					return;
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getX() < this.borders[0]) {
			// Scretch Y
			this.scretchY(1 + e.getWheelRotation() * 0.1, e.getY());
		} else if (e.getY() > this.getHeight() - this.borders[3]) {
			// Scretch X
			this.scretchX(1 + e.getWheelRotation() * 0.1, e.getX());
		} else {
			// Rescale
			this.rescale(1 + e.getWheelRotation() * 0.1, e.getX(), e.getY());
		}
	}


}
