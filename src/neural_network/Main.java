package neural_network;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.println("Benvenuto! Seleziona una modalità:");
        System.out.println("1. Cat Detector (con immagini)");
        System.out.println("2. Somma modulo 97 (rete neurale)");
        System.out.print("\nScelta: ");

        int scelta = scanner.nextInt();
        if (scelta == 1) {

            CatDetector.run();
        } else if (scelta == 2) {
            Modulo97Trainer.run();
        } else {
            System.out.println("Scelta non valida.");

        }
    }

}
