package neuralnetwork;

import neuralnetwork.function.activation.ActivationFunction;
import neuralnetwork.function.activation.ReLU;

public class ConvolutionalLayer extends Layer {
	int[] inputDimension;
	int[] outputDimension;
	int depth;
	int[] stride;
	int[] padding;
	int[] kernelDimension;

	int[] inputStepSizePadding;
	int[] inputStepSize;
	int[] kernelStepSize;

	double[][] weights;

	
	transient double[][] weightGradients;
	transient double[] interOutput;


	private final int inputSize; // real size of input
	private final int outputSize; // real size of output
	private final int kernelSize; // real size of kernel (including bias)
	//private final int hypercubeVolume; // volume of input + padding
	private final int maxCubeIndex; // maximum index of input + padding for kernel to be applied

	private ActivationFunction activationFunction;
	
	public ConvolutionalLayer(int[] inputDimension, int[] kernelDimension, int depth, int[] stride, int[] padding) {
		this(inputDimension, kernelDimension, depth, stride, padding, new ReLU());
	}


	public ConvolutionalLayer(int[] inputDimension, int[] kernelDimension, int depth, int[] stride, int[] padding, ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
		if (inputDimension.length != kernelDimension.length 
			|| inputDimension.length != stride.length 
			|| inputDimension.length != padding.length) {
			throw new IllegalArgumentException("The length of inputDimension, kernelSize, stride and padding must be the same");
		}

		// Calculate the output dimension
		int[] outputDimension = new int[inputDimension.length+1];
		for (int i = 0; i < inputDimension.length; i++) {
			outputDimension[i] = (inputDimension[i] - kernelDimension[i] + 2 * padding[i]) / stride[i] + 1;
		}
		outputDimension[inputDimension.length] = depth;
		this.inputDimension = inputDimension;
		this.outputDimension = outputDimension;
		this.kernelDimension = kernelDimension;
		this.depth = depth;
		this.stride = stride;
		this.padding = padding;

		// Calculate step size
		this.inputStepSizePadding = new int[inputDimension.length];
		inputStepSizePadding[inputDimension.length-1] = 1;
		this.inputStepSize = new int[inputDimension.length];
		inputStepSize[inputDimension.length-1] = 1;
		for (int i = inputDimension.length-2; i >= 0; i--) {
			inputStepSizePadding[i] = inputStepSize[i+1] * (inputDimension[i+1] + 2 * padding[i]);
			inputStepSize[i] = inputStepSize[i+1] * inputDimension[i+1];
		}

		this.kernelStepSize = new int[kernelDimension.length];
		kernelStepSize[kernelDimension.length-1] = 1;
		for (int i = kernelDimension.length-2; i >= 0; i--) {
			kernelStepSize[i] = kernelStepSize[i+1] * kernelDimension[i+1];
		}

		int hypercubeVolume = 1; // Volume of padding, surface of hypercube
		for (int i = 0; i < padding.length; i++) {
			hypercubeVolume *= 2*padding[i] +  inputDimension[i];
		}

		int inputSize = 1;
		for (int i = 0; i < inputDimension.length; i++) {
			if (inputDimension[i] <= 0) {
				throw new IllegalArgumentException("The input dimension must be positive");
			}
			inputSize *= inputDimension[i];
		}

		int outputSize = 1;
		for (int i = 0; i < outputDimension.length; i++) {
			if (outputDimension[i] <= 0) {
				throw new IllegalArgumentException("The output dimension must be positive");
			}
			outputSize *= outputDimension[i];
		}

		int kernelSize = 1;
		int maxCubeIndex = hypercubeVolume;
		for (int i = 0; i < kernelDimension.length; i++) {
			if (kernelDimension[i] <= 0) {
				throw new IllegalArgumentException("The kernel dimension must be positive");
			}
			kernelSize *= kernelDimension[i];
			maxCubeIndex -= inputStepSize[i] * (kernelDimension[i] - 1);
		}
		maxCubeIndex--;
		kernelSize++; // +1 for bias

		

		//this.hypercubeVolume = hypercubeVolume;
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.kernelSize = kernelSize;
		this.maxCubeIndex = maxCubeIndex;

		this.weights = new double[this.kernelSize][this.depth];
		initWeight();
	}

