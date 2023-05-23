package ar.edu.unlu.sdypp.grupo1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ar.edu.unlu.sdypp.grupo1.requests.InformRequest;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
public class Maestro {

    public static void main(String[] args)
    { SpringApplication.run(Maestro.class, args); }

    public Maestro() {
        // Inicializa las listas de maestros y extremos
        // y carga la lista de maestros con los otros maestros.
        peersList = new ArrayList<HostSession>();
        mastersList = new ArrayList<HostSession>();
        // TODO: añadir los otros maestros.
    }

    // Utilizado por los extremos para anunciarse a la red,
    // y que su IP y lista de archivos sean replicados a
    // los demás extremos.
    @PostMapping(value="/inform")
    public String inform(@RequestBody InformRequest informRequest) {
        logger.debug(String.format(
            "Se anuncia el host <%s:%s>.",
            httpServletRequest.getRemoteHost(),
            httpServletRequest.getRemotePort()
        ));

        // Registra la IP del host y la descripción de
        // sus archivos, junto con el timestamp de la conexión.
        // TODO: valida si ya existe el host.
        peersList.add(
            new HostSession(
                httpServletRequest.getRemoteHost(),
                new Date()
            )
        );

        // Notifica a los maestros la información.
        JSONObject jsonNotificacion = (new JSONObject())
            .put(
                    "nuevos-extremos",
                    (new JSONArray())
                        .put(httpServletRequest.getRemoteHost())
                );
        ArrayList<JSONObject> listaRespuestas = enviarMensajeAMaestros("actualizar", jsonNotificacion);

        // TODO: construir la respuesta única con los mensajes de la lista de respuestas.

        return (new JSONObject())
            .put("Código de respuesta", 200)
            .toString();
    }

    // Endpoint utilizado por los otros maestros para enviar
    // información de actualización.
    @PostMapping(
        value="/actualizar",
        headers="Content-Type=application/json"
    )
    public String actualizar(@RequestBody String json) {
        return null;
    }

    // Endpoint utilizado por los extremos para pedir que se busquen ciertos
    // archivos.
    @PostMapping(
        value="/buscar-archivos",
        headers="Content-Type=application/json"
    )
    public String buscarArchivos(@RequestBody String json) {

        // Envía las peticiones de búsqueda a todos los extremos y obtiene las respuestas.
        JSONObject jsonNotificacion = (new JSONObject())
            .put(
                "buscar-archivos",
                json
            );
        ArrayList<JSONObject> respuestas = enviarMensajeAExtremos("buscar-archivos", jsonNotificacion);

        // Construye un diccionario, cuya clave es el hash de cada archivo, y que contendrá, como
        // valor, el objeto JSON que representa al archivo encontrado en un nodo.
        var diccionarioArchivos = new Hashtable<String, JSONObject>();
        for (JSONObject respuesta: respuestas) {
            JSONArray listaDeArchivos = (JSONArray) respuesta.get("archivos-solicitados");
            for (Object registroArchivoEncontrado: listaDeArchivos) {
                if (!diccionarioArchivos.containsKey(
                    ((JSONObject) registroArchivoEncontrado).get("hash")
                )) {
                    ((JSONObject) registroArchivoEncontrado).remove("hash");
                    diccionarioArchivos.put(
                        ((JSONObject) registroArchivoEncontrado).get("hash").toString(),
                        ((JSONObject) registroArchivoEncontrado)
                    );
                }
            }
        }

        // Construye el JSON, utilizando el diccionario creado anteriormente, que se devolverá
        // como respuesta al extremo que pidió la búsqueda de archivos.
        var resultadoJSON = new JSONArray();
        diccionarioArchivos.forEach(
            (String clave, JSONObject valor) -> {
                resultadoJSON.put(valor);
            }
        );

        return (new JSONObject())
            .put("resultado", resultadoJSON)
            .toString();
    }


    /* Private */
    
    private static final Logger logger = LoggerFactory.getLogger(Maestro.class);
    private List<HostSession> mastersList;
    private List<HostSession> peersList;
    private Dictionary<String, PeerSession> peerSessionDictionary;
    private Dictionary<String, FileDescriptionDictionaryItem> fileDescriptionDictionary;
    private Hashtable<String,FileDescription> fileList;
    @Autowired private HttpServletRequest httpServletRequest;

    private JSONObject gestionarError(Exception e, String mensaje) {
        // Muestra el mensaje en la consola del servidor.
        e.printStackTrace();

        // Devuelve el mensaje para el usuario.
        return new JSONObject().put(
            "Error",
            mensaje
        );
    }
    
