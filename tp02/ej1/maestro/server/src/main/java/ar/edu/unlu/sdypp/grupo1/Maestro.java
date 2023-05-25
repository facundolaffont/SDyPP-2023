package ar.edu.unlu.sdypp.grupo1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import ar.edu.unlu.sdypp.grupo1.requests.FileDescriptionRequest;
import ar.edu.unlu.sdypp.grupo1.requests.InformRequest;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

@SpringBootApplication
@RestController
public class Maestro {

    public static void main(String[] args)
    { SpringApplication.run(Maestro.class, args); }

    public Maestro() {
        // Carga las variables de entorno.
        dotenv = Dotenv.configure().load();
        postgresUrl = dotenv.get("POSTGRES_URL");

        // TODO: añadir los datos de los maestros.
        mastersList = new ArrayList<String>();
    }

    /**
     * Utilizado por los extremos para registrarse en la red,
     * para dar prueba de vida y para anunciar cambios en su
     * lista de archivos.
     * 
     * @param informRequest - contiene la lista de archivos del extremo
     * que se conecta.
     * @return - Un objeto JSON con el resultado de las operaciones.
     */
    @PostMapping(
        value="/inform",
        headers="Content-Type=application/json"
    )
    public String inform(@RequestBody InformRequest informRequest) {
        logger.debug(String.format(
            "Se anuncia el host <%s:%s>.",
            httpServletRequest.getRemoteHost(),
            httpServletRequest.getRemotePort()
        ));

        /** TODO (falta eliminar los archivos que dejen de existir)
         * Si la IP del extremo ya está registrada (A), modificar
         * la lista de artículos si hace falta (AA), y si
         * se modificó (C), deja marcada una bandera (E) para enviar
         * luego la notificación al resto de los maestros.
         * 
         * Si la IP del extremo no está registrada (G), crea el registro
         * del extremo (GG), guarda la lista de artículos (D), y deja
         * una bandera marcada (J) para envíar luego la notificación al
         * resto de los maestros.
         * 
         * En cualquiera de los casos, al finalizar se debe actualizar
         * el timestamp (B), y enviar a todos los maestros el nuevo
         * timestamp y la IP del extremo (h), y, si se modificó la lista
         * de artículos o si el extremo no estaba registrado, también
         * debe enviar la lista de artículos (i).
         * 
         */
        /*
        boolean newPeerOrUpdatedFileList = false;

        // (A)
        PeerSession queriedPeerRegister = peerSessionMap.get(httpServletRequest.getRemoteHost());
        if (queriedPeerRegister != null) {

            // (C)
            if(queriedPeerRegister.updateFileDescriptionList()) // (AA)
                newPeerOrUpdatedFileList = true; // (E)
            
            // (B)
            queriedPeerRegister.setTimestamp(new Date());

        // (G)
        } else {

            // (GG)
            PeerSession newPeer = new PeerSession(
                httpServletRequest.getRemoteHost(),
                informRequest.getFiles()
            );
            peerSessionMap.put(
                httpServletRequest.getRemoteHost(),
                newPeer
            );
            
            // (D)
            newPeer.updateFileDescriptionList();

            // (J)
            newPeerOrUpdatedFileList = true;

            // (B)
            newPeer.setTimestamp(new Date());

        }

        // (h) -->
        
        // Agrega el IP del extremo y su nuevo timestamp.
        JSONObject jsonNotification = (new JSONObject());
        // .put(
        //     "peer",
        //     httpServletRequest.getRemoteHost()
        // ).put(
        //     "timestamp",
        //     // TIMESTAMP
        // )

        ArrayList<JSONObject> listaRespuestas = null;
        if (newPeerOrUpdatedFileList) {

            // Agrega la lista de archivos.
            var filesArray = new JSONArray();
            for (FileDescriptionRequest fileDescriptionRequest: informRequest.getFiles()) {
                var tempJsonObject = (new JSONObject())
                    .put("name", fileDescriptionRequest.getName())
                    .put("sizeInBytes", fileDescriptionRequest.getSizeInBytes())
                    .put("hash", fileDescriptionRequest.getHash());
                filesArray.put(tempJsonObject);
            }
            jsonNotification.put(
                "files",
                filesArray
            );

            // Envía el mensaje a todos los maestros.
            listaRespuestas = enviarMensajeAMaestros("update", jsonNotification);

        }

        // Construir la respuesta única con los mensajes de la lista de respuestas.
        if (listaRespuestas ==  null)
            return (new JSONObject())
                .put("Respuesta", "200 (OK)")
                .toString();
        else
            return (new JSONObject())
                .put("Respuesta", listaRespuestas)
                .toString();
        */

        // (A)
        try {
            postgresConnection = DriverManager.getConnection(postgresUrl);

            executeQuery("
                SELECT 
            ");

            postgresConnection.close();
        } catch (SQLException e) {

        }
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
    // @PostMapping(
    //     value="/buscar-archivos",
    //     headers="Content-Type=application/json"
    // )
    // public String buscarArchivos(@RequestBody String json) {

    //     // Envía las peticiones de búsqueda a todos los extremos y obtiene las respuestas.
    //     JSONObject jsonNotificacion = (new JSONObject())
    //         .put(
    //             "buscar-archivos",
    //             json
    //         );
    //     ArrayList<JSONObject> respuestas = enviarMensajeAExtremos("buscar-archivos", jsonNotificacion);

    //     // Construye un diccionario, cuya clave es el hash de cada archivo, y que contendrá, como
    //     // valor, el objeto JSON que representa al archivo encontrado en un nodo.
    //     var diccionarioArchivos = new Hashtable<String, JSONObject>();
    //     for (JSONObject respuesta: respuestas) {
    //         JSONArray listaDeArchivos = (JSONArray) respuesta.get("archivos-solicitados");
    //         for (Object registroArchivoEncontrado: listaDeArchivos) {
    //             if (!diccionarioArchivos.containsKey(
    //                 ((JSONObject) registroArchivoEncontrado).get("hash")
    //             )) {
    //                 ((JSONObject) registroArchivoEncontrado).remove("hash");
    //                 diccionarioArchivos.put(
    //                     ((JSONObject) registroArchivoEncontrado).get("hash").toString(),
    //                     ((JSONObject) registroArchivoEncontrado)
    //                 );
    //             }
    //         }
    //     }

    //     // Construye el JSON, utilizando el diccionario creado anteriormente, que se devolverá
    //     // como respuesta al extremo que pidió la búsqueda de archivos.
    //     var resultadoJSON = new JSONArray();
    //     diccionarioArchivos.forEach(
    //         (String clave, JSONObject valor) -> {
    //             resultadoJSON.put(valor);
    //         }
    //     );

    //     return (new JSONObject())
    //         .put("resultado", resultadoJSON)
    //         .toString();
    // }


    /* Private */
    
    private static final Logger logger = LoggerFactory.getLogger(Maestro.class);
    private List<String> mastersList;
    private Map<String /* IP */, PeerSession> peerSessionMap;
    private Map<String /* Nombre del archivo */, List<FileDescription>> fileDescriptionMap;
    final Dotenv dotenv;
    Connection postgresConnection;
    String postgresUrl;
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

    // Ejecuta una consulta en la tabla.
    public ResultSet executeQuery(String query)
        throws SQLException, NotValidAttributeException
    {
        logger.debug(String.format(
            "executeQuery(%s)", query
        ));

        Statement statement = postgresConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        logger.info("Consulta ejecutada.");

        return resultSet;
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
    private ArrayList<JSONObject> enviarMensajeAMaestros(
        String endpoint,
        JSONObject jsonNotificacion
    ) {
        var listaMensajes = new ArrayList<JSONObject>();
        for (String masterIp : mastersList) {
            listaMensajes.add(

                // Contruye el objeto JSON que se agregará
                // a la lista de mensajes que devolverá esta
                // función. Dicho objeto está conformado por
                // la respuesta recibida por el maestro al cual
                // se le envía el mensaje, junto con su IP.
                postParaJSON(
                    String.format(
                        "https://%s:8080/%s",
                        masterIp,
                        endpoint
                    ),
                    jsonNotificacion
                ).append("host", masterIp)

            );
        }

        return listaMensajes;
    }

    // // Envía un mensaje a todos los extremos.
    // private ArrayList<JSONObject> enviarMensajeAExtremos(String endpoint, JSONObject jsonNotificacion) {
    //     var listaMensajes = new ArrayList<JSONObject>();
    //     for (PeerSession host : peersList) {
    //         listaMensajes.add(
    //             postParaJSON(
    //                 String.format(
    //                     "https://%s:8080/%s",
    //                     host.getIp(),
    //                     endpoint
    //                 ),
    //                 jsonNotificacion
    //             ).append("host", host.getIp())
    //         );
    //     }

    //     return listaMensajes;
    // }

    private class PeerSession {

        public PeerSession(
            String peerIp,
            List<FileDescriptionRequest> receivedFileDescriptionList
        ) {
            this.peerIp = peerIp;
            fileDescriptionList = receivedFileDescriptionList;
        }

        public long getTimestamp() {
            return timestamp.getTime();
        }

        // Recorre la lista de descripción de archivos y actualiza los
        // registros que sean necesarios. Devuelve true si hubo alguna
        // modificación.
        public boolean updateFileDescriptionList() {
            boolean fileDescriptionListModified = false;
            for(FileDescriptionRequest receivedFileDescription: fileDescriptionList) { // Por cada descripción de archivo que haya anunciado el extremo.

                /**
                 * Si no hay descripción de archivo registrado con el nombre de
                 * la descripción actual (A), añade una lista vacía en la clave
                 * correspondiente al nombre en el mapa de archivos (B), para
                 * guardar la descripción actual posteriormente.
                 */ 
                boolean addFile = false;
                List<FileDescription> storedFileDescriptionList = fileDescriptionMap.get(receivedFileDescription.getName());
                if (storedFileDescriptionList == null) { // (A)
                    storedFileDescriptionList = new ArrayList<FileDescription>(); // (B)
                    addFile = true;

                    /**
                     * Si ya existen registros de archivos con el mismo nombre
                     * que el de la descripción de archivo actual (A), verifica
                     * si ya está registrado el hash del archivo en la lista de
                     * descripciones guardada en el mapa de archivos (B). Sólo
                     * en caso de que no exista el hash (C), se modifica una
                     * bandera (D) para que luego se agregue una entrada nueva a
                     * la lista de descripciones (H).
                     * 
                     * Si el hash ya existe (E) y la IP no está registrada en la
                     * lista de IPs (F), agrega la IP a la lista (G).
                     */
                } else { // (A)
                    
                    // (B)
                    boolean fileHashExists = false;
                    for (FileDescription storedFileDescription: storedFileDescriptionList) {
                        if (storedFileDescription.getHash() == receivedFileDescription.getHash()) { // (E)
                            fileHashExists = true;

                            if (!storedFileDescription.getIpList().contains(peerIp)) { // (F)
                                storedFileDescription.getIpList().add(peerIp); // (G)
                                fileDescriptionListModified = true;
                            }

                            break;
                        }
                    }

                    if (!fileHashExists) { // (C)
                        addFile = true; // (D)
                        fileDescriptionListModified = true;
                    }

                }

                // (H)
                if(addFile)
                    storedFileDescriptionList.add(new FileDescription(
                        receivedFileDescription.getHash(),
                        receivedFileDescription.getSizeInBytes(),
                        peerIp
                    ));
            }

            return fileDescriptionListModified;
        }


        /* Private */

        private String peerIp;
        @Setter private Date timestamp;
        private List<FileDescriptionRequest> fileDescriptionList;

    }

    private class FileDescription {
        
        public FileDescription(
            String hash,
            Long sizeInBytes,
            String ip
        ) {
            this.hash = hash;
            this.sizeInBytes = sizeInBytes;
            this.ipList.add(ip);
        }


        /* Private */

        @Getter private String hash;
        @Getter private Long sizeInBytes;
        @Getter private List<String> ipList;
    }

}
