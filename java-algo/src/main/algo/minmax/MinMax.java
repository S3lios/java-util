package main.algo.minmax;

import java.util.Map;

public abstract class MinMax {
	public static int CACHE_INIT = 1000;
	
	// Cache for the score of the node
	private double __score = Double.NaN;
	private int __depth = 0;

	/**
	 * Get the score of the node, depending of his state
	 * @return the score of the node
	 */
	public abstract double getScore();

	/**
	 * Get the children of the node
	 * @return the children of the node
	 */
	public abstract MinMax[] getChildrens(); 

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
	 * @param maxDepth the max depth of the tree to evaluate. The depth can be bypassed by the method bypassDepth.
	 * @param isMaximizing true if the node is a maximizing node (i.e. search for the highest children), false otherwise (i.e. search for the lowest children).
	 * @return the best child of the node
	 */
	public final MinMax findBest(int maxDepth, boolean isMaximizing) {
		MinMax best = null;
		Map<MinMax, MinMax> __cache = new java.util.HashMap<MinMax, MinMax>(CACHE_INIT);
		double bestScore = isMaximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		for (MinMax child : this.getChildrens()) {
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
		return best;
	}

	/**
	 * Evaluate the node by using the MinMax algorithm.
	 * @param maxDepth the max depth of the tree to evaluate. The depth can be bypassed by the method bypassDepth.
	 * @param isMaximizing true if the node is a maximizing node (i.e. search for the highest children), false otherwise (i.e. search for the lowest children).
	 * @param __cache the cache of the nodes already evaluated
	 * @return the score of the node
	 */
	private final double evaluate(int maxDepth, boolean isMaximizing, Map<MinMax, MinMax> __cache) {

		// Check if the node has been evaluated before
		MinMax cache;
		if ((cache = __cache.get(this)) != null && cache.__getDepth() <= maxDepth) {
			this.__score = cache.__getScore();
			return this.__score;
		}

		// Check if the node is a leaf
		if (maxDepth <= 0  && !this.bypassDepth(-maxDepth)) {
			this.__score = this.getScore();
			__cache.put(this, this);
			return this.getScore();
		}

		// Check if the node can be the solution with the lower and upper bounds
		// TODO

		MinMax[] children = this.getChildrens();
		
		// Check if the node is a leaf
		if (children.length == 0) {
			this.__score = this.getScore();
			return this.getScore();
		}

		double bestScore;
		if (isMaximizing) {
			bestScore = Double.NEGATIVE_INFINITY;
			for (MinMax child : this.getChildrens()) {
				double score = child.evaluate(maxDepth - 1, false, __cache);
				bestScore = Math.max(bestScore, score);
			}
		} else {
			bestScore = Double.POSITIVE_INFINITY;
			for (MinMax child : this.getChildrens()) {
				double score = child.evaluate(maxDepth - 1, true, __cache);
				bestScore = Math.min(bestScore, score);
			}
		}

		this.__score = bestScore;
		this.__depth = maxDepth;
		__cache.put(this, this);

		return bestScore;
	}

	private final double __getScore() {
		return this.__score;
	}

	private final int __getDepth() {
		return this.__depth;
	}
}