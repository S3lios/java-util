package neuralnetwork;

import java.util.ArrayList;
import java.util.List;

import neuralnetwork.function.ErrorFunction;
import neuralnetwork.function.MinSquareError;
import neuralnetwork.function.Sigmoid;

public class NeuralNetwork {

	List<Layer> layers;

	ErrorFunction errorFunction;

	public NeuralNetwork() {
		layers = new ArrayList<Layer>();
		errorFunction = new MinSquareError();
	}

	public void addLayer(Layer layer) {
		if (layers.size() > 0 && layer.getInputDimension() != layers.get(layers.size() - 1).getOutputDimension()) {
			throw new IllegalArgumentException("The input dimension of the new layer must be equal to the output dimension of the last layer.");
		}
		layers.add(layer);
	}

	public void setErrorFunction(ErrorFunction errorFunction) {
		this.errorFunction = errorFunction;
	}

	public void forward() {
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).forward();
			if (i < layers.size() - 1)
				layers.get(i + 1).setInput(layers.get(i).getOutput());
		}
	}

	public void backward(double[] outputs) {
		layers.get(layers.size() - 1).setInputGradients(errorFunction.errorGradient(layers.get(layers.size() - 1).getOutput(), outputs));
		for (int i = layers.size() - 1; i >= 0; i--) {
			layers.get(i).backward();
			if (i > 0)
				layers.get(i - 1).setInputGradients(layers.get(i).getOutputGradients());
		}
	}

	public void update() {
		for (Layer layer : layers) {
			layer.update();
		}
	}

	public void setInput(double[] input) {
		layers.get(0).setInput(input);
	}

	public double[] getOutput() {
		return layers.get(layers.size() - 1).getOutput();
	}

	public void train(double[][] inputs, double[][] outputs, int epochs, boolean verbose) {
		train(inputs, outputs, epochs, 1, verbose);
	}

	public void train(double[][] inputs, double[][] outputs, int epochs, int batchSize, boolean verbose) {
		int count = 0;
		long start = System.currentTimeMillis();
		long end;
		double error;
		for (int i = 0; i < epochs; i++) {
			error = 0;
			for (int j = 0; j < inputs.length; j += batchSize) {
				for (int k = 0; k < batchSize && j + k < inputs.length ; k++) {
					setInput(inputs[j + k]);
					forward();
					error += errorFunction.error(getOutput(),outputs[j + k]);
					backward(outputs[j + k]);
				}
				update();
			}
			
			count++;
			end = System.currentTimeMillis();
			if (verbose)
				System.out.println("Epoch " + count + "/" + epochs + " (" + (end - start) + " ms) - error: " + error/inputs.length);
			start = end;
		}
	}

	public String toString() {
		String s = "";
		s += "--- Neural Network ---";
		for (Layer layer : layers) {
			s += "\n" +  layer.getClass().getName() + ":\n";
			s += layer.toString();
		}
		s += "--- End Neural Network ---\n";
		return s;
	}

	public void resume() {
		System.out.println("---------------------------------");
		System.out.println("Neural network resume:");
		for (Layer layer : layers) {
			layer.resume();
		}
		System.out.println("\nError function: " + errorFunction.toString());
		int totalParameters = 0;
		for (Layer layer : layers) {
			totalParameters += layer.getParameterCount();
		}
		System.out.println("Parameters: " + totalParameters);
		System.out.println("---------------------------------");
	}

	public static void main(String[] args) {
		NeuralNetwork nn = new NeuralNetwork();
		nn.addLayer(new DenseLayer(2, 2));
		//nn.addLayer(new DenseLayer(9, 9));
		//nn.addLayer(new DenseLayer(9, 9));

		nn.addLayer(new DenseLayer(2, 1, new Sigmoid()));
		// Try to learn the center of a circle
		double[][] inputs = new double[3][2];
		double[][] outputs = new double[3][1];

		inputs[0][0] = 0.5;
		inputs[0][1] = 0.5;

		inputs[1][0] = 0.1;
		inputs[1][1] = 0.1;

		inputs[2][0] = 0.9;
		inputs[2][1] = 0.9;

		// inputs[4][0] = 0.9;
		// inputs[4][1] = 0.1;

		outputs[0][0] = 1;
		outputs[1][0] = 0;
		outputs[2][0] = 0;
		
		//nn.resume();

		System.out.println(nn);

		nn.train(inputs, outputs, 2000, 1, false);

		double[] input = new double[2];
		input[0] = 0.5;
		input[1] = 0.5;
		nn.setInput(input);
		nn.forward();
		System.out.println("Output: " + nn.getOutput()[0] + " (expected 1)");

		input[0] = 0.1;
		input[1] = 0.1;

		nn.setInput(input);
		nn.forward();
		System.out.println("Output: " + nn.getOutput()[0] + " (expected 0)");

		input[0] = 0.9;
		input[1] = 0.9;

		nn.setInput(input);
		nn.forward();
		System.out.println("Output: " + nn.getOutput()[0] + " (expected 0)");
	}



}