package neural_network.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import neural_network.NeuralNetwork;

public class ImageGenerator {
	private NeuralNetwork nn;

	public ImageGenerator(NeuralNetwork nn) {
		this.nn = nn;
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
		double[][] W1 = nn.getWeights()[0]; // Es.: Pesi hidden -> input
		double[][] W2 = nn.getWeights()[1]; // Es.: Pesi output -> hidden

		int inputSize = W1[0].length; // 2500
		int hiddenSize = W1.length; // 128

		double[] summedInput = new double[inputSize];

		for (int i = 0; i < inputSize; i++) { // Per ogni pixel
			double sum = 0.0;
			for (int j = 0; j < hiddenSize; j++) { // Per ogni hidden neuron
				sum += W1[j][i] * W2[outputNeuron][j];
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
		int totalPixels = pixels.length;
		int computedSize = (int) Math.round(Math.sqrt(totalPixels));

		if (computedSize * computedSize != totalPixels) {
			throw new IllegalArgumentException(
					"❌ Errore: il vettore di pixel (" + totalPixels + ") non corrisponde a un quadrato perfetto!");
		}

		BufferedImage image = new BufferedImage(computedSize, computedSize, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = image.getRaster();

		for (int y = 0; y < computedSize; y++) {
			for (int x = 0; x < computedSize; x++) {
				int index = y * computedSize + x;
				double pixelValue = pixels[index];
				int value = (int) (pixelValue * 255);
				value = Math.max(0, Math.min(255, value)); // Clamp tra 0 e 255
				raster.setSample(x, y, 0, value);
			}
		}

		System.out.println("✅ Immagine generata di dimensioni: " + computedSize + "x" + computedSize);

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
