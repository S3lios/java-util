package neuralnetwork;
public abstract class Layer {

	public abstract void forward();
	public abstract void backward();
	public abstract void update();
	public abstract void setInput(double[] input);
	public abstract double[] getOutput();
	public abstract void setInputGradients(double[] inputGradients);
	public abstract double[] getOutputGradients();

	public abstract int getInputDimension();
	public abstract int getOutputDimension();
	public abstract int getParameterCount();


	public abstract void resume();

}
