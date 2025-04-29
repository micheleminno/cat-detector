package neural_network.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageLoader {

	public List<BufferedImage> loadImagesFromFolder(String folderPath, int imageSize) {
		List<BufferedImage> images = new ArrayList<>();
		File folder = new File(folderPath);

		if (!folder.exists() || !folder.isDirectory()) {
			System.err.println("Errore: La cartella " + folderPath + " non esiste o non è una cartella valida.");
			return images;
		}

		ImageIO.scanForPlugins();
		ImageConverter.convert(folderPath);

		File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg")
				|| name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg"));

		if (files != null) {
			System.out.println("Trovate " + files.length + " immagini da caricare nella cartella " + folderPath);

			for (int i = 0; i < files.length; i++) {
				File file = files[i];

				try {
					BufferedImage img = ImageIO.read(file);
					int width = img.getWidth();
					int height = img.getHeight();
					int computedImageX = (int) Math.round(Math.sqrt(imageSize));

					if ((width > computedImageX) || (height > computedImageX)) {
						System.out.println("Troppo grande, la riduco");

						img = ImageConverter.resizeImage(img, computedImageX, computedImageX, file);

					}

					if (img != null) {
						images.add(img);
						width = img.getWidth();
						height = img.getHeight();

						System.out.println("Caricata immagine n. " + (i + 1) + " (" + width + ", " + height + "): "
								+ file.getName());
					} else {
						System.err.println("Errore: Impossibile leggere l'immagine " + file.getName());
					}

					img = null; // libera subito
				} catch (IOException e) {
					System.err.println("Errore nel caricamento di " + file.getName() + ": " + e.getMessage());
				}
			}

			System.gc(); // Chiedi (gentilmente) a Java di liberare la memoria

		} else {
			System.err.println("Nessuna immagine trovata nella cartella " + folderPath);
		}

		return images;
	}

}
