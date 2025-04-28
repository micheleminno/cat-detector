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

	/**
	 * Genera un'immagine "ideale" per la classe "gatto" usando anche i pesi
	 * dell'output
	 */
	public BufferedImage generateCatImage() {
		// Se voglio generare un'immagine che massimizza "gatto", passo outputNeuron =
		// 0, altrimenti 1.
		double[] generatedPixels = generateFromWeightsConsideringOutput(0);
		return pixelsToImage(generatedPixels);
	}

	/**
	 * Usa i pesi input->hidden e hidden->output per generare l'immagine
	 */
	private double[] generateFromWeightsConsideringOutput(int outputNeuron) {
		double[][] W1 = nn.getWeights()[0]; // Pesi hidden -> input (128 x 2500)
		double[][] W2 = nn.getWeights()[1]; // Pesi output -> hidden (2 x 128)

		int inputSize = W1[0].length; // 2500
		int hiddenSize = W1.length; // 128

		double[] summedInput = new double[inputSize];

		for (int i = 0; i < inputSize; i++) { // Per ogni pixel
			double sum = 0.0;
			for (int j = 0; j < hiddenSize; j++) { // Per ogni hidden neuron
				sum += W1[j][i] * W2[outputNeuron][j]; // ⚡ CAMBIATO QUI
			}
			summedInput[i] = sum;
		}

		// Normalizzazione 0-1
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for (double v : summedInput) {
			if (v < min)
				min = v;
			if (v > max)
				max = v;
		}
		for (int i = 0; i < summedInput.length; i++) {
			summedInput[i] = (summedInput[i] - min) / (max - min);
		}

		return summedInput;
	}

	/**
	 * Converte l'array normalizzato in una BufferedImage
	 */
	private BufferedImage pixelsToImage(double[] pixels) {
		BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = image.getRaster();

		for (int y = 0; y < imageSize; y++) {
			for (int x = 0; x < imageSize; x++) {
				int index = y * imageSize + x;
				double pixelValue = pixels[index];
				int value = (int) (pixelValue * 255);
				value = Math.max(0, Math.min(255, value)); // Clamp tra 0 e 255
				raster.setSample(x, y, 0, value);
			}
		}

		return image;
	}

	/**
	 * Salva l'immagine su file
	 */
	public void saveImage(BufferedImage image, String filename) {
		try {
			File output = new File(filename);
			ImageIO.write(image, "png", output);
		} catch (IOException e) {
			System.err.println("Errore nel salvataggio dell'immagine: " + e.getMessage());
		}
	}
}