    /**
	 * Envía un JSON en un POST al endpoint {@code url}, y en base a la
     * respuesta obtenida (que debe ser un JSON), se retorna tal cual, o
     * se retorna un JSON que contiene un error.
	 * 
	 * @param urlString - Endpoint de la API.
     * @param json - JSON que se envía en el cuerpo de la petición.
	 * @return Un JSON, cuyo primer campo será el resultado, o será "Error" si hubo un problema.
	 */
	private JSONObject postParaJSON(String urlString, JSONObject json) {
        
        // Verifica si la URL está mal formada. Si es así, devuelve un JSON de error, si lo hubo.
        URL url;
        try { url = new URL(urlString); }
        catch (MalformedURLException e) { return gestionarError(e, "Error del servidor."); }

        // Establece la conexión, y devuelve un JSON de error, si lo hubo.
        HttpURLConnection conexionHTTP;
        try { conexionHTTP = (HttpURLConnection) url.openConnection(); }
        catch (IOException e) { return gestionarError(e, "Error del servidor."); }

        // Establece el método de envío, y envía un JSON de error, si lo hubo.
        try { conexionHTTP.setRequestMethod("POST"); }
        catch (ProtocolException e) { return gestionarError(e, "Error del servidor."); }
        conexionHTTP.setRequestProperty("Content-Type", "application/json");

        // Envía el post, y devuelve un JSON de error, si lo hubo.
        conexionHTTP.setDoOutput(true);
        OutputStreamWriter flujoSalida;
        try {
            flujoSalida = new OutputStreamWriter(conexionHTTP.getOutputStream());
            flujoSalida.write(json.toString());
            flujoSalida.flush();
            flujoSalida.close();
        }
        catch (IOException e) { return gestionarError(e, "Error del servidor."); }

        // Obtiene el código de respuesta, y devuelve un JSON de error, si lo hubo.
        int codigoRespuesta = 0;
        try { codigoRespuesta = conexionHTTP.getResponseCode(); }
        catch (IOException e) { return gestionarError(e, "Error del servidor."); }
        System.out.println("* Código de respuesta del servidor: " + codigoRespuesta);

        // Lee la respuesta, y devuelve un JSON de error, si lo hubo.
        StringBuilder respuesta = new StringBuilder();
        try (BufferedReader bufferReader = new BufferedReader(
            new InputStreamReader(conexionHTTP.getInputStream())
        )) {
            String lineaEntrante;
            while ((lineaEntrante = bufferReader.readLine()) != null) { respuesta.append(lineaEntrante); }
            bufferReader.close();
        } catch (IOException e) { return gestionarError(e, "Error del servidor."); }

        // Devuelve el JSON, ya sea con error, o con la respuesta del servidor.
        return new JSONObject(respuesta.toString());
	}

    // Envía un mensaje a todos los maestros.
    private ArrayList<JSONObject> enviarMensajeAMaestros(String endpoint, JSONObject jsonNotificacion) {
        var listaMensajes = new ArrayList<JSONObject>();
        for (HostSession host : mastersList) {
            listaMensajes.add(
                postParaJSON(
                    String.format(
                        "https://%s:8080/%s",
                        host.getIp(),
                        endpoint
                    ),
                    jsonNotificacion
                ).append("host", host.getIp())
            );
        }

        return listaMensajes;
    }

    // Envía un mensaje a todos los extremos.
    private ArrayList<JSONObject> enviarMensajeAExtremos(String endpoint, JSONObject jsonNotificacion) {
        var listaMensajes = new ArrayList<JSONObject>();
        for (HostSession host : peersList) {
            listaMensajes.add(
                postParaJSON(
                    String.format(
                        "https://%s:8080/%s",
                        host.getIp(),
                        endpoint
                    ),
                    jsonNotificacion
                ).append("host", host.getIp())
            );
        }

        return listaMensajes;
    }

    private class HostSession {

        public HostSession(String ip, Date timestamp, List<FileDescription> fileDescriptionList) {
            this.ip = ip;
            this.timestamp = timestamp;
            // Por cada archivo en la lista, agregar 
        }

        public String getIp() {
            return ip;
        }

        public long getTimestamp() {
            return timestamp.getTime();
        }


        /* Private */

        private Date timestamp;
        private String ip;
        private List<FileDescription> fileDescriptionList;

    }

    private class FileDescriptionDictionaryItem {

        public FileDescription(Long sizeInBytes, String hash) {
            this.sizeInBytes = sizeInBytes;
            this.hash = hash;
        }


        /* Private */

        private Long sizeInBytes;
        private String hash;
    }

}
