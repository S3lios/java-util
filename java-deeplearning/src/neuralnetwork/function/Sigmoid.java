package neuralnetwork.function;

public class Sigmoid implements ActivationFunction {

	@Override
	public void activate(double[] input) {
		for (int i = 0; i < input.length; i++) {
			input[i] = 1 / (1 + Math.exp(-input[i]));
		}
	}

	@Override
	public void activateGradients(double[] output, double[] inputGradients) {
		for (int i = 0; i < output.length; i++) {
			inputGradients[i] = output[i] * (1 - output[i]) * inputGradients[i];
		}
	}
	
}
