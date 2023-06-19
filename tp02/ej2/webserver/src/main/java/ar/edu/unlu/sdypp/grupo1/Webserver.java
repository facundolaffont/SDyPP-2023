package ar.edu.unlu.sdypp.grupo1;

import java.io.IOException;
import java.io.OutputStream;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ar.edu.unlu.sdypp.grupo1.requests.FileDescriptionRequest;
import ar.edu.unlu.sdypp.grupo1.requests.InformRequest;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
public class Webserver {

    public static void main(String[] args)
    { SpringApplication.run(Webserver.class, args); }

    public Webserver() {

        // Carga las variables de entorno.
        postgresUrl = System.getenv("POSTGRES_URL");
        postgresUser = System.getenv("POSTGRES_USER");
        postgresPassword = System.getenv("POSTGRES_PASSWORD");
        logger.debug(String.format(
            "POSTGRES_URL=%s\n" +
            "POSTGRES_USER=%s\n" +
            "POSTGRES_PASSWORD=%s",
            postgresUrl,
            postgresUser,
            postgresPassword
        ));

    }

    @PostMapping(
        value="/job",
        headers="Content-Type=application/json"
    )
    public String writeGcs(@RequestBody String data) throws IOException {

        // logger.debug(String.format(
        //     "Se ejecuta el método job. [jobRequest = %s]",
        //     jobRequest.toString()
        // ));

        logger.debug(
            "Se ejecuta el método job."
        );

        // // Genera el mensaje y el código para el caso en el que el
        // // parámetro esté vacío.
        // returningJson = new JSONObject()
        //     .put("Respuesta", "Debe especificar el arreglo 'files'.");
        // statusCode = HttpStatus.BAD_REQUEST;
        

        
        // var returningJson = (new JSONObject())
        //     .put("Respuesta","Sin cambios. Fecha y hora de acceso actualizadas.");
        // var statusCode = HttpStatus.OK;
        
        // // ...

        // return ResponseEntity
        //     .status(statusCode)
        //     .body(
        //         returningJson.toString()
        //     );

        try (OutputStream os = ((WritableResource) this.gcsFile).getOutputStream()) {
            os.write(data.getBytes());
        }
        
        return "file was updated\n";

    }


    /* Private */
    
    private static final Logger logger = LoggerFactory.getLogger(Webserver.class);
    Connection postgresConnection;
    String postgresUrl;
    String postgresUser;
    String postgresPassword;
    @Autowired private HttpServletRequest httpServletRequest;

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
    private ResultSet executeQuery(String query, Connection postgresConnection)
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
    private void executeStatement(String sqlStatement, Connection postgresConnection)
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

    @Value("gs://${gcs-bucket}/my-file.txt")
    private Resource gcsFile;
}