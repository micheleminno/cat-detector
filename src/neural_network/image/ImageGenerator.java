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
		// Crea un vettore di input casuale
		double[] randomInput = new double[2]; // 2 valori casuali (uno per ogni neurone di output)
		for (int i = 0; i < randomInput.length; i++) {
			randomInput[i] = Math.random() * randomness;
		}

		// Inverte il processo della rete neurale
		double[] generatedPixels = this.invertNetwork(randomInput);

		// Converti i pixel in un'immagine
		return this.pixelsToImage(generatedPixels);
	}

	private double[] invertNetwork(double[] input) {
		// Prendiamo i pesi e i bias dell'unico layer
		double[][] weights = this.nn.getWeights()[0]; // Dimensioni: [2][2500]

		// TODO: perché biases non è usato qui?
		// double[] biases = this.nn.getBiases()[0]; // Dimensioni: [2]

		// Creiamo l'array per i nuovi valori
		double[] newCurrent = new double[2500]; // Vogliamo generare un'immagine 50x50 (2500 pixel)

		// Applichiamo la trasformazione inversa
		for (int i = 0; i < 2500; i++) {
			double sum = 0.0;
			for (int j = 0; j < 2; j++) { // Iteriamo sui 2 neuroni di output
				sum += weights[j][i] * input[j]; // Usiamo i pesi in modo inverso
			}
			newCurrent[i] = this.relu(sum); // Usiamo la ReLU per limitare i valori tra 0 e 1
		}

		return newCurrent;
	}

	private BufferedImage pixelsToImage(double[] pixels) {
		BufferedImage image = new BufferedImage(this.imageSize, this.imageSize, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = image.getRaster();

		for (int y = 0; y < this.imageSize; y++) {
			for (int x = 0; x < this.imageSize; x++) {
				double pixelValue = pixels[(y * this.imageSize) + x];
				int value = (int) (pixelValue * 255); // Scala il valore tra 0 e 255
				value = Math.max(0, Math.min(255, value)); // Clamp tra 0 e 255
				raster.setSample(x, y, 0, value);
			}
		}

		return image;
	}

	private double relu(double x) {
		return Math.max(0, x);
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