package javax.swing.panel;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import data.Pair;
import functionnal.TriProcedure;
import data.struct.graph.AdjListGraph;
import data.struct.graph.Graph;

public class JGraph<V,E> extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener, KeyListener   {
	
	Graph<V,E> graph;
	Graph<Pair<Double, Double>, Void> posGraph;
	TriProcedure<Graphics, V, Pair<Integer, Integer>> vertexDrawer;
	TriProcedure<Graphics, E, Pair<Integer, Integer>> edgeDrawer;


	public JGraph() {
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
	}

	public JGraph(Graph<V,E> graph) {
		this();
		this.graph = graph;
		this.vertexDrawer = (g, v, p) -> {g.drawString(v.toString(), p.first, p.second);};
		this.edgeDrawer = (g, e, p) -> {g.drawString(e.toString(), p.first, p.second);};
		this.update();
	}

	public JGraph(Graph<V,E> graph, TriProcedure<Graphics, V, Pair<Integer, Integer>> vertexDrawer, TriProcedure<Graphics, E, Pair<Integer, Integer>> edgeDrawer) {
		this();
		this.graph = graph;
		this.vertexDrawer = vertexDrawer;
		this.edgeDrawer = edgeDrawer;
		this.update();
	}

	public void update() {
		this.posGraph = this.calcGraph();
		this.repaint();
	}

	public void setGraph(Graph<V,E> graph) {
		this.graph = graph;
		this.update();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (this.posGraph == null) return;

		for (Integer vertex : this.posGraph) {
			int x = (int) (double) posGraph.getVertexData(vertex).first;
			int y = (int) (double) posGraph.getVertexData(vertex).second;
			this.vertexDrawer.apply(g, this.graph.getVertexData(vertex), new Pair<>(x, y));
		}

		for (Pair<Integer, Integer> edge : this.posGraph.getEdges()) {
			int xSrc = (int) (double) posGraph.getVertexData(edge.first).first;
			int ySrc = (int) (double) posGraph.getVertexData(edge.first).second;
			int xDest = (int) (double) posGraph.getVertexData(edge.second).first;
			int yDest = (int) (double) posGraph.getVertexData(edge.second).second;
			g.drawLine(xSrc, ySrc, xDest, yDest);
			this.edgeDrawer.apply(g, this.graph.getEdgeData(edge.first, edge.second), new Pair<>((xSrc + xDest) / 2, (ySrc + yDest) / 2));
		}

	}

	private Graph<Pair<Double, Double>, Void> calcGraph() {
		Set<Integer> visited = new HashSet<>();
		Graph<Pair<Double, Double>, Void> graph = new AdjListGraph<>();

		int n = this.graph.getVertexCount();
		for (int x = 0; x < n; x++) {
			graph.addVertex(new Pair<>(0., 0.));
		}

		graph.getVertexData(0).first = (double) this.getWidth() / 2;
		graph.getVertexData(0).second = (double) 20;

		this.calcGraph(graph, 0,  20, this.getWidth(), 100, this.getHeight(), visited);
		return graph;
	}

	private void calcGraph(Graph<Pair<Double, Double>, Void> graph, int vertex, int minX, int maxX, int minY, int maxY,  Set<Integer> visited) {
		visited.add(vertex);
		List<Integer> neighbors = this.graph.getNeighbors(vertex);
		int n = neighbors.size();
		if (n == 0) return;
		int dx = (maxX - minX) / n;
		int x = minX;
		for (int neighbor : neighbors) {
			if (!visited.contains(neighbor)) {
				graph.getVertexData(neighbor).first = (double) x + dx / 2;
				graph.getVertexData(neighbor).second = (double) minY;
				graph.addEdge(vertex, neighbor, null);
				this.calcGraph(graph, neighbor, x, x + dx, minY + 80, maxY , visited);
				x += dx;
			} else {
				graph.addEdge(vertex, neighbor, null);
			}
		}
	}

	private void rescale(double factor, int x, int y) {
		if (this.posGraph == null) return;
		for (Integer vertex : this.posGraph) {
			this.posGraph.getVertexData(vertex).first = (this.posGraph.getVertexData(vertex).first - x) * factor + x;
			this.posGraph.getVertexData(vertex).second = (this.posGraph.getVertexData(vertex).second - y) * factor + y;
		}

		this.repaint();
	}

	private void shift(int dx, int dy) {
		if (this.posGraph == null) return;
		for (Integer vertex : this.posGraph) {
			this.posGraph.getVertexData(vertex).first += dx;
			this.posGraph.getVertexData(vertex).second += dy;
		}

		this.repaint();
	}

	private void scretch(double factorX, int x) {
		if (this.posGraph == null) return;
		for (Integer vertex : this.posGraph) {
			this.posGraph.getVertexData(vertex).first = (this.posGraph.getVertexData(vertex).first - x) * factorX +  x;
		}
		this.repaint();
	}

	public void show() {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setVisible(true);
		this.repaint();
		this.requestFocus();
	}

	boolean isDragging = false;
	int lastX, lastY;

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isDragging) {
			this.shift(e.getX() - lastX, e.getY() - lastY);
		}
		lastX = e.getX();
		lastY = e.getY();
		isDragging = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}


	boolean isScretching = false;
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (isScretching) {
			this.scretch(1 - e.getWheelRotation() * 0.1, e.getX());
		} else {
			this.rescale(1 - e.getWheelRotation() * 0.1, e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDragging = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			isScretching = false;
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			isScretching = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			isScretching = false;
		}
	}
}
