package neuralnetwork.function.activation;

public class Sigmoid implements ActivationFunction {

	@Override
	public void activate(double[] input, double[] output) {
		for (int i = 0; i < input.length; i++) {
			output[i] = 1 / (1 + Math.exp(-input[i]));
		}
	}

	@Override
	public void activateGradients(double[] input, double[] output, double[] outputGradients) {
		for (int i = 0; i < input.length; i++) {
			outputGradients[i] = output[i] * (1 - output[i]) * outputGradients[i];
		}
	}
	
}
