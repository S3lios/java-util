package neuralnetwork.function.activation;

public class Affine implements ActivationFunction {

	double a = 1;
	double b = 0;

	@Override
	public void activate(double[] input, double output[]) {
		for (int i = 0; i < input.length; i++) {
			output[i] = a * input[i] + b;
		}
	}

	@Override
	public void activateGradients(double[] input, double[] output, double[] outputGradients) {
		for (int i = 0; i < input.length; i++) {
			outputGradients[i] = a * outputGradients[i];
		}
	}
	
}
