package neuralnetwork.function.activation;

public class ReLU implements ActivationFunction {

	double treshold = 0;
	double eps = 1e-6;

	boolean variant = false;

	public ReLU() {
	}

	public ReLU(boolean variant) {
		this.variant = variant;
	}


	public ReLU(double treshold) {
		this.treshold = treshold;
	}

	public ReLU(double treshold, boolean variant) {
		this.treshold = treshold;
		this.variant = variant;
	}

	public ReLU setEpsilon(double eps) {
		if (!variant) {
			System.out.println("Warning: the epsilon is only used in the variant of the ReLU function. Consider using the constructor with the variant parameter.");
		}
		this.eps = eps;
		if (eps < 0 || eps > 1) {
			throw new IllegalArgumentException("The epsilon must be between 0 and 1.");
		}
		return this;
	}


	@Override
	public void activate(double[] input, double[] output) {
		if (variant) {
			for (int i = 0; i < output.length; i++) {
				output[i] = Math.max(eps * (input[i]-treshold), input[i] - treshold);
			}
		} else {
			for (int i = 0; i < output.length; i++) {
				output[i] = Math.max(0, input[i] - treshold);
			}
		}
	}

	@Override
	public void activateGradients(double[] ouput, double[] input, double[] outputGradients) {
		if (variant) {
			for (int i = 0; i < input.length; i++) {
				if (input[i] <= treshold) {
					outputGradients[i] = eps * outputGradients[i];
				} else {
					outputGradients[i] = outputGradients[i];
				}
			}
		} else {
			for (int i = 0; i < input.length; i++) {
				if (input[i] <= treshold) {
					outputGradients[i] = 0;
				} else {
					outputGradients[i] = outputGradients[i];
				}
			}
		}
	}
}
