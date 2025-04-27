package neural_network.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImageDownloader {

	public void downloadIn(String folderName) {
		try {
			// Leggi il file JSON
			String jsonContent = new String(Files.readAllBytes(Paths.get("dataset.json")));
			JSONObject dataset = new JSONObject(jsonContent);

			// Crea la cartella se non esiste
			File folder = new File(folderName);
			if (!folder.exists()) {
				folder.mkdir();
				System.out.println("Cartella " + folderName + " creata.");
			}

			// Scarica le immagini
			JSONArray nonCats = dataset.getJSONArray(folderName);

			for (int i = 0; i < nonCats.length(); i++) {
				String imageUrl = nonCats.getJSONObject(i).getString("url");
				String fileName = folderName + "/image_" + i + ".jpg"; // Nome del file
				downloadImage(imageUrl, fileName);
				System.out.println("Scaricata: " + fileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Metodo per scaricare un'immagine da un URL e salvarla
	private static void downloadImage(String imageUrl, String fileName) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			// Leggi l'immagine
			InputStream inputStream = connection.getInputStream();
			BufferedImage image = ImageIO.read(inputStream);
			inputStream.close();

			// Salva l'immagine
			if (image != null) {
				File outputFile = new File(fileName);
				ImageIO.write(image, "jpg", outputFile);
			} else {
				System.err.println("Errore: Immagine non valida da " + imageUrl);
			}

		} catch (IOException e) {
			System.err.println("Errore nel download di " + imageUrl + ": " + e.getMessage());
		}
	}
}