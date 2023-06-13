package ar.edu.unlu.sdypp.grupo1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ar.edu.unlu.sdypp.grupo1.requests.FileDescriptionRequest;
import ar.edu.unlu.sdypp.grupo1.requests.InformRequest;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
public class Maestro {

    public static void main(String[] args)
    { SpringApplication.run(Maestro.class, args); }

    public Maestro() {

        // Carga las variables de entorno.
        dotenv = Dotenv.configure().load();
        postgresUrl = dotenv.get("POSTGRES_URL");
        postgresUser = dotenv.get("POSTGRES_USER");
        postgresPassword = dotenv.get("POSTGRES_PASSWORD");

        // Configura la frecuencia que tendrá la revisión de los
        // extremos que no actualizaron sus conexiones (2 minutos).
        var timer = new Timer();
        var task = new TimerTask() {

            @Override
            public void run() {
                try { cleanPeers(); }
                catch (SQLException e) {
                    gestionarError(e, "Error con el servidor SQL.", null);
                    System.exit(1);
                } catch (ClassNotFoundException e) {
                    gestionarError(e, "Error con el driver JDBC.", null);
                    System.exit(1);
                }
            }

        };
        timer.scheduleAtFixedRate(task, 120000, 120000);

    }

    // TODO: Validar que el objeto JSON tenga datos.
    /**
     * Utilizado por los extremos para registrarse en la red,
     * para dar prueba de vida y para anunciar cambios en su
     * lista de archivos.
     * 
     * @param informRequest - Contiene la lista de archivos del extremo
     * que se conecta.
     * @return Un objeto JSON con el resultado de las operaciones.
     */
    @PostMapping(
        value="/inform",
        headers="Content-Type=application/json"
    )
    public ResponseEntity<String> inform(@RequestBody InformRequest informRequest) {

        logger.debug(String.format(
            "Se ejecuta el método inform. [informRequest = %s] [X-Forwarded-For: %s]",
            informRequest.toString(),
            httpServletRequest.getHeader("X-Forwarded-For")
        ));

        // Genera el mensaje y código para el caso en el que
        // no esté especificado correctamente el header
        // 'X-Forwarded-For'.
        String clientIp = getSourceIp();
        var returningJson = new JSONObject()
            .put("Respuesta", "Debe especificar la IP del cliente en el header 'X-Forwarded-For'.");
        var statusCode = HttpStatus.BAD_REQUEST;

        if (clientIp != null) {

            logger.debug(String.format(
                "Se anuncia el host %s.",
                clientIp
            ));

            // Genera el mensaje y el código para el caso en el que el
            // parámetro esté vacío.
            returningJson = new JSONObject()
                .put("Respuesta", "Debe especificar el arreglo 'files'.");
            statusCode = HttpStatus.BAD_REQUEST;
            
            if (informRequest.getFiles() != null) {

                /**
                 * Si la IP del extremo ya está registrada (AA), modificar
                 * la lista de artículos si hace falta, y almacena de forma
                 * obligatoria el timestamp (AB).
                 * 
                 * Si la IP del extremo no está registrada (BA), guarda el registro
                 * del extremo, la lista de artículos y el timestamp (BB).
                 */
                returningJson = (new JSONObject())
                    .put("Respuesta","Sin cambios. Fecha y hora de acceso actualizadas.");
                statusCode = HttpStatus.OK;
                try {

                    Class.forName("org.postgresql.Driver");
                    
                    postgresConnection = DriverManager.getConnection(
                        postgresUrl,
                        postgresUser,
                        postgresPassword
                    );

                    ResultSet resultSet = executeQuery(
                        String.format(
                            "SELECT * FROM active_peers WHERE peerIp = '%s'",
                            clientIp
                        ),
                        postgresConnection
                    );

                    // (AA)
                    if (resultSet.next()) {
                        
                        // (AB)
                        if (updateFileList(
                            clientIp,
                            informRequest.getFiles(),
                            postgresConnection
                        )) {
                            returningJson = (new JSONObject())
                                .put("Respuesta","Archivos modificados.");
                            statusCode = HttpStatus.OK;
                        }

                    // (BA)
                    } else {

                        // (BB)
                        recordPeer(
                            clientIp,
                            informRequest.getFiles(),
                            postgresConnection
                        );

                        // Configura el mensaje y código que serán devueltos al cliente.
                        String responseMessage;
                        if (informRequest.getFiles() != null) responseMessage = "Extremo y archivos registrados.";
                        else responseMessage = "Extremo registrado.";
                        returningJson = (new JSONObject())
                            .put("Respuesta", responseMessage);
                        statusCode = HttpStatus.OK;

                    }

                    postgresConnection.close();

                } catch (SQLException e) {
                    returningJson = gestionarError(e, "Error con el servidor SQL.", statusCode);

                } catch (ClassNotFoundException e) {
                    returningJson = gestionarError(e, "Error con el driver JDBC.", statusCode);
                }

            }

        }

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );

    }

    // Endpoint utilizado por los extremos para pedir que se busquen ciertos
    // archivos.
    @GetMapping(
        value="/query"
    )
    public ResponseEntity<String> query(@RequestParam String file) {
        
        logger.debug(String.format(
            "Se ejecuta método query. [file = %s]",
            file
        ));

        // Genera el mensaje y código para el caso en el que
        // no esté especificado correctamente el header
        // 'X-Forwarded-For'.
        String clientIp = getSourceIp();
        var returningJson = new JSONObject()
            .put("Respuesta", "Debe especificar la IP del cliente en el header 'X-Forwarded-For'.");
        var statusCode = HttpStatus.BAD_REQUEST;

        if (clientIp != null) {

            // Este objeto JSON se devuelve si el extremo no está registrado
            // en la BD.
            returningJson = (new JSONObject())
                .put("Respuesta", "El extremo no está registrado en la BD.");
            statusCode = HttpStatus.BAD_REQUEST;

            try {

                // Establece cuál será el driver JDBC.
                Class.forName("org.postgresql.Driver");
                                
                // Realiza la conexión con la BD.
                postgresConnection = DriverManager.getConnection(
                    postgresUrl,
                    postgresUser,
                    postgresPassword
                );

                // Realiza la búsqueda si el extremo está registrado
                // en la BD.
                ResultSet resultSet = executeQuery(
                    String.format(
                        "SELECT * FROM active_peers WHERE peerIp = '%s'",
                        clientIp
                    ),
                    postgresConnection
                );
                if (resultSet.next()) {

                    // Este objeto JSON se devuelve si el parámetro está vacío.
                    returningJson = (new JSONObject())
                        .put("Respuesta", "El parámetro 'file' no puede estar vacío.");
                    statusCode = HttpStatus.BAD_REQUEST;

                    if (file != "") {

                        // TODO: Valida que el parámetro tenga caracteres válidos.
                        // Si no es válido, devuelve mensaje. Si es válido, avanza.

                        // Busca en la BD y obtiene los resultados.
                        String queryStatement = String.format(
                            "SELECT * " +
                            "FROM files " +
                            "WHERE fileName like '%%%s%%'",
                            file
                        );
                        resultSet = executeQuery(queryStatement, postgresConnection);

                        // Construye el JSON que se devuelve.
                        var filesJsonArray = new JSONArray();
                        var fileDescriptionJsonObject = new JSONObject();
                        while (resultSet.next()) {
                            fileDescriptionJsonObject = (new JSONObject())
                                .put("host",resultSet.getString("peerIp"))
                                .put("name",resultSet.getString("fileName"))
                                .put("sizeInBytes",resultSet.getLong("sizeInBytes"));
                            filesJsonArray.put(fileDescriptionJsonObject);
                        }
                        returningJson = (new JSONObject())
                            .put("files", filesJsonArray);
                        statusCode = HttpStatus.OK;

                    }

                }

            } catch (SQLException e) {
                returningJson = gestionarError(e, "Error con el servidor SQL.", statusCode);
            } catch (ClassNotFoundException e) {
                returningJson = gestionarError(e, "Error con el driver JDBC.",  statusCode);
            }

        }

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );

    }

    // Endpoint utilizado por los extremos para anunciar que se desconectan.
    @PostMapping(
        value="/exit"
    )
    public ResponseEntity<String> exit()
        throws ClassNotFoundException, SQLException
    {

        logger.debug("Se ejecuta el método exit.");

        // Genera el mensaje y código para el caso en el que
        // no esté especificado correctamente el header
        // 'X-Forwarded-For'.
        String clientIp = getSourceIp();
        var returningJson = new JSONObject()
            .put("Respuesta", "Debe especificar la IP del cliente en el header 'X-Forwarded-For'.");
        var statusCode = HttpStatus.BAD_REQUEST;

        if (clientIp != null) {

            // Este objeto JSON se devuelve si el extremo no está registrado
            // en la BD.
            returningJson = (new JSONObject())
                .put("Respuesta", "El extremo no está registrado en la base de datos.");
            statusCode = HttpStatus.BAD_REQUEST;

            // Establece el driver para JDBC y se conecta al servidor
            // de la BD.
            Class.forName("org.postgresql.Driver");
            postgresConnection = DriverManager.getConnection(
                postgresUrl,
                postgresUser,
                postgresPassword
            );

            // Elimina los registros del extremo, si el extremo está registrado
            // en la BD.
            ResultSet resultSet = executeQuery(
                String.format(
                    "SELECT * FROM active_peers WHERE peerIp = '%s'",
                    clientIp
                ),
                postgresConnection
            );
            if (resultSet.next()) {

                // Indica que no se guardarán los datos en la BD automáticamente,
                // sino que se registrarán cuando se lo indique mediante código.
                postgresConnection.setAutoCommit(false);

                // Elimina los registros del extremo.
                String deleteStatement = String.format(
                    "DELETE " +
                    "FROM active_peers " +
                    "WHERE peerIp = '%s'",
                    clientIp
                );
                executeStatement(deleteStatement, postgresConnection);
                logger.info(String.format(
                    "Se eliminaron los registros del extremo. [IP del extremo = %s]",
                    clientIp
                ));

                // Guarda los cambios en la BD, deja el autocommit como estaba,
                // y cierra la conexión.
                postgresConnection.commit();
                postgresConnection.setAutoCommit(true);
                postgresConnection.close();

                // Configura el mensaje que se devolverá al cliente.
                returningJson = (new JSONObject())
                    .put("Respuesta", "Extremo y archivos eliminados.");
                statusCode = HttpStatus.OK;
            
            }

        }

        return ResponseEntity
            .status(statusCode)
            .body(
                returningJson.toString()
            );

    }


    /* Private */
    
    private static final Logger logger = LoggerFactory.getLogger(Maestro.class);
    final Dotenv dotenv;
    Connection postgresConnection;
    String postgresUrl;
    String postgresUser;
    String postgresPassword;
    @Autowired private HttpServletRequest httpServletRequest;

    private String getSourceIp() {
        
        logger.debug("Se ejecuta el método getSourceIp.");

        // // Verifica que exista el header 'X-Forwarded-For'.
        // String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        // logger.debug(String.format(
        //     "httpServletRequest.getHeader(\"X-Forwarded-For\") = %s",
        //     clientIp
        // ));
        // if (clientIp != null && !clientIp.isEmpty()) {
            
        //     // Dado que el header puede contener más de un IP, separados por coma,
        //     // y que la primera IP es la que se busca, se parsea.
        //     clientIp = clientIp.split(",")[0].trim();

        // } else {

        //     // No existe el header 'X-Forwarded-For' o está vacío.
        //     clientIp =
        //         clientIp != null
        //         ? clientIp = null
        //         : clientIp;

        // }

        String clientIp = httpServletRequest.getRemoteHost();
        
        logger.debug(String.format(
            "Se devuelve clienteIp. [%s]",
            clientIp
        ));
        
        return clientIp;

    }

    /**
     * Gestiona errores del lado del servidor.
     * 
     * @param e - Excepción que será manejada por este método.
     * @param mensaje - Mensaje que se logueará.
     * @param httpStatusOutVariable - Variable para almacenar el código HTTP que generará este método. Puede ser null, si no hace falta guardar el código HTTP.
     * @return Objeto JSON con un mensaje por defecto indicando que hubo un error en el servidor.
     */
    private JSONObject gestionarError(Exception e, String mensaje, HttpStatus httpStatusOutVariable) {

        // Muestra el mensaje en la consola del servidor.
        e.printStackTrace();
        logger.debug(mensaje);

        // Devuelve el mensaje JSON y el código HTTP.
        if (httpStatusOutVariable != null)
            httpStatusOutVariable = HttpStatus.INTERNAL_SERVER_ERROR;

        return (new JSONObject())
            .put("Respuesta", "Error interno del servidor.");

    }

    // Ejecuta una consulta en la tabla que luego puede utilizarse para modificar
    // la BD.
    // La conexión ya tiene que estar establecida.
    public ResultSet executeQuery(String query, Connection postgresConnection)
        throws SQLException
    {
        logger.debug(String.format(
            "executeQuery(%s)", query
        ));

        Statement statement = postgresConnection.createStatement(
            ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_UPDATABLE
        );
        ResultSet resultSet = statement.executeQuery(query);
        logger.info("Consulta ejecutada.");

        return resultSet;
    }

    // Ejecuta una sentencia en la tabla.
    // La conexión ya tiene que estar establecida.
    public void executeStatement(String sqlStatement, Connection postgresConnection)
        throws SQLException
    {
        logger.debug(String.format(
            "Se ejecuta el método executeStatement. [statement = %s]",
            sqlStatement
        ));

        Statement statement = postgresConnection.createStatement();
        statement.executeUpdate(sqlStatement);
        logger.info(String.format("Sentencia ejecutada. [%s]", sqlStatement));
    }

    // Actualiza la lista de archivos que la base de datos tiene almacenada
    // sobre el extremo, si hace falta. Devuelve 'true' si hubo modificación,
    // o 'false' si no hubo modificación.
    private boolean updateFileList(
        String peerIp,
        List<FileDescriptionRequest> fileList,
        Connection postgresConnection
    )
        throws SQLException
    {

        // La bandera para indicar si se modificaron los datos en la BD
        // o no.
        boolean updated = false;

        // Indica que no se guardarán los datos en la BD automáticamente,
        // sino que se registrarán cuando se lo indique mediante código.
        postgresConnection.setAutoCommit(false);
        
        // Obtiene la lista de archivos almacenada en la BD, que
        // corresponde al extremo pasado por parámetro.
        ResultSet storedFilesResultSet = executeQuery(
            String.format(
                "SELECT * " +
                "FROM files " +
                "WHERE peerIp = '%s'",
                peerIp
            ),
            postgresConnection
        );

        /**
         * Si no hay registros de archivos en la BD, significa que todos los
         * archivos que se enviaron en el POST deben almacenarse en la BD,
         * si es que el extremo anunció al menos 1 archivo compartido.
         */
        if (!storedFilesResultSet.next()) {

            // Agrega todos los archivos a la BD, si hay al menos 1 archivo
            // para compartir.
            if (fileList != null) {

                // Marca la bandera que indica que hubo modificación en el listado
                // de archivos del extremo.
                updated = true;

                // Registra en la BD las descripciones de archivo.
                for (FileDescriptionRequest peerFileDescription: fileList) {
                    String insertStatement = String.format(
                        "INSERT " +
                        "INTO files (fileName, sizeInBytes, peerIp) " +
                        "VALUES ('%s', %d, '%s')",
                        peerFileDescription.getName(),
                        peerFileDescription.getSizeInBytes(),
                        peerIp
                    );
                    executeStatement(insertStatement, postgresConnection);
                }

            }

        // Si hay archivos en la BD, hay que determinar si la lista cambió para
        // realizar alguna modificación.
        } else {

            /** 
             * Por cada registro en la lista de archivos enviada por el extremo:
             * si existe un registro con el mismo nombre de archivo en el resultado
             * de la consulta (AA), se elimina el registro de este último (AB) porque
             * no hace falta ya que no se modificará en la BD; y si
             * no existe un registro con el mismo nombre de archivo (BA), los datos
             * enviados del archivo son almacenados en una lista temporal (BB).
             * 
             * Luego, se eliminan de la BD todos los registros que hayan quedado en
             * la lista de resultados luego de haber eliminado los duplicados (CA), y
             * se agregan a la BD los registros que hayan quedado en la lista temporal
             * (CB).
             * 
             * En cualquier caso en que haya habido una modificación de la BD, se
             * devolverá 'true' como valor de retorno; si no, 'false'.
             */

            // Crea un arreglo con los nombres de archivo de los registros de la BD.
            // Luego de procesar los datos, este arreglo contendrá, eventualmente,
            // los registros que tengan que eliminarse de la BD.
            var fileDescriptionsToRemove = new ArrayList<String>();
            do {
                fileDescriptionsToRemove.add(storedFilesResultSet.getString("fileName"));
            } while(storedFilesResultSet.next());

            // Crea un arreglo para contener los registros de descripción de archivo que
            // se tendrán que insertar en la BD. La creación de esta variable es necesaria
            // incluso si el extremo anuncia que comparte cero archivos.
            var fileDescriptionsToInsert = new ArrayList<FileDescriptionRequest>();

            // Marca los archivos que no se eliminarán de la BD porque están también
            // en la carpeta compartida del extremo.
            if (fileList != null) {
                for (FileDescriptionRequest peerFileDescription: fileList) {

                    // (AA)
                    if(fileDescriptionsToRemove.contains(
                        peerFileDescription.getName()
                    ))

                        // (AB)
                        fileDescriptionsToRemove.remove(peerFileDescription.getName());

                    // (BA)
                    else {

                        // (BB)
                        fileDescriptionsToInsert.add(peerFileDescription);

                    }
                }
            }

            // (CA)
            for (String fileNameToRemove: fileDescriptionsToRemove) {
                String deleteStatement = String.format(
                    "DELETE " +
                    "FROM files " +
                    "WHERE peerIp = '%s' " +
                        "AND fileName = '%s'",
                    peerIp,
                    fileNameToRemove
                );
                executeStatement(deleteStatement, postgresConnection);
            }

            // (CB)
            for (FileDescriptionRequest fileDescriptionToInsert: fileDescriptionsToInsert) {
                String insertStatement = String.format(
                    "INSERT " +
                    "INTO files (fileName, sizeInBytes, peerIp) " +
                    "VALUES ('%s', %d, '%s')",
                    fileDescriptionToInsert.getName(),
                    fileDescriptionToInsert.getSizeInBytes(),
                    peerIp
                );
                executeStatement(insertStatement, postgresConnection);
            }

            // Si hubo modificaciones en la lista de archivos, marca la
            // correspondiente bandera.
            if (
                !fileDescriptionsToRemove.isEmpty()
                || !fileDescriptionsToInsert.isEmpty()
            )
                updated = true;

        }
        
        // Registra el timestamp y guarda los cambios en la BD.
        String updateStatement = String.format(
            "UPDATE active_peers " +
            "SET lastConnectionTimestamp = %d " +
            "WHERE peerIp = '%s'",
            (new Date()).getTime(),
            peerIp
        );
        executeStatement(updateStatement, postgresConnection);
        postgresConnection.commit();

        // Deja la conexión en el estado que estaba.
        postgresConnection.setAutoCommit(true);

        return updated;
    }

    /**
     * Registra el extremo, y los archivos que comparte, si los hay.
     * 
     * @param peerIp - IP del extremo.
     * @param fileList - Lista de los archivos que se tienen que registrar. Puede ser {@code null}, si no hay archivos para compartir.
     * @param postgresConnection - Conexión preconfigurada con el servidor PostgreSQL.
     * @throws SQLException
     */
    private void recordPeer(
        String peerIp,
        List<FileDescriptionRequest> fileList,
        Connection postgresConnection
    )
        throws SQLException
    {

        // Indica que no se guardarán los datos en la BD automáticamente,
        // sino que se registrarán cuando se lo indique mediante código.
        postgresConnection.setAutoCommit(false);

        // Agrega el extremo a la BD.
        String insertStatement;
        insertStatement = String.format(
            "INSERT " +
            "INTO active_peers (peerIp, lastConnectionTimestamp) " +
            "VALUES ('%s', %d)",
            peerIp,
            (new Date()).getTime()
        );
        executeStatement(insertStatement, postgresConnection);

        // Agrega las descripciones de archivo del extremo a la BD,
        // si el extremo tiene archivos que compartir.
        if (fileList !=  null) {
            for (FileDescriptionRequest peerFileDescription: fileList) {
                insertStatement = String.format(
                    "INSERT " +
                    "INTO files (fileName, sizeInBytes, peerIp) " +
                    "VALUES ('%s', %d, '%s')",
                    peerFileDescription.getName(),
                    peerFileDescription.getSizeInBytes(),
                    peerIp
                );
                executeStatement(insertStatement, postgresConnection);
            }
        }

        // Guarda los cambios en la BD y deja la conexión en el estado
        // que estaba.
        postgresConnection.commit();
        postgresConnection.setAutoCommit(true);

    }

    /**
     * Elimina de la BD los extremos cuya última conexión sucedió
     * hace más de 2 minutos.
     */
    private void cleanPeers() 
        throws SQLException, ClassNotFoundException
    {

        logger.debug("Se ejecuta el método cleanPeers.");

        // Establece el driver para JDBC y se conecta al servidor
        // de la BD.
        Class.forName("org.postgresql.Driver");
        postgresConnection = DriverManager.getConnection(
            postgresUrl,
            postgresUser,
            postgresPassword
        );

        // Indica que no se guardarán los datos en la BD automáticamente,
        // sino que se registrarán cuando se lo indique mediante código.
        postgresConnection.setAutoCommit(false);

        // Obtiene todos los registros de los extremos en la BD.
        ResultSet resultSet = executeQuery(
            "SELECT * FROM active_peers",
            postgresConnection
        );

        if (resultSet.next()) {

            // Si la última vez que se conectó el extremo del registro
            // actual fue hace más de 2 minutos, elimina el registro
            // del extremo y de sus archivos.
            logger.debug(String.format(
                "(new Date()).getTime() - resultSet.getLong(\"lastConnectionTimestamp\") > 120000 = %b " +
                "[(new Date()).getTime() = %d] " +
                "[resultSet.getLong(\"lastConnectionTimestamp\") = %d] " +
                "[(new Date()).getTime() - resultSet.getLong(\"lastConnectionTimestamp\") = %d]",
                (new Date()).getTime() - resultSet.getLong("lastConnectionTimestamp") > 120000,
                (new Date()).getTime(),
                resultSet.getLong("lastConnectionTimestamp"),
                (new Date()).getTime() - resultSet.getLong("lastConnectionTimestamp")
            ));
            if (
                (new Date()).getTime() - resultSet.getLong("lastConnectionTimestamp") > 120000
            ) {
                logger.info(String.format(
                    "Se eliminarán los registros de un extremo. [IP de extremo = %s]",
                    resultSet.getString("peerIp")
                ));
                resultSet.deleteRow();
                logger.info("Registro eliminado.");
            }

        }

        // Guarda los cambios en la BD, deja el autocommit como estaba,
        // y cierra la conexión.
        postgresConnection.commit();
        postgresConnection.setAutoCommit(true);
        postgresConnection.close();

    }

}