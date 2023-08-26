package neuralnetwork.function;

public class ReLU implements ActivationFunction {
	
	@Override
	public void activate(double[] output) {
		for (int i = 0; i < output.length; i++) {
			output[i] = Math.max(0, output[i]);
		}
	}

	@Override
	public void activateGradients(double output[], double[] inputGradients) {
		for (int i = 0; i < output.length; i++) {
			if (output[i] <= 0) {
				inputGradients[i] = 0;
			}
		}
	}
}
