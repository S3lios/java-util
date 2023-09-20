package neuralnetwork.function.activation;

import java.io.Serializable;

public interface ActivationFunction extends Serializable{

	public void activate(double[] input, double[] output);

	public void activateGradients(double[] input, double[] output, double[] outputGradients);
}
