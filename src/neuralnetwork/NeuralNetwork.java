package neuralnetwork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.swing.panel.JPlot;

import neuralnetwork.function.activation.ReLU;
import neuralnetwork.function.activation.Sigmoid;
import neuralnetwork.function.error.ErrorFunction;
import neuralnetwork.function.error.MinSquareError;


public class NeuralNetwork implements Serializable{

	private static final long serialVersionUID = 1L;

	List<Layer> layers;

	double learningRate = 0.02;

	ErrorFunction errorFunction;

	boolean hasBeenTrained = false;

	boolean bigVerbose = false;

	boolean shuffle = true;

	double lastError = 0;

	transient double[] logs = null;

	transient private boolean isInit = false;

	public NeuralNetwork() {
		layers = new ArrayList<Layer>();
		errorFunction = new MinSquareError();
	}


	// SECTION : Public setup methods
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public void setBigVerbose(boolean bigVerbose) {
		this.bigVerbose = bigVerbose;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public void addLayer(Layer layer) {
		if (layers.size() > 0 && layer.getInputDimension() != layers.get(layers.size() - 1).getOutputDimension()) {
			throw new IllegalArgumentException("Incompatible layer dimensions:" + layers.get(layers.size() - 1).getOutputDimension() + " -/-> " + layer.getInputDimension());
		}
		layers.add(layer);
	}

	public void setErrorFunction(ErrorFunction errorFunction) {
		if (hasBeenTrained) {
			System.out.println("Warning: the error function has been changed after the training. May cause unexpected results. ");
		}
		this.errorFunction = errorFunction;
	}
	// !SECTION : Public setup methods
	// SECTION : Public saving methods
	public void save(String path) throws IOException {
		save(path, false);
	}


	public void save(String path, boolean verbose) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.flush();
		oos.close();
		if (verbose) {
			System.out.println("Neural network saved in " + path);
		}
	}

	public static NeuralNetwork load(String path) throws IOException {
		return load(path, false);
	}

	public static NeuralNetwork load(String path, boolean verbose) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		NeuralNetwork nn = null;
		try {
			nn = (NeuralNetwork) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ois.close();
		if (verbose) {
			System.out.print("Neural network loaded from " + path +".");
			if (nn.hasBeenTrained) {
				System.out.print(" Last error: " + nn.lastError);
			}
			System.out.println();
		}
		return nn;
	}

	public void charge(NeuralNetwork nn) {
		this.layers = nn.layers;
		this.learningRate = nn.learningRate;
		this.errorFunction = nn.errorFunction;
		this.hasBeenTrained = nn.hasBeenTrained;
		this.bigVerbose = nn.bigVerbose;
		this.shuffle = nn.shuffle;
		this.logs = nn.logs;
		this.isInit = nn.isInit;
	}
	// !SECTION : Public saving methods

