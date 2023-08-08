package graph;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/** 
 * A graph is a set of vertices and a set of edges. Each edge connects two vertices.
 * Vertex and edge classes can be parameterized by the types V and E to store additional information.
 * @author S3lios
 * @version 1.0
*/
public interface Graph<V, E> extends Cloneable, Serializable, Iterable<Integer> {
	

	/**
	 * Add a vertex to this graph.
	 * @param data data to be stored in the vertex
	 * @implNote Add an vertex can change all vertex indices, depending on the implementation.
	 */
	public void addVertex(V data);

	/**
	 * Add an edge to this graph.
	 * @param src index of the source vertex
	 * @param dest index of the destination vertex
	 * @param data data to be stored in the edge
	 */
	public void addEdge(int src, int dest, E data);

	/**
	 * Get the data stored in the vertex with the given index.
	 * @param vertex index of the vertex
	 * @return data stored in the vertex
	 */
	public V getVertexData(int vertex);

	/**
	 * Get the data stored in the edge with the given source and destination vertices.
	 * @param src index of the source vertex
	 * @param dest index of the destination vertex
	 * @return data stored in the edge
	 */
	public E getEdgeData(int src, int dest);

	/**
	 * Remove an edge from this graph.
	 * @param src index of the source vertex
	 * @param dest index of the destination vertex
	 */
	public void removeEdge(int src, int dest);

	/**
	 * Remove a vertex from this graph.
	 * @param vertex index of the vertex
	 */
	public void removeVertex(int vertex);

	/**
	 * Get the data stored in the vertex with the given index.
	 * @param vertex index of the vertex
	 * @return data stored in the vertex
	 */
	public List<Integer> getNeighbors(int vertex);
	
	/**
	 * Get the number of vertices in this graph.
	 * @return the number of vertices
	 */
	public int getVertexCount();

	/**
	 * Get the number of edges in this graph.
	 * @return the number of edges
	 */
	public int getEdgeCount();

	/**
	 * Transform data in all vertices in this graph using the given mapper.
	 * @param mapper 
	 */
	public void mapVertices(Function<V,V> mapper);

	/**
	 * Transform data in all edges in this graph using the given mapper.
	 * @param mapper
	 */
	public void mapEdges(Function<E,E> mapper);

	/**
	 * For each vertex in this graph, apply the given consumer.
	 * @param consumer
	 */
	public void forEachVertex(Function<V,Void> consumer);

	/**
	 * For each edge in this graph, apply the given consumer.
	 * @param  consumer consumer description
	 */
	public void forEachEdge(Function<E,Void> consumer);

	/**
	 * Print this graph to the console.
	 */
	public void print();

}
