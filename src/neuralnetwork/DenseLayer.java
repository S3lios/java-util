package neuralnetwork;

import neuralnetwork.function.activation.ActivationFunction;
import neuralnetwork.function.activation.ReLU;

public class DenseLayer extends Layer {

	private ActivationFunction activationFunction;
	private final int inputSize;
	private final int outputSize;
	
	transient double[][] weightGradients;
	transient double[] biasGradients;
	transient double[] interOutput;

	double[][] weights;
	double[] bias;

	public DenseLayer(int inputSize, int outputSize) {
		this.weights = new double[outputSize][inputSize];
		this.bias = new double[outputSize];
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		initWeight();
	}

	public DenseLayer(int inputSize, int outputSize, ActivationFunction activationFunction) {
		this(inputSize, outputSize);
		this.activationFunction = activationFunction;
	}

	@Override
	public void initForPredict() {
		this.interOutput = new double[outputSize];
		super.initForPredict();
	}

	@Override
	public void initForTraining() {
		this.biasGradients = new double[getOutputDimension()];
		this.weightGradients = new double[outputSize][inputSize];
		this.interOutput = new double[outputSize];
		super.initForTraining();
	}
	
	@Override
	public void clear() {
		this.biasGradients = null;
		this.weightGradients = null;
		this.interOutput = null;
		super.clear();
	}

	@Override
	public void forward() {

		for (int i = 0; i < outputSize; i++) {
			output[i] = 0;
			for (int j = 0; j < inputSize; j++) {
				output[i] += input[j] * weights[i][j];
			}
			output[i] += bias[i];
			//interOutput[i] = output[i];   
		}
		
		if ( activationFunction != null) {
			 activationFunction.activate(output, output);
		}
	}
	
	@Override
	public void backward() {
		if ( activationFunction != null) {
			activationFunction.activateGradients(interOutput, output, outputGradients);
		}
		
		for (int j = 0; j < inputSize; j++) {
			inputGradients[j] = 0;
		}

		for (int i = 0; i < outputSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				inputGradients[j] += outputGradients[i] * weights[i][j];
				weightGradients[i][j] += outputGradients[i] * input[j];
			}
			biasGradients[i] = outputGradients[i];
		}
	}
	
	@Override
	public void update(double learningRate, double momemtum) {
		for (int i = 0; i < outputSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				weights[i][j] -= weightGradients[i][j] * learningRate;
				weightGradients[i][j] = momemtum * weightGradients[i][j];
			}
			bias[i] -= biasGradients[i] * learningRate;
			biasGradients[i] = momemtum * biasGradients[i];
		}
	}


	@Override
	public String toString() {
		String s = "";
		s += "DL(" + inputSize + "," + outputSize + ")";
		return s;
	}

	public void resume() {
		String s = "";
		s += "Dense Layer " + inputSize + "->" + outputSize;
		if (activationFunction != null) {
			s += " (" + activationFunction.getClass().getSimpleName() + ")";
		}
		s += ": " + (inputSize * outputSize + outputSize) + " variables";
		System.out.println(s);
	}

	@Override
	public int getInputDimension() {
		return inputSize;
	}

	@Override
	public int getOutputDimension() {
		return outputSize;
	}

	@Override
	public int getParameterCount() {
		return inputSize * outputSize + outputSize;
	}

	private void initWeight() {
		for (int i = 0; i < outputSize; i++) {
			for (int j = 0; j < inputSize; j++) {
				this.weights[i][j] = (Math.random() + 0.1)/5;
			}
			this.bias[i] = Math.random()/5;
		}
		activationFunction = new ReLU(); // default activation function
	}
}
