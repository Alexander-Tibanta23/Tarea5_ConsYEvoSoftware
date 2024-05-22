import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Servicio para la gestión de álbumes de música.
 */
public class ServicioMusica {
    private List<Album> albumes;

    /**
     * Constructor para la clase ServicioMusica.
     */
    public ServicioMusica() {
        this.albumes = new ArrayList<>();
        DatabaseConnection.initializeDatabase();
        cargarAlbumesDesdeDB();
    }

    /**
     * Carga los álbumes desde la base de datos.
     */
    private void cargarAlbumesDesdeDB() {
        String sql = "SELECT * FROM albums";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int anio = rs.getInt("anio");
                String disquera = rs.getString("disquera");
                String artistas = rs.getString("artistas");

                Album album = new Album(id, nombre, anio, disquera, List.of(artistas.split(", ")));
                cargarCancionesDesdeDB(album);
                albumes.add(album);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Carga las canciones desde la base de datos.
     *
     * @param album El álbum al que pertenecen las canciones.
     */
    private void cargarCancionesDesdeDB(Album album) {
        String sql = "SELECT * FROM canciones WHERE album_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, album.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String titulo = rs.getString("titulo");
                    double duracion = rs.getDouble("duracion");
                    Cancion cancion = new Cancion(titulo, duracion);
                    album.agregarCancion(cancion);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Muestra el menú principal de la aplicación.
     */
    public void mostrarMenu() {
        System.out.println("\n====================");
        System.out.println(" Menú Principal ");
        System.out.println("====================");
        System.out.println("1. Agregar nuevo álbum");
        System.out.println("2. Agregar canción a un álbum existente");
        System.out.println("3. Ver lista de canciones de un álbum");
        System.out.println("4. Buscar álbumes por año");
        System.out.println("5. Mostrar todos los álbumes con sus canciones");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Agrega un nuevo álbum a la colección.
     *
     * @param scanner El objeto Scanner para leer la entrada del usuario.
     */
    public void agregarAlbum(Scanner scanner) {
        System.out.println("\n====================");
        System.out.println(" Agregar Nuevo Álbum ");
        System.out.println("====================");
        System.out.print("Nombre del álbum: ");
        String nombre = scanner.nextLine();

        int anio;
        while (true) {
            System.out.print("Año de lanzamiento: ");
            String anioInput = scanner.nextLine();
            try {
                anio = Integer.parseInt(anioInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: el año debe ser un número entero.");
            }
        }

        System.out.print("Disquera: ");
        String disquera = scanner.nextLine();
        System.out.print("Artistas (separados por comas): ");
        String artistasInput = scanner.nextLine();
        List<String> artistas = List.of(artistasInput.split(", "));

        String sql = "INSERT INTO albums(nombre, anio, disquera, artistas) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setInt(2, anio);
            pstmt.setString(3, disquera);
            pstmt.setString(4, artistasInput);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        cargarAlbumesDesdeDB();
        System.out.println("Álbum agregado exitosamente.");
    }

    /**
     * Agrega una canción a un álbum existente.
     *
     * @param scanner El objeto Scanner para leer la entrada del usuario.
     */
    public void agregarCancion(Scanner scanner) {
        System.out.println("\n====================");
        System.out.println(" Agregar Canción ");
        System.out.println("====================");
        System.out.print("Nombre del álbum: ");
        String nombreAlbum = scanner.nextLine();
        Album album = encontrarAlbumPorNombre(nombreAlbum);
        if (album == null) {
            System.out.println("Álbum no encontrado.");
            return;
        }

        System.out.print("Título de la canción: ");
        String titulo = scanner.nextLine();

        double duracion;
        while (true) {
            System.out.print("Duración de la canción (minutos): ");
            String duracionInput = scanner.nextLine().replace(',', '.');
            try {
                duracion = Double.parseDouble(duracionInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: la duración debe ser un número decimal. Use coma para separar los decimales.");
            }
        }

        Cancion cancion = new Cancion(titulo, duracion);
        if (album.agregarCancion(cancion)) {
            String sql = "INSERT INTO canciones(titulo, duracion, album_id) VALUES(?, ?, ?)";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, titulo);
                pstmt.setDouble(2, duracion);
                pstmt.setInt(3, album.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("Canción agregada exitosamente.");
        } else {
            System.out.println("Error: ya existe una canción con ese título en el álbum.");
        }
    }

    /** Muestra la lista de canciones de un álbum específico.
     *
     * @param scanner El objeto Scanner para leer la entrada del usuario.
     */
    public void listarCanciones(Scanner scanner) {
        System.out.println("\n====================");
        System.out.println(" Lista de Canciones ");
        System.out.println("====================");
        System.out.print("Nombre del álbum: ");
        String nombreAlbum = scanner.nextLine();
        Album album = encontrarAlbumPorNombre(nombreAlbum);
        if (album == null) {
            System.out.println("Álbum no encontrado.");
            return;
        }

        System.out.println("Canciones en el álbum '" + album.getNombre() + "':");
        for (Cancion cancion : album.getCanciones()) {
            System.out.println("  - " + cancion);
        }
    }

    /**
     * Busca y muestra álbumes por año de lanzamiento.
     *
     * @param scanner El objeto Scanner para leer la entrada del usuario.
     */
    public void buscarAlbumesPorAnio(Scanner scanner) {
        System.out.println("\n====================");
        System.out.println(" Buscar Álbumes por Año ");
        System.out.println("====================");
        int anio;
        while (true) {
            System.out.print("Año de lanzamiento: ");
            String anioInput = scanner.nextLine();
            try {
                anio = Integer.parseInt(anioInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error: el año debe ser un número entero.");
            }
        }

        String sql = "SELECT * FROM albums WHERE anio = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, anio);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Álbumes lanzados en " + anio + ":");
                while (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String disquera = rs.getString("disquera");
                    String artistas = rs.getString("artistas");
                    System.out.println("Nombre: " + nombre + ", Disquera: " + disquera + ", Artistas: " + artistas);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Muestra todos los álbumes con sus canciones.
     */
    public void mostrarTodosLosAlbumes() {
        System.out.println("\n====================");
        System.out.println(" Todos los Álbumes ");
        System.out.println("====================");
        if (albumes.isEmpty()) {
            System.out.println("No hay álbumes en la colección.");
        } else {
            for (Album album : albumes) {
                System.out.println(album);
                for (Cancion cancion : album.getCanciones()) {
                    System.out.println("  - " + cancion);
                }
            }
        }
    }

    /**
     * Encuentra un álbum por su nombre.
     *
     * @param nombre El nombre del álbum.
     * @return El álbum si se encuentra, null si no.
     */
    private Album encontrarAlbumPorNombre(String nombre) {
        for (Album album : albumes) {
            if (album.getNombre().equalsIgnoreCase(nombre)) {
                return album;
            }
        }
        return null;
    }
}
