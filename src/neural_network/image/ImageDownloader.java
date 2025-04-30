package neural_network.image;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImageDownloader {

	private static final String API_KEY = System.getenv("PEXELS_API_KEY");
	private static final String OUTPUT_FOLDER = "non_cats";
	private static final String API_URL = "https://api.pexels.com/v1/search";

	private static final String[] QUERIES = { "dog", "rabbit", "tiger", "lion", "puma",
			"wolf" };

	public static void main(String[] args) throws Exception {
		if (API_KEY == null || API_KEY.isEmpty()) {
			throw new IllegalStateException("❌ Variabile d'ambiente PEXELS_API_KEY non trovata.");
		}

		downloadMoreImages(50);
	}

	public static void downloadMoreImages(int howMany) throws Exception {
		Files.createDirectories(Paths.get(OUTPUT_FOLDER));
		int totalDownloaded = 0;
		int queryIndex = 0;

		while (totalDownloaded < howMany) {
			String query = QUERIES[queryIndex % QUERIES.length];
			queryIndex++;

			System.out.println("🔍 Query: " + query);
			List<String> urls = searchPexelsImageUrls(query, 80); // chiediamo più immagini possibili

			for (String url : urls) {
				if (totalDownloaded >= howMany)
					break;
				try {
					String filename = query.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".jpg";
					downloadImage(url, OUTPUT_FOLDER, filename);
					totalDownloaded++;
				} catch (IOException e) {
					System.err.println("⚠️  Errore nel download: " + e.getMessage());
				}

				// piccola pausa per evitare blocchi lato server
				Thread.sleep(300);
			}

			Thread.sleep(500);
		}

		cleanUnreadableImages(OUTPUT_FOLDER);
		System.out.println("📦 Download terminato: " + totalDownloaded + " immagini scaricate.");
	}

	private static List<String> searchPexelsImageUrls(String query, int limit) throws IOException {
		List<String> urls = new ArrayList<>();
		String fullUrl = API_URL + "?query=" + URLEncoder.encode(query, "UTF-8") + "&per_page=" + limit;

		HttpURLConnection conn = (HttpURLConnection) new URL(fullUrl).openConnection();
		conn.setRequestProperty("Authorization", API_KEY);
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new IOException("Errore API Pexels: codice " + conn.getResponseCode());
		}

		try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
			}

			JSONObject json = new JSONObject(response.toString());
			JSONArray photos = json.getJSONArray("photos");

			for (int i = 0; i < photos.length(); i++) {
				JSONObject photo = photos.getJSONObject(i);
				String imageUrl = photo.getJSONObject("src").getString("medium");
				urls.add(imageUrl);
			}
		}

		return urls;
	}

	private static void downloadImage(String imageUrl, String folder, String filename) throws IOException {
		URL url = new URL(imageUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn.setRequestProperty("Referer", "https://www.pexels.com");

		if (conn.getResponseCode() != 200) {
			throw new IOException(
					"Server returned HTTP response code: " + conn.getResponseCode() + " for URL: " + imageUrl);
		}

		try (InputStream in = conn.getInputStream()) {
			Files.copy(in, Paths.get(folder, filename), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("✔ Scaricata: " + filename);
		}
	}

	public static void cleanUnreadableImages(String folderPath) {
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			System.err.println("❌ Cartella non trovata: " + folderPath);
			return;
		}

		File[] files = folder.listFiles((dir, name) -> {
			String lower = name.toLowerCase();
			return lower.endsWith(".jpg") || lower.endsWith(".png") || lower.endsWith(".jpeg");
		});

		int deleted = 0;
		for (File file : files) {
			try {
				BufferedImage img = ImageIO.read(file);
				if (img == null && file.delete()) {
					System.out.println("🗑️ Eliminato file non leggibile: " + file.getName());
					deleted++;
				}
			} catch (Exception e) {
				if (file.delete()) {
					System.out.println("🗑️ Eliminato file corrotto: " + file.getName());
					deleted++;
				}
			}
		}

		System.out.println("🧽 Pulizia completata: " + deleted + " file rimossi.");
	}
}
