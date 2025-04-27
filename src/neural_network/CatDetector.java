package neural_network;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import neural_network.image.ImageGenerator;
import neural_network.image.ImageLoader;
import neural_network.image.ImageProcessor;

public class CatDetector {
	public static void main(String[] args) {

		// ImageDownloader id = new ImageDownloader();

		try {
			// Carica il dataset
			List<DataSample> dataset = loadDataset();
			Collections.shuffle(dataset);

			// Split training/test
			int splitIndex = (int) (dataset.size() * 0.8);
			List<DataSample> trainingData = dataset.subList(0, splitIndex);
			List<DataSample> testData = dataset.subList(splitIndex, dataset.size());

			// Crea la rete neurale
			int[] layers = { 2500, 128, 64, 2 };
			NeuralNetwork nn = new NeuralNetwork(layers, 0.01);

			// 🔥 PROVA A CARICARE I PESI
			boolean loaded = WeightManager.loadWeights(nn);

			if (!loaded) {
				// Se i pesi non esistono, procedi con l'addestramento
				System.out.println("🔄 Nessun peso trovato. Inizio addestramento...");

				trainNetwork(nn, trainingData, 100);

				// Dopo addestramento, salva i pesi
				WeightManager.saveWeights(nn);

				System.out.println("💾 Pesi salvati dopo addestramento.");
			} else {
				System.out.println("⚡ Pesi caricati. Addestramento saltato.");
			}

			// Valutazione
			evaluateModel(nn, testData);

			// Test con due nuove immagini
			// TODO: da migliorare

			// gatto:
			String testUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/Siam_lilacpoint.jpg/294px-Siam_lilacpoint.jpg";

			double[] result = processTestImage(testUrl, nn);
			System.out.println("\nRisultato test (gatto):");
			System.out.println(result[0] > result[1] ? "L'immagine contiene un gatto!" : "Nessun gatto rilevato.");

			// Non gatto:
			testUrl = "https://cdn.britannica.com/92/212692-050-D53981F5/labradoodle-dog-stick-running-grass.jpg?w=300";

			result = processTestImage(testUrl, nn);
			System.out.println("\nRisultato test (non gatto):");
			System.out.println(result[0] > result[1] ? "L'immagine contiene un gatto!" : "Nessun gatto rilevato.");

			// Parte generativa
			// Creazione della cartella "generation" se non esiste
			File generationFolder = new File("generation");
			if (!generationFolder.exists()) {
				generationFolder.mkdir(); // Crea la cartella
				System.out.println("Cartella 'generation' creata.");
			}

			// Genera 5 immagini di gatti
			ImageGenerator generator = new ImageGenerator(nn, 28);
			for (int i = 0; i < 5; i++) {
				BufferedImage generatedImage = generator.generateCatImage(1.0); // Genera un'immagine
				String filename = "generation/generated_cat_" + i + ".png"; // Percorso del file
				generator.saveImage(generatedImage, filename); // Salva l'immagine
				System.out.println("Generata immagine: " + filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class DataSample {
		double[] pixels;
		double[] label;

		DataSample(double[] pixels, double[] label) {
			this.pixels = pixels;
			this.label = label;
		}
	}

	private static List<DataSample> loadDataset() throws Exception {

		ImageProcessor processor = new ImageProcessor();
		List<DataSample> samples = new ArrayList<>();

		loadCategory(samples, "cats", processor, new double[] { 1, 0 });
		loadCategory(samples, "non_cats", processor, new double[] { 0, 1 });

		return samples;
	}

	private static void loadCategory(List<DataSample> samples, String categoryFolder, ImageProcessor processor,
			double[] label) {

		ImageLoader loader = new ImageLoader();

		List<BufferedImage> images = loader.loadImagesFromFolder(categoryFolder);

		for (int i = 0; i < images.size(); i++) {

			double[] pixels = processor.processImage(images.get(i));
			if ((pixels != null) && (pixels.length == 2500)) {
				samples.add(new DataSample(pixels, label));
			}
		}
	}

	private static void trainNetwork(NeuralNetwork nn, List<DataSample> trainingData, int epochs) {
		for (int epoch = 0; epoch < epochs; epoch++) {
			for (DataSample sample : trainingData) {
				nn.train(sample.pixels, sample.label);
			}
			if ((epoch % 10) == 0) {
				System.out.printf("Epoch %d completata\n", epoch);
			}
		}
	}

	private static void evaluateModel(NeuralNetwork nn, List<DataSample> testData) {
		int correct = 0;
		for (DataSample sample : testData) {
			double[] output = nn.predict(sample.pixels);
			int predicted = output[0] > output[1] ? 0 : 1;
			int actual = sample.label[0] > sample.label[1] ? 0 : 1;
			if (predicted == actual) {
				correct++;
			}
		}
		System.out.printf("\nAccuracy: %.2f%% (%d/%d)\n", ((correct * 100.0) / testData.size()), correct,
				testData.size());
	}

	private static double[] processTestImage(String url, NeuralNetwork nn) {
		ImageProcessor processor = new ImageProcessor();
		BufferedImage img = processor.downloadImage(url);
		double[] pixels = processor.processImage(img);
		return nn.predict(pixels != null ? pixels : new double[2500]);
	}
}