package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of a graph using adjacency list.
 * In this implementation, edge can have null data.
 * @see <a href="https://en.wikipedia.org/wiki/Adjacency_list"> Adjacency Matrix</a>
 * @param <V> type of the vertex data
 * @param <E> type of the edge data
 */
public class AdjListGraph<V,E> extends Graph<V, E> {

	private int indexVertexGenerator = 0;

	private Map<Integer, V> vertices;
	private Map<Integer, Map<Integer, E>> edges;


	public AdjListGraph() {
		vertices = new HashMap<>();
		edges = new HashMap<>();
	}

	public AdjListGraph(Graph<V,E> graph) {
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


	@Override
	public int addVertex(V data) {
		vertices.put(indexVertexGenerator, data);
		edges.put(indexVertexGenerator, new HashMap<>());
		return indexVertexGenerator++;
	}

	@Override
	public void addEdge(int src, int dest, E data) {
		edges.get(src).put(dest, data);
	}

	@Override
	public void removeEdge(int src, int dest) {
		edges.get(src).remove(dest);
	}

	@Override
	public void removeVertex(int vertex) {
		vertices.remove(vertex);
		edges.remove(vertex);
	}

	@Override
	public List<Integer> getNeighbors(int vertex) {
		List<Integer> neighbors = new ArrayList<>();
		for (Integer neighbor : edges.get(vertex).keySet()) {
			neighbors.add(neighbor);
		}
		return neighbors;
	}

	@Override
	public int getVertexCount() {
		return vertices.size();
	}

	@Override
	public int getEdgeCount() {
		int edgeCount = 0;
		for (Map<Integer, E> edge : edges.values()) {
			edgeCount += edge.size();
		}
		return edgeCount;
	}

	@Override
	public void mapVertices(Function<V, V> mapper) {
		for (Integer vertex : vertices.keySet()) {
			vertices.put(vertex, mapper.apply(vertices.get(vertex)));
		}
	}

	@Override
	public void mapEdges(Function<E, E> mapper) {
		for (Integer vertex : edges.keySet()) {
			for (Integer neighbor : edges.get(vertex).keySet()) {
				edges.get(vertex).put(neighbor, mapper.apply(edges.get(vertex).get(neighbor)));
			}
		}
	}

	@Override
	public void forEachVertex(Function<V, Void> consumer) {
		for (Integer vertex : vertices.keySet()) {
			consumer.apply(vertices.get(vertex));
		}
	}

	@Override
	public void forEachEdge(Function<E, Void> consumer) {
		for (Integer vertex : edges.keySet()) {
			for (Integer neighbor : edges.get(vertex).keySet()) {
				consumer.apply(edges.get(vertex).get(neighbor));
			}
		}
	}

	@Override
	public void print() {
		for (Integer vertex : vertices.keySet()) {
			System.out.print(vertex + " -> ");
			for (Integer neighbor : edges.get(vertex).keySet()) {
				System.out.print(neighbor + ", ");
			}
			System.out.println();
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		return vertices.keySet().iterator();
	}

	@Override
	public V getVertexData(int vertex) {
		return vertices.get(vertex);
	}

	@Override
	public E getEdgeData(int src, int dest) {
		return edges.get(src).get(dest);
	}

}
