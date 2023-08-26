package neuralnetwork;

import neuralnetwork.function.ActivationFunction;
import neuralnetwork.function.ReLU;

public class DenseLayer extends Layer {

	public static final double LEARNING_RATE = 0.02;

	private double[] input;
	private double[] output;
	private double[][] weights;
	private double[] bias;
	private double[][] weightGradients;
	private double[] biasGradients;
	private double[] inputGradients;
	private double[] outputGradients;
	private double learningRate;

	private ActivationFunction activationFunction;


	public DenseLayer(int inputSize, int outputSize) {
		this(inputSize, outputSize, LEARNING_RATE); //
	}

	
	public DenseLayer(int inputSize, int outputSize, double learningRate) {
		this.learningRate = learningRate;
		weights = new double[outputSize][inputSize];
		bias = new double[outputSize];
		weightGradients = new double[outputSize][inputSize];
		biasGradients = new double[outputSize];
		inputGradients = new double[outputSize];
		outputGradients = new double[inputSize];
		input = new double[inputSize];
		output = new double[outputSize];
		for (int i = 0; i < outputSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				weights[i][j] = (Math.random() + 0.1)/5;
			}
			bias[i] = Math.random()/5;
		}
		activationFunction = new ReLU(); // default activation function

	}

	public DenseLayer(int inputSize, int outputSize, ActivationFunction activationFunction) {
		this(inputSize, outputSize, LEARNING_RATE, activationFunction);
	}

	public DenseLayer(int inputSize, int outputSize, double learningRate, ActivationFunction activationFunction) {
		this(inputSize, outputSize, learningRate);
		this.activationFunction = activationFunction;
	}
	
	@Override
	public void forward() {
		

		for (int i = 0; i < output.length; i++) {
			output[i] = 0;
			for (int j = 0; j < input.length; j++) {
				output[i] += input[j] * weights[i][j];
			}
			output[i] += bias[i];
		}

		if ( activationFunction != null) {
			 activationFunction.activate(output);
		}
	}
	
	@Override
	public void backward() {
		if ( activationFunction != null) {
			activationFunction.activateGradients(output, inputGradients);
		}

		for (int i = 0; i < outputGradients.length; i++) {
			outputGradients[i] = 0;
			for (int j = 0; j < inputGradients.length; j++) {
				outputGradients[i] += inputGradients[j] * weights[j][i];
			}
		}
		for (int i = 0; i < inputGradients.length; i++) {
			for (int j = 0; j < outputGradients.length; j++) {
				weightGradients[i][j] += inputGradients[i] * input[j];
			}
			biasGradients[i] = inputGradients[i];
		}
	}
	
	@Override
	public void update() {
		for (int i = 0; i < weights.length; i++) {
			for (int j = 0; j < weights[i].length; j++) {
				weights[i][j] -= weightGradients[i][j] * learningRate;
				weightGradients[i][j] = 0;
			}
			bias[i] -= biasGradients[i] * learningRate;
			biasGradients[i] = 0;
		}
	}

	@Override
	public void setInput(double[] input) {
		this.input = input;
	}

	@Override
	public double[] getOutput() {
		return output;
	}

	public void setInputGradients(double[] inputGradients) {
		this.inputGradients = inputGradients;
	}

	public double[] getOutputGradients() {
		return outputGradients;
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < weights.length; i++) {
			s += "Neuron " + i + ": ";
			for (int j = 0; j < weights[i].length; j++) {
				s += weights[i][j] + " ";
			}
			s += "b:" +  bias[i] + "\n";
		}

		return s;
	}

	public void resume() {
		String s = "";
		s += "Dense Layer " + input.length + "->" + output.length;
		if (activationFunction != null) {
			s += " (" + activationFunction.getClass().getSimpleName() + ")";
		}

		s += ": " + (input.length * output.length + output.length) + " variables";

		
		System.out.println(s);
	}


	@Override
	public int getInputDimension() {
		return input.length;
	}


	@Override
	public int getOutputDimension() {
		return output.length;
	}


	@Override
	public int getParameterCount() {
		return input.length * output.length + output.length;
	}

}
