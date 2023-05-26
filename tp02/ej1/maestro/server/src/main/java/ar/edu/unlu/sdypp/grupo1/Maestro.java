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
                    gestionarError(e, "Error con el servidor SQL.");
                } catch (ClassNotFoundException e) {
                    gestionarError(e, "Error con el driver JDBC.");
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
    public String inform(@RequestBody InformRequest informRequest) {
        logger.debug(String.format(
            "Se anuncia el host <%s:%s>.",
            httpServletRequest.getRemoteHost(),
            httpServletRequest.getRemotePort()
        ));

        /**
         * Si la IP del extremo ya está registrada (AA), modificar
         * la lista de artículos si hace falta, y almacena de forma
         * obligatoria el timestamp (AB).
         * 
         * Si la IP del extremo no está registrada (BA), guarda el registro
         * del extremo, la lista de artículos y el timestamp (BB).
         */
        var returnedJson = (new JSONObject()
            .put("Respuesta","200 (OK)"))
            .put("Descripción","Sin cambios. Fecha y hora de acceso actualizadas.");
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
                    httpServletRequest.getRemoteHost()
                ),
                postgresConnection
            );

            // (AA)
            if (resultSet.next()) {
                
                // (AB)
                if (updateFileList(
                    httpServletRequest.getRemoteHost(),
                    informRequest.getFiles(),
                    postgresConnection
                ))
                    returnedJson = (new JSONObject())
                        .put("Resultado","200 (OK)")
                        .put("Descripción","Archivos modificados.");


            // (BA)
            } else {

                // (BB)
                recordPeer(
                    httpServletRequest.getRemoteHost(),
                    informRequest.getFiles(),
                    postgresConnection
                );

                returnedJson = (new JSONObject())
                    .put("Resultado","200 (OK)")
                    .put("Descripción","Extremo y archivos registrados.");

            }

            postgresConnection.close();

        } catch (SQLException e) {
            returnedJson = gestionarError(e, "Error con el servidor SQL.");
        } catch (ClassNotFoundException e) {
            returnedJson = gestionarError(e, "Error con el driver JDBC.");
        }

        return returnedJson.toString();
    }

    // // Endpoint utilizado por los otros maestros para enviar
    // // información de actualización.
    // @PostMapping(
    //     value="/update",
    //     headers="Content-Type=application/json"
    // )
    // public String update(@RequestBody String json) {}

    // Endpoint utilizado por los extremos para pedir que se busquen ciertos
    // archivos.
    // @GetMapping(
    //     value="/query"
    // )
    // public String query() {}

    // Endpoint utilizado por los extremos para anunciar que se desconectan.
    @PostMapping(
        value="/exit"
    )
    public String exit()
        throws ClassNotFoundException, SQLException
    {

        logger.debug("Se ejecuta el método exit.");

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

        // Elimina los registros del extremo.
        String deleteStatement = String.format(
            "DELETE " +
            "FROM active_peers " +
            "WHERE peerIp = '%s'",
            httpServletRequest.getRemoteHost()
        );
        executeStatement(deleteStatement, postgresConnection);
        logger.info(String.format(
            "Se eliminaron los registros del extremo. [IP del extremo = %s]",
            httpServletRequest.getRemoteHost()
        ));

        // Guarda los cambios en la BD, deja el autocommit como estaba,
        // y cierra la conexión.
        postgresConnection.commit();
        postgresConnection.setAutoCommit(true);
        postgresConnection.close();

        return (new JSONObject())
            .put("Respuesta","200 (OK)")
            .put("Descripción","Se eliminaron los registros del extremo.")
            .toString();
        
    }


    /* Private */
    
    private static final Logger logger = LoggerFactory.getLogger(Maestro.class);
    final Dotenv dotenv;
    Connection postgresConnection;
    String postgresUrl;
    String postgresUser;
    String postgresPassword;
    @Autowired private HttpServletRequest httpServletRequest;

    private JSONObject gestionarError(Exception e, String mensaje) {

        // Muestra el mensaje en la consola del servidor.
        e.printStackTrace();
        logger.debug(mensaje);

        // Devuelve el mensaje para el usuario.
        return (new JSONObject())
            .put(
                "Respuesta",
                "500 (Error interno del servidor)"
            ).put(
                "Descripción",
                mensaje
            );

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
         * archivos que se enviaron en el POST deben almacenarse en la BD.
         */
        if (!storedFilesResultSet.next()) {

            // Marca la bandera que indica que hubo modificación en el listado
            // de archivos del extremo.
            updated = true;

            // Agrega todos los archivos a la BD.
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
            // se tendrán que insertar en la BD.
            var fileDescriptionsToInsert = new ArrayList<FileDescriptionRequest>();

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

        // Agrega las descripciones de archivo del extremo a la BD.
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