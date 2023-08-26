package neuralnetwork.function;

public interface ActivationFunction {

	public void activate(double[] input);

	public void activateGradients(double[] output, double[] inputGradients);
}
