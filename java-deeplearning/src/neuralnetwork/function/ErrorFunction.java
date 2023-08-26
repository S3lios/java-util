package neuralnetwork.function;

public interface ErrorFunction {

	public double error(double[] output, double[] target);

	public double[] errorGradient(double[] output, double[] target);
	
}
