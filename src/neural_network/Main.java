package neural_network;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.println("Benvenuto! Seleziona una rete neurale tra quelle disponibili:");
        System.out.println("1. Cat Detector");
        System.out.println("2. Somma modulo 97");
        System.out.print("\nScelta: ");

        int scelta = scanner.nextInt();
        System.out.println(
                "\nOttima scelta. Vuoi addestrare la rete neurale? Calcolerà nuovi pesi e li salverà in un file");
        System.out.println("1. No, mi vanno bene i pesi già salvati");
        System.out.println("2. Sì, voglio ricalcolare i pesi adesso");
        System.out.print("\nScelta: ");
        boolean conAddestramento = scanner.nextInt() == 1 ? false : true;

        if (scelta == 1) {

            CatDetector.run(conAddestramento);
        } else if (scelta == 2) {
            Modulo97Trainer.run(conAddestramento);
        } else {
            System.out.println("Scelta non valida.");

        }
    }

}
