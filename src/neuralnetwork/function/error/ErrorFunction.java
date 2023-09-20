package neuralnetwork.function.error;

import java.io.Serializable;

public interface ErrorFunction extends Serializable{

	public double error(double[] output, double[] target);

	public double[] errorGradient(double[] output, double[] target);
	
}
