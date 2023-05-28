package ar.edu.unlu.sdypp.grupo1;

import java.util.ArrayList;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
     * Lista de las direcciones IP de los nodos maestros.
     */
    private ArrayList<String> mastersIPs;

    /**
     * Lista de archivos disponibles para compartir en la red P2P.
     */
    private ArrayList<Archivo> sharedFiles;

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
     * @param service Servicio de red para realizar la petición HTTP.
     * @return Boolean que indica si se informó exitosamente a los nodos maestros.
     */
    public boolean inform(ServicioRed service) {
        JSONObject body = new JSONObject();
        body.put("files", this.sharedFiles);
        for (String master : this.mastersIPs) {
            try {
                ResponseEntity<String> response = service.postRequest(String.format("http://%s:8080/inform", master), body);
                return response.getStatusCode().is2xxSuccessful();
            } catch (RestClientException e) {
                // Falló la petición, se intentará con el siguiente nodo maestro.
            }
        }
        // No se obtuvo respuesta de ningún nodo maestro.
        return false;
    }

    /**
     * Informa a los nodos maestros la desconexión de la red. Iterará sobre los
     * maestros hasta que alguno responda.
     * @param service Servicio de red para realizar la petición HTTP.
     */
    public void disconnect(ServicioRed service) {
        for (String master : this.mastersIPs) {
            try {
                service.postRequest(String.format("http://%s:8080/exit", master));
                return;
            } catch (RestClientException e) {
                // Falló la petición, se intentará con el siguiente nodo maestro.
            }
        }
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