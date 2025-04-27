package neural_network.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import neural_network.NeuralNetwork;

public class ImageGenerator {
	private NeuralNetwork nn;
	private int imageSize;

	public ImageGenerator(NeuralNetwork nn, int imageSize) {
		this.nn = nn;
		this.imageSize = imageSize;
	}

	public BufferedImage generateCatImage(double randomness) {
		// Crea un vettore di input casuale con un po' di rumore controllato
		double[] randomInput = new double[2];
		for (int i = 0; i < randomInput.length; i++) {
			randomInput[i] = ((Math.random() * 2) - 1) * randomness; // random tra -randomness e +randomness
		}

		double[] generatedPixels = this.invertNetwork(randomInput);
		return this.pixelsToImage(generatedPixels);
	}

	private double[] invertNetwork(double[] input) {
		double[][] weights = this.nn.getWeights()[0]; // [2][2500]
		double[] biases = this.nn.getBiases()[0]; // [2]

		double[] newCurrent = new double[2500];

		// Applichiamo la trasformazione inversa
		for (int i = 0; i < 2500; i++) {
			double sum = 0.0;
			for (int j = 0; j < 2; j++) {
				sum += (weights[j][i] * input[j]) + biases[j]; // ⚡ adesso usiamo anche i bias!
			}
			newCurrent[i] = this.relu(sum); // oppure potresti provare sigmoid(sum) se vuoi valori più "soft"
		}

		return newCurrent;
	}

	private BufferedImage pixelsToImage(double[] pixels) {
		BufferedImage image = new BufferedImage(this.imageSize, this.imageSize, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = image.getRaster();

		for (int y = 0; y < this.imageSize; y++) {
			for (int x = 0; x < this.imageSize; x++) {
				double pixelValue = pixels[(y * this.imageSize) + x];
				int value = (int) (pixelValue * 255);
				value = Math.max(0, Math.min(255, value)); // Clamp tra 0 e 255
				raster.setSample(x, y, 0, value);
			}
		}

		return image;
	}

	private double relu(double x) {
		return Math.max(0, x);
	}

	private double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}

	public void saveImage(BufferedImage image, String filename) {
		try {
			File output = new File(filename);
			ImageIO.write(image, "png", output);
		} catch (IOException e) {
			System.err.println("Errore nel salvataggio dell'immagine: " + e.getMessage());
		}
	}
}
