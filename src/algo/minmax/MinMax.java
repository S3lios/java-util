package algo.minmax;

import java.util.Map;

import javax.swing.panel.JGraph;

import data.Pair;
import graph.AdjListGraph;
import graph.Graph;

public abstract class MinMax {
	public static int CACHE_INIT = 1000;
	
	// Cache for the score of the node
	double __score = Double.NaN;
	private int __depth = 0;
	private int __graphId = 0;
	private boolean __isMaximizing = true;

	/**
	 * Get the score of the node, depending of his state
	 * @return the score of the node
	 */
	public abstract double getScore();

	/**
	 * Get the children of the node
	 * @return the children of the node
	 */
	public abstract MinMax[] getChildren(); 

	/**
	 * Get the upper bound of the node. Used to prune the tree.
	 * Actually, the method is not used in the MinMax algorithm. No need to override it.
	 * @return the upper bound of the node
	 */
	public double getUpperBound() {
		return Double.POSITIVE_INFINITY;
	}

	/**
	 * Get the lower bound of the node. Used to prune the tree.
	 * Actually, the method is not used in the MinMax algorithm. No need to override it.
	 * @return the lower bound of the node
	 */
	public double getLowerBound() {
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Check if the node is allowed to be evaluated after the depth limit.
	 * If overrided, it is recommended to set a max overflow independant of the state of the node, to avoid infinite loop.
	 * @param overflow the depth overflow
	 * @return true if the node is allowed to be evaluated after the depth limit, false otherwise
	 */
	public boolean bypassDepth(int overflow) {
		return false;
	}

	/**
	 * Get the best child of the node, by using the MinMax algorithm.  
	 * To avoid evaluating the same node twice, a cache is used. To correctly use the cache, the method hashcode and equals of the node should be overrided.
	 * @param maxDepth the max depth of the tree to evaluate. The depth can be bypassed by the method bypassDepth.
	 * @param isMaximizing true if the node is a maximizing node (i.e. search for the highest children), false otherwise (i.e. search for the lowest children).
	 * @return the best child of the node
	 */
	public final Pair<MinMax, Integer> findBest(int maxDepth, boolean isMaximizing) {
		MinMax best = null;
		Map<MinMax, MinMax> __cache = new java.util.HashMap<MinMax, MinMax>(CACHE_INIT);
		double bestScore = isMaximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		for (MinMax child : this.getChildren()) {
			double score = child.evaluate(maxDepth, isMaximizing, __cache );
			if (isMaximizing) {
				if (score > bestScore) {
					bestScore = score;
					best = child;
				}
			} else {
				if (score < bestScore) {
					bestScore = score;
					best = child;
				}
			}
		}

		int index = 0;
		for (MinMax child : this.getChildren()) {
			if (child.equals(best)) break;
			index++;
		}

		return new Pair<MinMax, Integer>(best, index);
	}

	public final Graph<MinMax, Double> generateGraph(int maxDepth, boolean isMaximizing) {
		Map<MinMax, MinMax> __cache = new java.util.HashMap<MinMax, MinMax>(CACHE_INIT);
		Graph<MinMax, Double> graph = new AdjListGraph<MinMax, Double>();
		__graphId = graph.addVertex(this);
		return this.generateGraph(maxDepth, isMaximizing, __cache, graph);
	}

	private final Graph<MinMax, Double> generateGraph(int maxDepth, boolean isMaximizing, Map<MinMax, MinMax> __cache, Graph<MinMax, Double> graph) {
		this.__isMaximizing = isMaximizing;
		MinMax[] children = this.getChildren();

		if (children.length == 0) {
			// Leaf
			this.__score = this.getScore();
			this.__depth = Integer.MAX_VALUE; // Always a leaf, independant of the maxDepth
			__cache.put(this, this);
			return graph;
		}

		double bestScore = isMaximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		for (MinMax child : children) {

			MinMax __child = __cache.get(child);

			if (__child != null && __child.__depth >= maxDepth) {
				child.__score = __child.__score;
				// Child already evaluated
				graph.addEdge(__graphId, __child.__graphId, __child.__score);
				bestScore = isMaximizing ? Math.max(bestScore, __child.__score) : Math.min(bestScore, __child.__score);
				continue;
			}

			child.__graphId = graph.addVertex(child);

			if (maxDepth <= 0  && !child.bypassDepth(-maxDepth)) {
				// Child is a leaf
				child.__score = child.getScore();
				child.__depth = maxDepth;
				__cache.put(child, child);
				graph.addEdge(__graphId, graph.addVertex(child), child.getScore());
				bestScore = isMaximizing ? Math.max(bestScore, child.getScore()) : Math.min(bestScore, child.getScore());
				continue;
			}

			child.generateGraph(maxDepth, !isMaximizing, __cache, graph);
			graph.addEdge(__graphId, child.__graphId, child.__score);
			bestScore = isMaximizing ? Math.max(bestScore, child.__score) : Math.min(bestScore, child.__score);
		}
		this.__score = bestScore;
		return graph;
	}

	/**
	 * Evaluate the node by using the MinMax algorithm.
	 * For optimization, the method use a cache to avoid evaluating the same node twice. To do so, the method use a Map<MinMax, MinMax> as parameter.
	 * @param maxDepth the max depth of the tree to evaluate. The depth can be bypassed by the method bypassDepth.
	 * @param isMaximizing true if the node is a maximizing node (i.e. search for the highest children), false otherwise (i.e. search for the lowest children).
	 * @param __cache the cache of the nodes already evaluated. The cache is used to avoid evaluating the same node twice.
	 * @return the score of the node
	 */
	private final double evaluate(int maxDepth, boolean isMaximizing, Map<MinMax, MinMax> __cache) {

		// Check if the node has been evaluated before
		MinMax __child;
		if ((__child = __cache.get(this)) != null && __child.__depth >= maxDepth) {
			this.__score = __child.__score;
			return this.__score;
		}

		// Check if the node is a leaf
		if (maxDepth <= 0  && !this.bypassDepth(-maxDepth)) {
			this.__score = this.getScore();
			this.__depth = maxDepth;
			__cache.put(this, this);
			return this.getScore();
		}

		// Check if the node can be the solution with the lower and upper bounds
		// TODO

		MinMax[] children = this.getChildren();
		
		// Check if the node is a leaf
		if (children.length == 0) {
			this.__score = this.getScore();
			this.__depth = Integer.MAX_VALUE; // Always a leaf, independant of the maxDepth
			return this.getScore();
		}

		double bestScore;
		if (isMaximizing) {
			bestScore = Double.NEGATIVE_INFINITY;
			for (MinMax child : this.getChildren()) {
				double score = child.evaluate(maxDepth - 1, false, __cache);
				bestScore = Math.max(bestScore, score);
			}
		} else {
			bestScore = Double.POSITIVE_INFINITY;
			for (MinMax child : this.getChildren()) {
				double score = child.evaluate(maxDepth - 1, true, __cache);
				bestScore = Math.min(bestScore, score);
			}
		}

		this.__score = bestScore;
		this.__depth = maxDepth;
		__cache.put(this, this);
		return bestScore;
	}

	private static MinMax minMaxGenerator(int depth) {
		double score = Math.random() * 20;
		return new MinMax() {
			@Override
			public double getScore() {
				return score;
			}

			@Override
			public MinMax[] getChildren() {
				if (depth <= 0) return new MinMax[0];
				MinMax[] children = new MinMax[2];
				children[0] = minMaxGenerator(depth - 1);
				children[1] = minMaxGenerator(depth - 1);
				return children;
			}

			public String toString() {
				return String.format("MinMax(%f)", this.__score);
			}
		};
	}

	/**
	 * True if the last evaluation of the node was a maximizing one, false otherwise.
	 * @return true if the last evaluation of the node was a maximizing one, false otherwise.
	 */
	public boolean isMaximizing() {
		return this.__isMaximizing;
	}

	public static void main(String[] args) {
		MinMax root = minMaxGenerator(5);
		
		Graph<MinMax, Double> graph =  root.generateGraph(2, true);
		JGraph<MinMax, Double> panel = new JGraph<MinMax, Double>(graph, 
			(g, v, p) -> { 
				if (v.isMaximizing()) {
					g.drawRect(p.first - 5, p.second - 5, 10, 10);
				} else {
					g.fillRect(p.first - 5, p.second - 5, 10, 10);
				}
			},
			(g, e, p) -> {//Float with 2 decimals
				g.drawString(String.format("%.2f", e), p.first, p.second);
			});
			panel.show();
	}
}