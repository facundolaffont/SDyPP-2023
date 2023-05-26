package ar.edu.unlu.sdypp.grupo1;

import java.util.ArrayList;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Extremo {

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
     * Realiza una petición HTTP a los nodos maestros. Iterará sobre ellos hasta
     * que uno responda.
     * @param endpoint Endpoint del nodo maestro al cual se realizará la petición.
     * @return Objeto JSON con una y solo una de las siguientes propiedades:
     *         {
     *           "data": Respuesta devuelta en caso de éxito.
     *           "error": Mensaje de error en caso de fallo.
     *         }
     */
    public JSONObject makeRequest(String endpoint) {
        RestTemplate rt = new RestTemplate();
        for (String master : this.mastersIPs) {
            try {
                ResponseEntity<String> response = rt.getForEntity(String.format("http://%s:8080/%s", master, endpoint), String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    return new JSONObject("{\"data\":" + response.getBody() + "}");
                }
                return new JSONObject("{\"error\":" + response.getBody() + "}");
            } catch (RestClientException e) {
                // No hago nada, se hará la petición al siguiente nodo maestro.
            }
        }
        // Ningún nodo maestro respondió.
        return new JSONObject("{\"error\":\"No se obtuvo respuesta de ningún nodo maestro.\"}");
    }

    public static void main(String[] args) {
        SpringApplication.run(Extremo.class, args);
    }

}
