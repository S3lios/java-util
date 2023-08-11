package graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import data.Pair;


/** 
 * A graph is a set of vertices and a set of edges. Each edge connects two vertices.
 * Vertex and edge classes can be parameterized by the types V and E to store additional information.
 * @author S3lios
 * @version 1.0
*/
public abstract class Graph<V, E> implements Cloneable, Serializable, Iterable<Integer>  {
	

	/**
	 * Add a vertex to this graph.
	 * @param data data to be stored in the vertex
	 * @return id of the vertex
	 * @implNote Add an vertex can change all vertex indices, depending on the implementation.
	 */
	public abstract int addVertex(V data);

	/**
	 * Add an edge to this graph.
	 * @param src index of the source vertex
	 * @param dest index of the destination vertex
	 * @param data data to be stored in the edge
	 */
	public abstract void addEdge(int src, int dest, E data);

	/**
	 * Get the data stored in the vertex with the given index.
	 * @param vertex index of the vertex
	 * @return data stored in the vertex
	 */
	public abstract V getVertexData(int vertex);

	/**
	 * Get the data stored in the edge with the given source and destination vertices.
	 * @param src index of the source vertex
	 * @param dest index of the destination vertex
	 * @return data stored in the edge
	 */
	public abstract E getEdgeData(int src, int dest);

	/**
	 * Remove an edge from this graph.
	 * @param src index of the source vertex
	 * @param dest index of the destination vertex
	 */
	public abstract void removeEdge(int src, int dest);

	/**
	 * Remove a vertex from this graph.
	 * @param vertex index of the vertex
	 */
	public abstract void removeVertex(int vertex);

	/**
	 * Get the data stored in the vertex with the given index.
	 * @param vertex index of the vertex
	 * @return data stored in the vertex
	 */
	public abstract List<Integer> getNeighbors(int vertex);
	
	/**
	 * Get the number of vertices in this graph.
	 * @return the number of vertices
	 */
	public abstract int getVertexCount();

	/**
	 * Get the number of edges in this graph.
	 * @return the number of edges
	 */
	public abstract int getEdgeCount();

	/**
	 * Transform data in all vertices in this graph using the given mapper.
	 * @param mapper 
	 */
	public abstract void mapVertices(Function<V,V> mapper);

	/**
	 * Transform data in all edges in this graph using the given mapper.
	 * @param mapper
	 */
	public abstract void mapEdges(Function<E,E> mapper);

	/**
	 * For each vertex in this graph, apply the given consumer.
	 * @param consumer
	 */
	public abstract void forEachVertex(Function<V,Void> consumer);

	/**
	 * For each edge in this graph, apply the given consumer.
	 * @param  consumer consumer description
	 */
	public abstract void forEachEdge(Function<E,Void> consumer);

	/**
	 * Print this graph to the console.
	 */
	public abstract void print();


	public List<Pair<Integer, Integer>> getEdges() {
		List<Pair<Integer, Integer>> edges = new ArrayList<>();
		for (Integer v : this) {
			for (Integer n : this.getNeighbors(v)) {
				edges.add(new Pair<>(v, n));
			}
		}

		return edges;
	}

	public List<Integer> getVertices() {
		List<Integer> vertices = new ArrayList<>();
		for (Integer v : this) {
			vertices.add(v);
		}
		return vertices;
	}


	/**
	 * Plot this graph.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getVertexCount(); i++) {
			sb.append(String.format("%d: %s\n", i, getVertexData(i)));
			for (int neighbor : getNeighbors(i)) {
				sb.append(String.format("\t%d: %s\n", neighbor, getEdgeData(i, neighbor)));
			}
		}
		return sb.toString();
	}

}
