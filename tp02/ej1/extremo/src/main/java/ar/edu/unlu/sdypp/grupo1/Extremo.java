package ar.edu.unlu.sdypp.grupo1;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

@SpringBootApplication
public class Extremo {

    /**
     * Tiempo (en milisegundos) que debe pasar entre los informes de vida del
     * nodo a los maestros. Este valor debe estar acorde a la configuración de
     * los nodos maestros.
     */
    public static final long INFORM_INTERVAL = 60000;

    /**
     * Ruta a la carpeta compartida.
     */
    private String sharedFolder;

    /**
     * Lista de las direcciones IP de los nodos maestros.
     */
    private ArrayList<String> mastersIPs;

    /**
     * Lista de archivos disponibles para compartir en la red P2P.
     */
    private ArrayList<Archivo> sharedFiles;

    /**
     * Servicio para las peticiones de red.
     */
    @Autowired
    private ServicioRed networkService;

    public void setSharedFolder(String sharedFolder) {
        this.sharedFolder = sharedFolder;
    }

    public void setMastersIPs(ArrayList<String> mastersIPs) {
        this.mastersIPs = mastersIPs;
    }

    public void setSharedFiles(ArrayList<Archivo> sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    /**
     * Informa a los nodos maestros sobre la existencia de este nodo extremo, y
     * anuncia los archivos disponibles para compartir. Iterará sobre los maestros
     * hasta que alguno responda.
     * @return Boolean que indica si se informó exitosamente a los nodos maestros.
     */
    public boolean inform() {
        JSONObject body = new JSONObject();
        body.put("files", this.sharedFiles);
        for (String master : this.mastersIPs) {
            try {
                ResponseEntity<String> response = this.networkService.postRequest(
                    String.format("http://%s/inform", master),
                    body
                );
                return response.getStatusCode().is2xxSuccessful();
            } catch (RestClientException e) {
                // Falló la petición, se intentará con el siguiente nodo maestro.
            }
        }
        // No se obtuvo respuesta de ningún nodo maestro.
        return false;
    }

    /**
     * Realiza una búsqueda de archivos dado un patrón de búsqueda en la red P2P.
     * @param search Patrón de búsqueda.
     * @return Respuesta de los nodos maestros.
     * @throws ExcepcionMaestro Si no se obtiene respuesta de los nodos maestros,
     *                          o la misma no se comprende.
     */
    public String query(String search) throws ExcepcionMaestro {
        for (String master : this.mastersIPs) {
            try {
                ResponseEntity<String> response = this.networkService.getRequest(
                    String.format("http://%s/query?file=%s", master, search)
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                }
                // Código de respuesta distinto a 200.
                throw new ExcepcionMaestro("Falló la petición de búsqueda en la red.");
            } catch (RestClientException e) {
                // Falló la petición, se intentará con el siguiente nodo maestro.
            }
        }
        // No se obtuvo respuesta de ningún nodo maestro.
        throw new ExcepcionMaestro("No se obtuvo respuesta para la búsqueda"
                + " solicitada. Por favor verifique su conexión.");
    }

    /**
     * Retorna una instancia de `File` con un archivo solicitado de la carpeta
     * compartida.
     * @param name Nombre del archivo solicitado.
     * @return Instancia de `File` con el archivo solicitado.
     */
    public File getFile(String name) {
        return new File(this.sharedFolder + File.separator + name);
    }

    /**
     * Informa a los nodos maestros la desconexión de la red. Iterará sobre los
     * maestros hasta que alguno responda.
     */
    public void disconnect() {
        for (String master : this.mastersIPs) {
            try {
                this.networkService.postRequest(String.format("http://%s/exit", master));
                return;
            } catch (RestClientException e) {
                // Falló la petición, se intentará con el siguiente nodo maestro.
            }
        }
    }

    @Bean
    public Finalizador finalizador() {
        return new Finalizador();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java -jar tp2-ej1-extremo-1.0.0-rc.jar <fichero_maestros> <directorio_compartido>");
            System.err.println();
            System.err.println("Donde:");
            System.err.println("  <fichero_maestros>\t\tRuta a un fichero que contenga las direcciones IP de los nodos"
                    + " maestros. Debe tener una dirección por línea.");
            System.err.println("  <directorio_compartido>\tRuta al directorio compartido.");
            System.exit(1);
        }
        SpringApplication.run(Extremo.class, args);
    }

}
