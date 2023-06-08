package ar.edu.unlu.sdypp.grupo1;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Métodos de nivel de servicio relacionados con peticiones de red.
 */
@Service
public class ServicioRed {

    /**
     * Instancia de RestTemplate a utilizar en las peticiones.
     */
    private final RestTemplate client = new RestTemplate();

    /**
     * Realiza una petición GET.
     * @param url URL de destino.
     * @return Respuesta de la petición.
     * @throws RestClientException Si no se pudo disparar la petición o no se
     *                             obtuvo respuesta.
     */
    public ResponseEntity<String> getRequest(String url) throws RestClientException {
        return this.client.getForEntity(url, String.class);
    }

    /**
     * Realiza una petición POST.
     * @param url URL de destino.
     * @param body Objeto JSON a enviar en el body de la petición.
     * @return Respuesta de la petición.
     * @throws RestClientException Si no se pudo disparar la petición o no se
     *                             obtuvo respuesta.
     */
    public ResponseEntity<String> postRequest(String url, JSONObject body) throws RestClientException {
        String json = body == null ? null : body.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
        return this.client.postForEntity(url, httpEntity, String.class);
    }

    /**
     * Realiza una petición POST.
     * @param url URL de destino.
     * @return Respuesta de la petición.
     * @throws RestClientException Si no se pudo disparar la petición o no se
     *                             obtuvo respuesta.
     */
    public ResponseEntity<String> postRequest(String url) throws RestClientException {
        return this.postRequest(url, null);
    }

}
