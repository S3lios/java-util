package neuralnetwork;

import java.io.Serializable;

public abstract class Layer implements Serializable {

	private static final long serialVersionUID = 1L;

	transient double[] input;
	transient double[] output;
	transient double[] inputGradients;
	transient double[] outputGradients;

	public Layer() {
	}

	public abstract void forward();
	public abstract void backward();
	public abstract void update(double learningRate, double momemtum);

	public void setInput(double[] input) {
		this.input = input;
	}

	public double[] getOutput() {
		return output;
	}

	public void setOutputGradients(double[] outputGradients) {
		this.outputGradients = outputGradients;
	}

	public double[] getInputGradients() {
		return inputGradients;
	}

	public abstract int getInputDimension();
	public abstract int getOutputDimension();
	public abstract int getParameterCount();

	public void initForPredict() {
		this.output = new double[getOutputDimension()];
	}

	public void initForTraining() {
		this.input = null;
		this.output = new double[getOutputDimension()];
		this.inputGradients = new double[getInputDimension()];
		this.outputGradients = new double[getOutputDimension()];
	}


	public void clear() {
		this.input = null;
		this.output = null;
		this.inputGradients = null;
		this.outputGradients = null;
	}

	public abstract void resume();
	public void resumeGradient() {} // Optional

}
