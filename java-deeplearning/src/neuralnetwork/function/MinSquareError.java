package neuralnetwork.function;

public class MinSquareError implements ErrorFunction{
	
	@Override
	public double error(double[] output, double[] target) {
		double error = 0;
		for (int i = 0; i < output.length; i++) {
			error += Math.pow(output[i] - target[i], 2);
		}
		return error;
	}

	@Override
	public double[] errorGradient(double[] output, double[] target) {
		double[] errorGradient = new double[output.length];
		for (int i = 0; i < output.length; i++) {
			errorGradient[i] = 2 * (output[i] - target[i]);
		}
		return errorGradient;
	}

	public String toString() {
		return "MinSquareError";
	}

}
