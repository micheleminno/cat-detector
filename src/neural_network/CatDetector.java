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

	public static final int INPUT_LAYER_SIZE = 100;
	public static final int HIDDEN_LAYER_SIZE = 6;
	public static final int OUTPUT_LAYER_SIZE = 2;

	public static final String CAT_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/Siam_lilacpoint.jpg/294px-Siam_lilacpoint.jpg";
	public static final String NON_CAT_URL = "https://cdn.britannica.com/92/212692-050-D53981F5/labradoodle-dog-stick-running-grass.jpg?w=300";

	public static void main(String[] args) {

		try {
			// Crea la rete neurale
			int[] layers = { INPUT_LAYER_SIZE, HIDDEN_LAYER_SIZE, OUTPUT_LAYER_SIZE };
			NeuralNetwork nn = new NeuralNetwork(layers, 0.01);

			// PROVA A CARICARE I PESI
			boolean loaded = WeightManager.loadWeights(nn);

			List<DataSample> dataset = null;

			if (!loaded) {
				// Se i pesi non esistono, carica il dataset
				System.out.println("🔄 Nessun peso trovato. Carico il dataset...");

				dataset = loadDataset(INPUT_LAYER_SIZE);
				Collections.shuffle(dataset);

				// Split training/test
				int splitIndex = (int) (dataset.size() * 0.8);
				List<DataSample> trainingData = dataset.subList(0, splitIndex);
				List<DataSample> testData = dataset.subList(splitIndex, dataset.size());

				// Addestramento
				trainNetwork(nn, trainingData, 100);

				// Dopo addestramento, salva i pesi
				WeightManager.saveWeights(nn);
				System.out.println("💾 Pesi salvati dopo addestramento.");

				// Valutazione
				evaluateModel(nn, testData);

			} else {
				System.out.println("⚡ Pesi caricati. Addestramento saltato.");

				// ⚡⚡ Se vuoi, puoi comunque valutare su immagini di test
				// oppure saltare del tutto il testData se vuoi velocità massima
			}

			// Dopo: parte generativa
			File generationFolder = new File("generation");
			if (!generationFolder.exists()) {
				generationFolder.mkdir();
				System.out.println("Cartella 'generation' creata.");
			}

			ImageGenerator generator = new ImageGenerator(nn);
			BufferedImage generatedImage = generator.generateCatImage();
			String filename = "generation/generated_cat.png";
			generator.saveImage(generatedImage, filename);

			// Test singole immagini da URL
			testSingleImages(nn);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testSingleImages(NeuralNetwork nn) {
		try {
			double[] result = processTestImage(CAT_URL, nn);
			System.out.println("\nRisultato test (gatto):");
			System.out.println(result[0] > result[1] ? "L'immagine contiene un gatto!" : "Nessun gatto rilevato.");

			result = processTestImage(NON_CAT_URL, nn);
			System.out.println("\nRisultato test (non gatto):");
			System.out.println(result[0] > result[1] ? "L'immagine contiene un gatto!" : "Nessun gatto rilevato.");

		} catch (Exception e) {
			System.err.println("Errore nel test di immagini: " + e.getMessage());
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

	private static List<DataSample> loadDataset(int imageSize) throws Exception {

		ImageProcessor processor = new ImageProcessor();
		List<DataSample> samples = new ArrayList<>();

		loadCategory(samples, imageSize, "cats", processor, new double[] { 1, 0 });
		loadCategory(samples, imageSize, "non_cats", processor, new double[] { 0, 1 });

		return samples;
	}

	private static void loadCategory(List<DataSample> samples, int imageSize, String categoryFolder,
			ImageProcessor processor, double[] label) {

		ImageLoader loader = new ImageLoader();

		List<BufferedImage> images = loader.loadImagesFromFolder(categoryFolder, imageSize);

		for (int i = 0; i < images.size(); i++) {

			double[] pixels = processor.processImage(images.get(i));
			if ((pixels != null) && (pixels.length == INPUT_LAYER_SIZE)) {
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
		return nn.predict(pixels != null ? pixels : new double[INPUT_LAYER_SIZE]);
	}
}