	private int[] InputArrIndexToDimIndex(int index, int[] indexArray) {
		for (int i = 0; i < inputDimension.length; i++) {
			if (i == 0) {
				indexArray[i] = index / inputStepSize[i];
			} else {
				indexArray[i] = (index % inputStepSize[i-1]) / inputStepSize[i];
			}
		}
		return indexArray;
	}

	private int InputDimIndexToArrIndex(int[] indexArray) {
		int index = 0;
		for (int i = 0; i < inputDimension.length; i++) {
			index += indexArray[i] * inputStepSize[i];
		}
		return index;
	}

	private int[] KernelArrIndexToDimIndex(int index, int[] indexArray) {
		for (int i = 0; i < kernelDimension.length; i++) {
			if (i == 0) {
				indexArray[i] = index / kernelStepSize[i];
			} else {
				indexArray[i] = (index % kernelStepSize[i-1]) / kernelStepSize[i];
			}
		}
		return indexArray;
	}

	@Override
	public void initForPredict() {
		this.interOutput = new double[outputSize];
		super.initForPredict();
	}

	@Override
	public void initForTraining() {
		this.weightGradients = new double[kernelSize][depth];
		this.interOutput = new double[outputSize];
		super.initForTraining();
	}
	
	@Override
	public void clear() {
		this.weightGradients = null;
		this.interOutput = null;
		super.clear();
	}



	@Override
	public void forward() {
		int outputIndex = 0;
		int[] inputDimIndex = new int[inputDimension.length];
		int[] kernelDimIndex = new int[kernelDimension.length];
		int[] incrInputIndex = new int[inputDimension.length];
		for (int depthIndex = 0; depthIndex < depth; depthIndex++) {
			for (int inputIndex = 0; inputIndex <= maxCubeIndex; inputIndex++ ) {
				InputArrIndexToDimIndex(inputIndex, inputDimIndex);
				boolean inPadding = true;
				for (int i = 0; i < inputDimension.length; i++) {
					if (inputDimIndex[i] > inputDimension[i] - padding[i]) {
						inPadding = false;
						break;
					}
				}
				if (!inPadding) {
					continue;
				}

				for (int kernelIndex = 0; kernelIndex < kernelSize; kernelIndex++) {
					KernelArrIndexToDimIndex(kernelIndex, kernelDimIndex);
					for (int i = 0; i < inputDimension.length; i++) {
						incrInputIndex[i] = inputDimIndex[i] + kernelDimIndex[i] - padding[i];
					}

					boolean inCube = true;
					for (int i = 0; i < inputDimension.length; i++) {
						if (incrInputIndex[i] < 0 || incrInputIndex[i] >= inputDimension[i]) {
							inCube = false;
							break;
						}
					}
					if (!inCube) {
						continue;
					}

					int incrInputArrIndex = InputDimIndexToArrIndex(incrInputIndex);
					output[outputIndex] += input[incrInputArrIndex]
					 * weights[kernelIndex][depthIndex];
				}
				output[outputIndex] += weights[kernelSize-1][depthIndex]; // bias
				interOutput[outputIndex] = output[outputIndex];   
				outputIndex++;
			}
		}
		if ( activationFunction != null) {
			activationFunction.activate(output, output);
		}
	}


