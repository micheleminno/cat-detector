package neural_network;

import java.util.Random;

public class NeuralNetwork {
	private int[] layers; // Numero di neuroni in ogni layer
	private double[][][] weights; // Pesi tra i layer (input → output)
	private double[][] biases; // Bias per ogni layer nascosto e output
	private double learningRate;
	private Random random = new Random();

	public NeuralNetwork(int[] layers, double learningRate) {
		this.layers = layers;
		this.learningRate = learningRate;
		this.initializeNetwork();
	}

	private void initializeNetwork() {
		this.weights = new double[this.layers.length - 1][][];
		this.biases = new double[this.layers.length - 1][];

		for (int i = 0; i < (this.layers.length - 1); i++) {
			this.weights[i] = new double[this.layers[i]][this.layers[i + 1]];
			this.biases[i] = new double[this.layers[i + 1]];

			for (int j = 0; j < this.layers[i]; j++) {
				for (int k = 0; k < this.layers[i + 1]; k++) {
					this.weights[i][j][k] = this.random.nextGaussian(); // Pesi casuali
				}
			}

			for (int j = 0; j < this.layers[i + 1]; j++) {
				this.biases[i][j] = 0.0; // Bias inizializzati a zero
			}
		}
	}

	public double[] predict(double[] input) {
		double[] activation = input;
		for (int i = 0; i < this.layers.length - 1; i++) {
			activation = this.feedForward(activation, this.weights[i], this.biases[i]);
		}
		return activation;
	}

	private double[] feedForward(double[] input, double[][] layerWeights, double[] layerBiases) {
		double[] output = new double[layerBiases.length]; // num neuroni nel layer di output
		for (int j = 0; j < output.length; j++) {
			double sum = layerBiases[j];
			for (int k = 0; k < input.length; k++) {
				sum += layerWeights[k][j] * input[k]; // peso da input k a output j
			}
			// usa sigmoid solo sull'ultimo layer
			if (layerBiases.length == 1) {
				output[j] = this.sigmoid(sum); // layer di output
			} else {
				output[j] = this.relu(sum);    // layer nascosti
			}
		}
		return output;
	}

	public void train(double[] input, double[] target) {
		double[][] activations = new double[this.layers.length][];
		activations[0] = input;

		// Passaggio forward
		for (int i = 0; i < this.layers.length - 1; i++) {
			activations[i + 1] = this.feedForward(activations[i], this.weights[i], this.biases[i]);
		}

		// Calcolo dell'errore sull'output
		double[][] errors = new double[this.layers.length - 1][];
		int outputLayer = this.layers.length - 1;


		int lastErrorIndex = errors.length - 1;
		int outputLayerIndex = layers.length - 1;
		errors[lastErrorIndex] = new double[layers[outputLayerIndex]];

		for (int j = 0; j < layers[outputLayerIndex]; j++) {
			errors[lastErrorIndex][j] = activations[outputLayerIndex][j] - target[j];
		}


		// Backpropagation
		for (int i = errors.length - 2; i >= 0; i--) {
			errors[i] = new double[this.layers[i + 1]];
			for (int j = 0; j < this.layers[i + 1]; j++) {
				double error = 0.0;
				for (int k = 0; k < this.layers[i + 2]; k++) {
					error += errors[i + 1][k] * this.weights[i + 1][j][k];
				}
				if (i == errors.length - 1) {
					// Derivata sigmoid per l'output
					errors[i][j] = error * this.sigmoidDerivative(activations[i + 1][j]);
				} else {
					// Derivata ReLU per i layer nascosti
					errors[i][j] = error * this.reluDerivative(activations[i + 1][j]);
				}

			}
		}

		// Aggiornamento pesi e bias
		for (int i = 0; i < this.weights.length; i++) {
			for (int j = 0; j < this.weights[i].length; j++) {
				for (int k = 0; k < this.weights[i][j].length; k++) {
					this.weights[i][j][k] -= this.learningRate * errors[i][k] * activations[i][j];
				}
			}
			for (int j = 0; j < this.biases[i].length; j++) {
				this.biases[i][j] -= this.learningRate * errors[i][j];
			}
		}
	}

	private double relu(double x) {
		return Math.max(0, x);
	}

	private double reluDerivative(double x) {
		return x > 0 ? 1 : 0;
	}

	private double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	private double sigmoidDerivative(double sigmoidOutput) {
		return sigmoidOutput * (1 - sigmoidOutput);
	}


	// Getter/Setter
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
