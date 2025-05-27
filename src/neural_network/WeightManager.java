package neural_network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeightManager {

	private static final String WEIGHTS_FOLDER = "weights";
	
	public static void saveWeights(NeuralNetwork network, String fileName) {
		try {
			File folder = new File(WEIGHTS_FOLDER);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			double[][][] weights = network.getWeights();
			double[][] biases = network.getBiases(); // 👈 devi avere anche questo metodo nella rete

			JSONArray weightsArray = new JSONArray();
			for (double[][] layer : weights) {
				JSONArray layerArray = new JSONArray();
				for (double[] neuron : layer) {
					JSONArray neuronArray = new JSONArray();
					for (double weight : neuron) {
						neuronArray.put(weight);
					}
					layerArray.put(neuronArray);
				}
				weightsArray.put(layerArray);
			}

			JSONArray biasesArray = new JSONArray();
			for (double[] layerBiases : biases) {
				JSONArray layerArray = new JSONArray();
				for (double bias : layerBiases) {
					layerArray.put(bias);
				}
				biasesArray.put(layerArray);
			}

			JSONObject json = new JSONObject();
			json.put("weights", weightsArray);
			json.put("biases", biasesArray);

			try (FileWriter writer = new FileWriter(new File(folder, fileName))) {
				writer.write(json.toString(4));
			}

			System.out.println("💾 Pesi e bias salvati in " + WEIGHTS_FOLDER + "/" + fileName);
		} catch (IOException e) {
			System.err.println("Errore nel salvataggio dei pesi: " + e.getMessage());
		}
	}

	public static boolean loadWeights(NeuralNetwork network, String fileName) {
		
		File file = new File(WEIGHTS_FOLDER, fileName);
		if (!file.exists()) {
			System.out.println("❌ File pesi non trovato. Procedo con addestramento...");
			return false;
		}

		try {
			String content = Files.readString(Paths.get(file.toURI()), StandardCharsets.UTF_8);
			JSONObject json = new JSONObject(content);

			JSONArray weightsArray = json.getJSONArray("weights");
			double[][][] weights = new double[weightsArray.length()][][];

			for (int i = 0; i < weightsArray.length(); i++) {
				JSONArray layerArray = weightsArray.getJSONArray(i);
				weights[i] = new double[layerArray.length()][];

				for (int j = 0; j < layerArray.length(); j++) {
					JSONArray neuronArray = layerArray.getJSONArray(j);
					weights[i][j] = new double[neuronArray.length()];

					for (int k = 0; k < neuronArray.length(); k++) {
						weights[i][j][k] = neuronArray.getDouble(k);
					}
				}
			}

			JSONArray biasesArray = json.getJSONArray("biases");
			double[][] biases = new double[biasesArray.length()][];

			for (int i = 0; i < biasesArray.length(); i++) {
				JSONArray layerBiases = biasesArray.getJSONArray(i);
				biases[i] = new double[layerBiases.length()];
				for (int j = 0; j < layerBiases.length(); j++) {
					biases[i][j] = layerBiases.getDouble(j);
				}
			}

			network.setWeights(weights);
			network.setBiases(biases);

			System.out.println("✅ Pesi e bias caricati da " + WEIGHTS_FOLDER + "/" + fileName);
			return true;
		} catch (IOException e) {
			System.err.println("Errore nel caricamento dei pesi: " + e.getMessage());
			return false;
		}
	}

}
