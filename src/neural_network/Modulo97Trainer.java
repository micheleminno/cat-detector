package neural_network;

import java.util.*;
import neural_network.model.*;


public class Modulo97Trainer {

    public static final int INPUT_LAYER_SIZE = 2;
	public static final int HIDDEN_LAYER_SIZE = 1000;
	public static final int OUTPUT_LAYER_SIZE = 1;
    public static final int EPOCHS = 100;

    public static final String WEIGHTS_FILE_NAME = "modulo97-weights.json";


    public static void run() {
        try {
			int[] layers = { INPUT_LAYER_SIZE, HIDDEN_LAYER_SIZE, OUTPUT_LAYER_SIZE };
			NeuralNetwork nn = new NeuralNetwork(layers, 0.01);

			boolean loaded = WeightManager.loadWeights(nn, WEIGHTS_FILE_NAME);
			List<DataSampleModulo97> dataset = null;

			if (!loaded) {
				System.out.println("\uD83D\uDD04 Nessun peso trovato. Carico il dataset...");
				dataset = loadDataset(INPUT_LAYER_SIZE);
				Collections.shuffle(dataset);

				int splitIndex = (int) (dataset.size() * 0.8);
				List<DataSampleModulo97> trainingData = dataset.subList(0, splitIndex);
				List<DataSampleModulo97> testData = dataset.subList(splitIndex, dataset.size());

				trainNetwork(nn, trainingData);
				WeightManager.saveWeights(nn, WEIGHTS_FILE_NAME);
				System.out.println("\uD83D\uDCBE Pesi salvati dopo addestramento.");
				evaluateModel(nn, testData);
			} else {
				System.out.println("\u26A1 Pesi caricati. Addestramento saltato.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private static void evaluateModel(NeuralNetwork nn, List<DataSampleModulo97> testData) {
        int correct = 0;
		for (DataSampleModulo97 sample : testData) {
            double[] arrayInput = {sample.getNumero1(), sample.getNumero2()};
            double[] arrayOutput = {sample.getSomma()};
			double[] output = nn.predict(arrayInput);
			int predicted = (int) output[0];
			int actual = (int) arrayOutput[0];
			if (predicted == actual) {
				correct++;
			}
		}
		System.out.printf("\nAccuracy: %.2f%% (%d/%d)\n", ((correct * 100.0) / testData.size()), correct,
				testData.size());
    }

    private static void trainNetwork(NeuralNetwork nn, List<DataSampleModulo97> trainingData) {
        int epochs = EPOCHS;
        for (int epoch = 0; epoch < epochs; epoch++) {
			for (DataSampleModulo97 sample : trainingData) {
                double[] arrayInput = {sample.getNumero1(), sample.getNumero2()};
                double[] arrayOutput = {sample.getSomma()};
				nn.train(arrayInput, arrayOutput);
			}
			if ((epoch % 10) == 0) {
				System.out.printf("Epoch %d completata\n", epoch);
			}
		}
    }

    private static List<DataSampleModulo97> loadDataset(int inputLayerSize) {
        Random r = new Random();
        List<DataSampleModulo97> samples = new ArrayList<>();
        for(int i =0; i < 100000; i++){
            int numero1 = r.nextInt(100);
            int numero2 = r.nextInt(100);
            int somma = (numero1 + numero2 ) % 97;

            samples.add(new DataSampleModulo97(numero1, numero2, somma));
        }
        
		return samples;
    }

    
}
