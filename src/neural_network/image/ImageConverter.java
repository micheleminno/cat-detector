package neural_network.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageConverter {

	public static void convert(String folderPath) {
		// 📁 Cartella con le immagini .webp
		File folder = new File(folderPath);

		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("Cartella non trovata o non valida.");
			return;
		}

		File[] webpFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".webp"));

		if ((webpFiles == null) || (webpFiles.length == 0)) {
			System.out.println("Nessuna immagine .webp trovata nella cartella.");
			return;
		}

		Arrays.sort(webpFiles); // opzionale: per avere ordine alfabetico
		System.out.println("Trovati " + webpFiles.length + " file .webp da convertire.");

		for (File webpFile : webpFiles) {
			String pngFilename = webpFile.getName().replaceAll("(?i)\\.webp$", ".png");
			File pngFile = new File(folder, pngFilename);

			ProcessBuilder pb = new ProcessBuilder("/opt/homebrew/bin/dwebp", webpFile.getAbsolutePath(), "-o",
					pngFile.getAbsolutePath());

			try {
				Process process = pb.start();
				int exitCode = process.waitFor();
				if (exitCode == 0) {
					System.out.println("✔️ Convertito: " + webpFile.getName() + " → " + pngFilename);
					boolean deleted = webpFile.delete();
					if (deleted) {
						System.out.println("🗑️ Rimosso file originale: " + webpFile.getName());
					}
				} else {
					System.err.println("❌ Errore nella conversione di: " + webpFile.getName());
				}
			} catch (IOException | InterruptedException e) {
				System.err.println("⚠️ Errore durante la conversione di " + webpFile.getName() + ": " + e.getMessage());
			}
		}

		System.out.println("✅ Conversione completata.");
	}

	public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight,
			File originalFile) {

		String format = getFormatName(originalFile.getName());
		int imageType = format.equals("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, imageType);
		Graphics2D g2d = resizedImage.createGraphics();

		// Migliore qualità di scaling
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		g2d.dispose();

		// 🔥 Dopo aver ridimensionato, sovrascrive il file originale
		/*
		 * try { ImageIO.write(resizedImage, format, originalFile);
		 * System.out.println("✔️ Sovrascritta immagine: " + originalFile.getName()); }
		 * catch (IOException e) { System.err
		 * .println("❌ Errore nel sovrascrivere l'immagine " + originalFile.getName() +
		 * ": " + e.getMessage()); }
		 */

		return resizedImage;
	}

	private static String getFormatName(String filename) {
		String lower = filename.toLowerCase();
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
			return "jpg";
		}
		if (lower.endsWith(".png")) {
			return "png";
		}
		return "png"; // default
	}
}
