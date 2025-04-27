package neural_network;

import java.util.Random;

public class NeuralNetwork {
	private int[] layers; // Numero di neuroni in ogni layer
	private double[][][] weights; // Pesi tra i layer
	private double[][] biases; // Bias per ogni layer
	private double learningRate;
	private Random random = new Random();

	public NeuralNetwork(int[] layers, double learningRate) {
		this.layers = layers;
		this.learningRate = learningRate;
		this.initializeNetwork();
	}

	// Inizializza pesi e bias
	private void initializeNetwork() {
		this.weights = new double[this.layers.length - 1][][];
		this.biases = new double[this.layers.length - 1][];

		for (int i = 0; i < (this.layers.length - 1); i++) {
			this.weights[i] = new double[this.layers[i + 1]][this.layers[i]];
			this.biases[i] = new double[this.layers[i + 1]];

			for (int j = 0; j < this.layers[i + 1]; j++) {
				for (int k = 0; k < this.layers[i]; k++) {
					this.weights[i][j][k] = this.random.nextGaussian() * 0.01; // Pesi piccoli e casuali
				}
				this.biases[i][j] = 0.0; // Bias inizializzati a 0
			}
		}
	}

	// Forward pass
	public double[] predict(double[] input) {
		double[] activation = input;
		for (int i = 0; i < (this.layers.length - 1); i++) {
			activation = this.feedForward(activation, this.weights[i], this.biases[i]);
		}
		return activation;
	}

	private double[] feedForward(double[] input, double[][] layerWeights, double[] layerBiases) {
		double[] output = new double[layerWeights.length];
		for (int j = 0; j < layerWeights.length; j++) {
			double sum = layerBiases[j];
			for (int k = 0; k < input.length; k++) {
				sum += layerWeights[j][k] * input[k];
			}
			output[j] = this.relu(sum); // Funzione di attivazione ReLU
		}
		return output;
	}

	// Backpropagation e training
	public void train(double[] input, double[] target) {
		// Forward pass
		double[][] activations = new double[this.layers.length][];
		activations[0] = input;

		for (int i = 0; i < (this.layers.length - 1); i++) {
			activations[i + 1] = this.feedForward(activations[i], this.weights[i], this.biases[i]);
		}

		// Backward pass
		double[][] errors = new double[this.layers.length - 1][];
		errors[errors.length - 1] = new double[this.layers[this.layers.length - 1]];

		// Calcolo dell'errore sull'output layer
		for (int j = 0; j < this.layers[this.layers.length - 1]; j++) {
			errors[errors.length - 1][j] = activations[this.layers.length - 1][j] - target[j];
		}

		// Propagazione dell'errore indietro
		for (int i = errors.length - 2; i >= 0; i--) {
			errors[i] = new double[this.layers[i + 1]];
			for (int j = 0; j < this.layers[i + 1]; j++) {
				double error = 0.0;
				for (int k = 0; k < this.layers[i + 2]; k++) {
					error += errors[i + 1][k] * this.weights[i + 1][k][j];
				}
				errors[i][j] = error * this.reluDerivative(activations[i + 1][j]);
			}
		}

		// Aggiornamento dei pesi e dei bias
		for (int i = 0; i < this.weights.length; i++) {
			for (int j = 0; j < this.weights[i].length; j++) {
				for (int k = 0; k < this.weights[i][j].length; k++) {
					this.weights[i][j][k] -= this.learningRate * errors[i][j] * activations[i][k];
				}
				this.biases[i][j] -= this.learningRate * errors[i][j];
			}
		}
	}

	// Funzione di attivazione ReLU
	private double relu(double x) {
		return Math.max(0, x);
	}

	// Derivata della ReLU
	private double reluDerivative(double x) {
		return x > 0 ? 1 : 0;
	}

	public double[][][] getWeights() {
		return this.weights;
	}

	public void setWeights(double[][][] newWeights) {
		this.weights = newWeights;
	}

	public double[][] getBiases() {
		return this.biases;
	}

	public void setBiases(double[][] newBiases) {
		this.biases = newBiases;
	}
}