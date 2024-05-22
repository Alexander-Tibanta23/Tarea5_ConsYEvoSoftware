import java.util.ArrayList;
import java.util.List;

/**
 * Representa un álbum musical.
 */
public class Album {
    private int id;
    private String nombre;
    private int anioLanzamiento;
    private String disquera;
    private List<String> artistas;
    private List<Cancion> canciones;

    /**
     * Constructor para la clase Album.
     */
    public Album(String nombre, int anioLanzamiento, String disquera, List<String> artistas) {
        this(-1, nombre, anioLanzamiento, disquera, artistas);
    }

    /**
     * Constructor para la clase Album con ID.
     */
    public Album(int id, String nombre, int anioLanzamiento, String disquera, List<String> artistas) {
        this.id = id;
        this.nombre = nombre;
        this.anioLanzamiento = anioLanzamiento;
        this.disquera = disquera;
        this.artistas = artistas;
        this.canciones = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public String getDisquera() {
        return disquera;
    }

    public List<String> getArtistas() {
        return artistas;
    }

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public boolean agregarCancion(Cancion cancion) {
        for (Cancion c : canciones) {
            if (c.getTitulo().equalsIgnoreCase(cancion.getTitulo())) {
                return false;
            }
        }
        canciones.add(cancion);
        return true;
    }

    @Override
    public String toString() {
        return "Álbum: " + nombre + ", Año: " + anioLanzamiento + ", Disquera: " + disquera + ", Artistas: " + String.join(", ", artistas);
    }
}