	// SECTION : Public training methods
	public void train(double[][] inputs, double[][] outputs, int epochs, int batchSize, double momemtum, boolean verbose) {
		initForTrainig();

		// Check input dimensions
		if (inputs.length != outputs.length) {
			throw new IllegalArgumentException("The number of inputs and outputs must be the same.");
		}

		if (inputs.length == 0) {
			throw new IllegalArgumentException("The number of inputs must be greater than 0.");
		}

		int wantedInputDimension = layers.get(0).getInputDimension();
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].length != wantedInputDimension) {
				throw new IllegalArgumentException("The input " + i + " has a dimension of " + inputs[i].length + " but the first layer has a dimension of " + wantedInputDimension);
			}
		}

		logs = new double[epochs];
		int count = 0;
		long start = System.currentTimeMillis();
		long globalStart = start;
		long end;

		


		for (int i = 0; i < epochs; i++) {

			if (shuffle) {
				for (int k = 0; k < inputs.length; k++) {
					int j = (int) (Math.random() * inputs.length);
					double[] temp = inputs[k];
					inputs[k] = inputs[j];
					inputs[j] = temp;
					temp = outputs[k];
					outputs[k] = outputs[j];
					outputs[j] = temp;
				}
			}
			
			lastError = 0;
			for (int j = 0; j < inputs.length; j += batchSize) {
				for (int k = 0; k < batchSize && j + k < inputs.length ; k++) {
					setInput(inputs[j + k]);
					forward();
					lastError += errorFunction.error(getOutput(),outputs[j + k]);
					backward(outputs[j + k]);
				}
				if (verbose && bigVerbose) {
					System.out.println("Epoch " + i + "/" + epochs +  "- Batch " + (j/batchSize + 1) + "/" + (inputs.length/batchSize) + " - error: " + lastError/batchSize);
					int layerIndex = 0;
					for (Layer layer : layers) {
						layerIndex++;
						System.out.println("Layer " + layerIndex + ":");
						layer.resumeGradient();
					}
				}
				update(momemtum);
			}
			logs[i] = lastError/inputs.length;
			count++;
			end = System.currentTimeMillis();
			if (verbose && !bigVerbose) {
				if (!bigVerbose) {
					System.out.println("Epoch " + count + "/" + epochs + " (" + (end - start) + " ms) - error: " + lastError/inputs.length);
				}
			}
			start = end;
		}
		end = System.currentTimeMillis();
		if (verbose) {
			System.out.println("Training finished in " + (end - globalStart) + " ms.");
			System.out.println("Modifier variables count: " + getParameterCount() * epochs);
		}
		clear();
		hasBeenTrained = true;
		isInit = false;
		lastError = lastError/inputs.length;
	}

	// !SECTION : Public training methods
	
	// SECTION : Public prediction methods
	public double[] predict(double[] input) {
		initForPredict();
		setInput(input);
		forward();
		double[] output = getOutput();
		// Clone the output to avoid the user to modify it
		double[] clone = new double[output.length];
		for (int i = 0; i < output.length; i++) {
			clone[i] = output[i];
		}
		return clone;
	}
	// !SECTION : Public prediction methods

	// SECTION : Public information methods
	public String toString() {
		String s = "";
		for (Layer layer : layers) {
			s += layer.toString() + "->";
		}
		return s;
	}

	public double[] getLogs() {
		return logs;
	}

	public double getLastError() {
		return lastError;
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

	public void showLogs() {
		JPlot plot = new JPlot();
		plot.setSize(500, 500);
		plot.plot(logs, 0x000000, "Error by epoch");
		plot.show();
	}
	// !SECTION : Public information methods
	// SECTION : Private training methods
	private void forward() {
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).forward();
			if (i < layers.size() - 1)
				layers.get(i + 1).setInput(layers.get(i).getOutput());
		}
	}

	private void backward(double[] outputs) {
		layers.get(layers.size() - 1).setOutputGradients(errorFunction.errorGradient(layers.get(layers.size() - 1).getOutput(), outputs));
		for (int i = layers.size() - 1; i >= 0; i--) {
			layers.get(i).backward();
			if (i > 0)
				layers.get(i - 1).setOutputGradients(layers.get(i).getInputGradients());
		}
	}

	private void update(double momemtum) {
		for (Layer layer : layers) {
			layer.update(this.learningRate, momemtum);
		}
	}
	// !SECTION : Private training methods

	// SECTION : Private prediction methods
	private void setInput(double[] input) {
		layers.get(0).setInput(input);
	}

	private double[] getOutput() {
		return layers.get(layers.size() - 1).getOutput();
	}
	// !SECTION : Private prediction methods

	// SECTION : Private information methods
	private int getParameterCount() {
		int count = 0;
		for (Layer layer : layers) {
			count += layer.getParameterCount();
		}
		return count;
	}
	// !SECTION : Private information methods


	// SECTION : Private state methods
	private void initForTrainig() {
		for (Layer layer : layers) {
			layer.initForTraining();
		}
		isInit = true;
	}

	private void initForPredict() {
		if (isInit) return;
		for (Layer layer : layers) {
			layer.initForPredict();
		}
		isInit = true;
	}

	private void clear() {
		for (Layer layer : layers) {
			layer.clear();
		}
	}
	// !SECTION : Private state methods


	public static void main(String[] args) {
		main1();
	}

	public static void main1() {
		NeuralNetwork nn = new NeuralNetwork();
		//nn.addLayer(new DenseLayer(2, 2, new ReLU()));
		//nn.addLayer(new ConvolutionalLayer(new int[] {2,2}, new int[] {2,2}, 1, new int[] {1,1}, new int[] {1,1}, new ReLU(true)));
		nn.addLayer(new DenseLayer(2, 50, new ReLU(true)));
		nn.addLayer(new DenseLayer(50, 10, new ReLU(true)));
		nn.addLayer(new DenseLayer(10, 1, new Sigmoid()));
		

		Function<double[], Double> f = (x) -> {
			// 1 if x is in one of the two circle of radius 0.5 centered in (0.5, 0.5) and (-0.5, -0.5)
			// 0 otherwise
			if (x[0] * x[0] + x[1] * x[1] < 0.1) {
				return 0.0;
			}
			if ((x[0] - 0.3) * (x[0] - 0.3) + (x[1] - 0.3) * (x[1] - 0.3) < 0.3) {
				return 1.0;
			}
			if ((x[0] + 0.3) * (x[0] + 0.3) + (x[1] + 0.3) * (x[1] + 0.3) < 0.3) {
				return 1.0;
			}
			return 0.0;
		};

		// Try to learn the center of a circle
		int n = 10000;
		double[][] inputs = new double[n][2];
		double[][] outputs = new double[n][1];

		for (int i = 0; i < n; i++) {
			double x = Math.random() * 4 - 2;
			double y = Math.random() * 4 - 2;
			inputs[i][0] = x;
			inputs[i][1] = y;
			outputs[i][0] = f.apply(new double[] {x, y});
		}

		nn.train(inputs, outputs, 100, 20, 0.1, true);

		nn.resume();
		nn.showLogs();
		try {
			nn.save("test.nn");
		} catch (IOException e) {
			e.printStackTrace();
		}


		List<double[]> good = new LinkedList<>();
		List<double[]> bad = new LinkedList<>();
		for (int i = 0; i < n; i++) {
			double[] prediction = nn.predict(inputs[i]);
			if (prediction[0] > 0.5) {
				good.add(inputs[i]);
			} else {
				bad.add(inputs[i]);
			}
		}

		JPlot plot = new JPlot();
		plot.setSize(500, 500);
		plot.plotXY(good.toArray(new double[0][0]), 0x00FF00, "Good");
		plot.plotXY(bad.toArray(new double[0][0]), 0xFF0000, "Bad");
		plot.show();
	}

}