import java.util.Scanner;

/**
 * Clase principal que gestiona la aplicación de álbumes de música.
 */
public class AppMusica {

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ServicioMusica servicioMusica = new ServicioMusica();
        int opcion = -1;

        while (true) {
            limpiarConsola();
            servicioMusica.mostrarMenu();

            while (true) {
                try {
                    String input = scanner.nextLine();
                    opcion = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Error: la opción debe ser un número entero. Intente de nuevo.");
                    System.out.print("Seleccione una opción: ");
                }
            }

            switch (opcion) {
                case 1:
                    servicioMusica.agregarAlbum(scanner);
                    break;
                case 2:
                    servicioMusica.agregarCancion(scanner);
                    break;
                case 3:
                    servicioMusica.listarCanciones(scanner);
                    break;
                case 4:
                    servicioMusica.buscarAlbumesPorAnio(scanner);
                    break;
                case 5:
                    servicioMusica.mostrarTodosLosAlbumes();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }

            // Esperar al usuario antes de continuar
            System.out.print("\nPresione Enter para continuar...");
            scanner.nextLine();
        }
    }

    /**
     * Simula la limpieza de la consola imprimiendo varias líneas en blanco.
     */
    private static void limpiarConsola() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
