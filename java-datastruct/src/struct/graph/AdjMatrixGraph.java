package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;


/**
 * Implementation of a graph using an adjacency matrix.
 * In this implementation, edge can't have null data.
 * @see <a href="https://en.wikipedia.org/wiki/Adjacency_matrix">Adjacency Matrix</a>
 * @param <V> type of the vertex data
 * @param <E> type of the edge data
 */
public class AdjMatrixGraph<V,E> extends Graph<V,E> {

	public static final int DEFAULT_CAPACITY = 10;

	private int capacity = DEFAULT_CAPACITY;

	private int vertexCount;
	private int edgeCount;

	private V[] vertices;

	private E[][] edges;

	/**
	 * Create a new graph with the initial capacity {@value #DEFAULT_CAPACITY}.
	 * Same as calling {@code new MatrixGraph(DEFAULT_CAPACITY)}.
	 * @see #MatrixGraph(int)
	 */
	public AdjMatrixGraph() {
		this(DEFAULT_CAPACITY);
	}

	public AdjMatrixGraph(Graph<V,E> graph) {
		this();
		for (Integer vertex: graph) {
			addVertex(graph.getVertexData(vertex));
		}

		for (Integer vertex: graph) {
			for (Integer neighbor: graph.getNeighbors(vertex)) {
				addEdge(vertex, neighbor, graph.getEdgeData(vertex, neighbor));
			}
		}
	}

	/**
	 * Create a new graph with the given capacity.
	 * @param capacity
	 */
	@SuppressWarnings("unchecked")
	public AdjMatrixGraph(int capacity) {
		this.capacity = capacity;
		this.vertexCount = 0;
		this.edgeCount = 0;
		this.vertices = (V[]) new Object[capacity];
		this.edges = (E[][]) new Object[capacity][capacity];
	}


	@Override
	@SuppressWarnings("unchecked")
	public int addVertex(V data) {
		vertexCount++;
		if (vertexCount > capacity) {
			capacity *= 2;
			V[] newVertices = (V[]) new Object[capacity];
			E[][] newEdges = (E[][]) new Object[capacity][capacity];
			for (int i = 0; i < vertices.length; i++) {
				newVertices[i] = vertices[i];
				for (int j = 0; j < vertices.length; j++) {
					newEdges[i][j] = edges[i][j];
				}
			}
			vertices = newVertices;
			edges = newEdges;
		}
		vertices[vertexCount - 1] = data;
		return vertexCount - 1;
	}

	/**
	 * Add an edge to this graph.
	 * Data is stored in the edge.
	 * If data is null, the edge is removed {@code (same as this.remove(src, dest)}).
	 * @param src index of the source vertex
	 * @param dest index of the destination vertex
	 * @param data data to be stored in the edge
	 */
	@Override
	public void addEdge(int src, int dest, E data) {
		if (src < 0 || src >= vertexCount) throw new IndexOutOfBoundsException("Invalid source vertex index");
		if (dest < 0 || dest >= vertexCount) throw new IndexOutOfBoundsException("Invalid destination vertex index");
		
		if (data == null) {
			if (edges[src][dest] != null) edgeCount--;
			edges[src][dest] = null;
			return;
		} else {
			if (edges[src][dest] == null) edgeCount++;
			edges[src][dest] = data;
		}
	}

	@Override
	public int getVertexCount() {
		return vertexCount;
	}

	@Override
	public int getEdgeCount() {
		return edgeCount;
	}

	@Override
	public void mapVertices(Function<V, V> mapper) {
		for (int i = 0; i < vertexCount; i++) {
			vertices[i] = mapper.apply(vertices[i]);
		}
	}

	@Override
	public void mapEdges(Function<E, E> mapper) {
		for (int i = 0; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				if (edges[i][j] != null) edges[i][j] = mapper.apply(edges[i][j]);
			}
		}
	}

	@Override
	public void forEachVertex(Function<V, Void> consumer) {
		for (int i = 0; i < vertexCount; i++) {
			consumer.apply(vertices[i]);
		}
	}

	@Override
	public void forEachEdge(Function<E, Void> consumer) {
		for (int i = 0; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				if (edges[i][j] != null) consumer.apply(edges[i][j]);
			}
		}
	}

	@Override
	public List<Integer> getNeighbors(int vertex) {
		List<Integer> neighbors = new ArrayList<>();
		for (int i = 0; i < vertexCount; i++) {
			if (edges[vertex][i] != null) neighbors.add(i);
		}
		return neighbors;
	}

	@Override
	public void removeEdge(int src, int dest) {
		if (src < 0 || src >= vertexCount) throw new IndexOutOfBoundsException("Invalid source vertex index");
		if (dest < 0 || dest >= vertexCount) throw new IndexOutOfBoundsException("Invalid destination vertex index");

		if (edges[src][dest] != null) edgeCount--;
		edges[src][dest] = null;
	}

	/**
	 * Remove a vertex from this graph.
	 * All edges connected to this vertex are also removed.
	 * Caution: this method shifts all vertices and edges after the given vertex. Indices of vertices and edges may change.
	 * @param vertex index of the vertex
	 */
	@Override
	public void removeVertex(int vertex) {
		if (vertex < 0 || vertex >= vertexCount) throw new IndexOutOfBoundsException("Invalid vertex index");

		for (int i = 0; i < vertexCount; i++) {
			if (edges[vertex][i] != null) edgeCount--;
			edges[vertex][i] = null;
		}
		for (int i = 0; i < vertexCount; i++) {
			if (edges[i][vertex] != null) edgeCount--;
			edges[i][vertex] = null;
		}
		vertices[vertex] = null;
		vertexCount--;

		// Shift vertices and edges
		for (int i = vertex; i < vertexCount; i++) {
			vertices[i] = vertices[i + 1];
			for (int j = 0; j < vertexCount; j++) {
				edges[i][j] = edges[i + 1][j];
			}
		}

		// Shift edges
		for (int i = vertex; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				edges[i][j] = edges[i][j + 1];
			}
		}
		for (int j = vertex; j < vertexCount; j++) {
			for (int i = 0; i < vertexCount; i++) {
				edges[i][j] = edges[i + 1][j];
			}
		}

		// Clear last row and column
		for (int i = 0; i < vertexCount; i++) {
			edges[vertexCount][i] = null;
			edges[i][vertexCount] = null;
		}
	}

	@Override
	public void print() {
		System.out.println("Vertices:");
		for (int i = 0; i < vertexCount; i++) {
			System.out.println(i + ": " + vertices[i]);
		}
		System.out.println("Edges:");
		for (int i = 0; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				if (edges[i][j] != null) System.out.println(i + " -> " + j + ": " + edges[i][j]);
			}
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int index = 0;
			@Override
			public boolean hasNext() {
				return index < vertexCount;
			}

			@Override
			public Integer next() {
				return index++;
			}
		};
	}

	@Override
	public V getVertexData(int vertex) {
		if (vertex < 0 || vertex >= vertexCount) throw new IndexOutOfBoundsException("Invalid vertex index");
		return vertices[vertex];
	}

	@Override
	public E getEdgeData(int src, int dest) {
		if (src < 0 || src >= vertexCount) throw new IndexOutOfBoundsException("Invalid source vertex index");
		if (dest < 0 || dest >= vertexCount) throw new IndexOutOfBoundsException("Invalid destination vertex index");
		if (edges[src][dest] == null) throw new NullPointerException("Edge does not exist");
		return edges[src][dest];
	}
	
}
