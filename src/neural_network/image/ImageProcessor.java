package neural_network.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageProcessor {

	public BufferedImage downloadImage(String urlString) {

		try {

			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			InputStream inputStream = connection.getInputStream();
			BufferedImage image = ImageIO.read(inputStream);
			inputStream.close();

			return image;

		} catch (IOException e) {

			System.err.println("Error downloading image from " + urlString + ": " + e.getMessage());
			return null;

		}
	}

	public double[] processImage(BufferedImage image) {
		if (image == null) {
			return null;
		}

		BufferedImage processed = this.resize(image, 50, 50);
		processed = this.toGrayscale(processed);
		return this.normalizePixels(processed);
	}

	private BufferedImage resize(BufferedImage img, int width, int height) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	private BufferedImage toGrayscale(BufferedImage img) {
		if (img.getType() == BufferedImage.TYPE_BYTE_GRAY) {
			return img;
		}
		BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g2d = gray.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		return gray;
	}

	private double[] normalizePixels(BufferedImage img) {
		WritableRaster raster = img.getRaster();
		double[] pixels = new double[50 * 50];

		for (int y = 0; y < 50; y++) {
			for (int x = 0; x < 28; x++) {
				int gray = raster.getSample(x, y, 0);
				pixels[(y * 50) + x] = gray / 255.0; // Normalizza tra 0 e 1
			}
		}
		return pixels;
	}
}