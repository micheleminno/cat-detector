package neural_network.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageProcessor {

	public double[] processImage(BufferedImage img) {
		int computedSize = (int) Math.round(Math.sqrt(neural_network.CatDetector.INPUT_LAYER_SIZE));

		// 🔥 Ridimensiona SEMPRE
		BufferedImage resized = resizeImage(img, computedSize, computedSize);

		double[] pixels = new double[computedSize * computedSize];

		for (int y = 0; y < computedSize; y++) {
			for (int x = 0; x < computedSize; x++) {
				int rgb = resized.getRGB(x, y);
				int gray = (rgb >> 16) & 0xFF; // Prendi solo il canale rosso
				pixels[y * computedSize + x] = gray / 255.0;
			}
		}

		return pixels;
	}

	public BufferedImage downloadImage(String url) {
		try {
			return javax.imageio.ImageIO.read(new java.net.URL(url));
		} catch (Exception e) {
			System.err.println("❌ Errore scaricando immagine: " + e.getMessage());
			return null;
		}
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
		BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = resizedImage.createGraphics();
		g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
		g2d.dispose();
		return resizedImage;
	}
}