	//FIXME - this is not correct. Always 0.
	@Override
	public void backward() {

		if ( activationFunction != null) {
			activationFunction.activateGradients(interOutput, output, outputGradients);
		}

		int outputIndex = 0;
		int[] inputDimIndex = new int[inputDimension.length];
		int[] kernelDimIndex = new int[kernelDimension.length];
		int[] incrInputIndex = new int[inputDimension.length];
		for (int depthIndex = 0; depthIndex < depth; depthIndex++) {
			for (int inputIndex = 0; inputIndex <= maxCubeIndex; inputIndex++ ) {
				InputArrIndexToDimIndex(inputIndex, inputDimIndex);
				boolean inPadding = true;
				for (int i = 0; i < inputDimension.length; i++) {
					if (inputDimIndex[i] > inputDimension[i] - padding[i]) {
						inPadding = false;
						break;
					}
				}
				if (!inPadding) {
					continue;
				}

				for (int kernelIndex = 0; kernelIndex < kernelSize; kernelIndex++) {
					KernelArrIndexToDimIndex(kernelIndex, kernelDimIndex);
					for (int i = 0; i < inputDimension.length; i++) {
						incrInputIndex[i] = inputDimIndex[i] + kernelDimIndex[i] - padding[i];
					}

					boolean inCube = true;
					for (int i = 0; i < inputDimension.length; i++) {
						if (incrInputIndex[i] < 0 || incrInputIndex[i] >= inputDimension[i]) {
							inCube = false;
							break;
						}
					}
					if (!inCube) {
						continue;
					}

					int incrInputArrIndex = InputDimIndexToArrIndex(incrInputIndex);
					inputGradients[incrInputArrIndex] += outputGradients[outputIndex] * weights[kernelIndex][depthIndex];
					weightGradients[kernelIndex][depthIndex] += outputGradients[outputIndex] * input[incrInputArrIndex];
				}
				outputIndex++;
			}
		}

	}

	@Override
	public void update(double learningRate, double momemtum) {
		//System.out.println("weightGradients: " + Arrays.deepToString(weightGradients));
		for (int i = 0; i < kernelSize; i++) {
			for (int j = 0; j < depth; j++) {
				weights[i][j] -= weightGradients[i][j] * learningRate;
				weightGradients[i][j] = momemtum * weightGradients[i][j];
			}
		}
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
		return kernelSize * depth;
	}

	@Override
	public void resume() {
		String s = "";
		s += "Convo Layer ";
		for (int i = 0; i < inputDimension.length; i++) {
			s += inputDimension[i] + "x";
		}
		s = s.substring(0, s.length()-1);
		s += "->";
		for (int i = 0; i < outputDimension.length-1; i++) {
			s += outputDimension[i] + "x";
		}
		s = s.substring(0, s.length()-1);
		s += "[" + depth + "]";
		if (activationFunction != null) {
			s += " (" + activationFunction.getClass().getSimpleName() + ")";
		}
		s += " - kernel ";
		for (int i = 0; i < kernelDimension.length; i++) {
			s += kernelDimension[i] + "x";
		}
		s = s.substring(0, s.length()-1);
		s += " - stride ";
		for (int i = 0; i < stride.length; i++) {
			s += stride[i] + "x";
		}
		s = s.substring(0, s.length()-1);
		s += " - padding ";
		for (int i = 0; i < padding.length; i++) {
			s += padding[i] + "x";
		}
		s = s.substring(0, s.length()-1);
		s += ": " + (kernelSize * depth) + " variables";
		
		
		System.out.println(s);




	}


	private void initWeight() {
		for (int i = 0; i < depth; i++) {
			for (int j = 0; j < kernelSize; j++) {
				this.weights[j][i] = 1;
				if (j == kernelSize - 1) {
					this.weights[j][i] = 0;
				}
			}
		}
	}


	public static void main(String[] args) {
		// Test forward
		int[] inputDimension = new int[] { 5, 5 };
		int[] kernelDimension = new int[] { 3, 3 };
		int depth = 2;
		int[] stride = new int[] { 1, 1 };
		int[] padding = new int[] { 1, 1 };
		ConvolutionalLayer layer = new ConvolutionalLayer(inputDimension, kernelDimension, depth, stride, padding);
		layer.setInput(new double[] { 1, 2, 3, 4, 5, 
									  6, 7, 8, 9, 10, 
									  11, 12, 13, 14, 15,
									  -1, -2, -3, -4, -5,
									  -6, -7, -8, -9, -10 });
		layer.initForPredict();
		layer.forward();
		double[] output = layer.getOutput();
		for (int i = 0; i < output.length; i++) {
			System.out.println(output[i]);
		}

	}
}
