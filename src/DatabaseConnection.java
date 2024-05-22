import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para gestionar la conexión a la base de datos SQLite.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:musica.db";

    /**
     * Conecta a la base de datos.
     *
     * @return Conexión a la base de datos.
     * @throws SQLException Si ocurre un error al conectar.
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Crea las tablas necesarias si no existen.
     */
    public static void initializeDatabase() {
        String createAlbumsTable = "CREATE TABLE IF NOT EXISTS albums ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL, "
                + "anio INTEGER NOT NULL, "
                + "disquera TEXT NOT NULL, "
                + "artistas TEXT NOT NULL);";

        String createCancionesTable = "CREATE TABLE IF NOT EXISTS canciones ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "titulo TEXT NOT NULL, "
                + "duracion REAL NOT NULL, "
                + "album_id INTEGER NOT NULL, "
                + "FOREIGN KEY(album_id) REFERENCES albums(id));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createAlbumsTable);
            stmt.execute(createCancionesTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
