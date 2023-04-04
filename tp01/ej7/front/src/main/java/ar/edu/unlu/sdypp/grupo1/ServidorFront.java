package ar.edu.unlu.sdypp.grupo1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ServidorFront {

    @PostMapping(
        value="/ejecutar-tarea-remota",
        headers="Content-Type=application/json"
    )
    public String ejecutarTareaRemota(@RequestBody String json) {
        
        // Determina si el texto recibido en el body es o no un JSON.
        // Si no es un JSON, devuelve un JSON con el mensaje de error.
        JSONObject objetoJSON = new JSONObject();
        try { objetoJSON = new JSONObject(json); }
        catch (JSONException e) { return _gestionarError(e, "JSON mal formado.").toString(); }

        // Determina si el JSON tiene una clave 'tarea'.
        // Si no la tiene, devuelve un JSON con el mensaje de error.
        if (!objetoJSON.has("tarea")) {
            return new JSONObject()
                .put(
                "Error",
                "No se encuentra la clave 'tarea'."
            ).toString();
        }

        // Determina si el valor de la clave 'tarea' es una cadena de caracteres.
        // Si no lo es, devuelve un JSON con el mensaje de error.
        String tarea = new String();
        try { tarea = objetoJSON.getString("tarea"); }
        catch (JSONException e) { return _gestionarError(e, "El valor de la clave 'tarea' debe ser una cadena de texto.").toString(); }

        // Determina si la tarea es soportada por el servidor.
        // Si no lo está, devuelve un JSON con el mensaje de error.
        String puerto = new String();
        switch (tarea) {
            case "calculo-pi":
                System.out.println("\n* Petición: cálculo de PI.");
                puerto = "9000";
            break;
            case "suma":
                System.out.println("\n* Petición: suma.");
                puerto = "9001";
            break;
            default:

                return new JSONObject().put(
                    "Error",
                    "La tarea no está soportada por el servidor."
                ).toString();
        }

        // Levanta el docker correspondiente, en un nuevo bash.
        // ¡IMPORTANTE!: hay que levantar el servidor con permisos de root.
        String[] comando = new String[] {"/bin/sh", "-c",
            "docker run" +
            " -d" +
            " -p " + puerto + ":" + puerto +
            " --name " + tarea +
            " " + tarea};
        Process proceso;
        try { proceso = new ProcessBuilder(comando).start(); }
        catch (IOException e) { return _gestionarError(e, "Error del servidor.").toString(); }

        // Si hubo error al ejecutar el comando, lo muestra en el bash,
        // y notifica al cliente.
        InputStream inputStream = proceso.getErrorStream();
        int byteLeido;
        boolean huboError = false;
        try {
            while((byteLeido = inputStream.read()) > -1) {
                huboError = true;
                System.out.print((char) byteLeido);
            }
        } catch (IOException e) { 
            if (huboError) { return _gestionarError(e, "Error del servidor.").toString(); }
        }

        // Envía la orden de tarea al nuevo contenedor y obtiene los resultados.
        return _postParaJSON("http://localhost:" + puerto + "/", objetoJSON).toString();
    }

    public static void main(String[] args)
    { SpringApplication.run(ServidorFront.class, args); }


    /* Privado */

    private JSONObject _gestionarError(Exception e, String mensaje) {
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
	 * @param url - Endpoint de la API.
     * @param json - JSON que se envía en el cuerpo de la petición.
	 * @return Un JSON, cuyo primer campo será el resultado, o será "Error" si hubo un problema.
	 */
	private JSONObject _postParaJSON(String url, JSONObject json) {
        
        // Verifica si la URL está mal formada. Si es así, devuelve un JSON de error, si lo hubo.
        URL _url;
        try { _url = new URL(url); }
        catch (MalformedURLException e) { return _gestionarError(e, "Error del servidor."); }

        // Establece la conexión, y devuelve un JSON de error, si lo hubo.
        HttpURLConnection conexionHTTP;
        try { conexionHTTP = (HttpURLConnection) _url.openConnection(); }
        catch (IOException e) { return _gestionarError(e, "Error del servidor."); }

        // Establece el método de envío, y envía un JSON de error, si lo hubo.
        try { conexionHTTP.setRequestMethod("POST"); }
        catch (ProtocolException e) { return _gestionarError(e, "Error del servidor."); }
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
        catch (IOException e) { return _gestionarError(e, "Error del servidor."); }

        // Obtiene el código de respuesta, y devuelve un JSON de error, si lo hubo.
        int codigoRespuesta = 0;
        try { codigoRespuesta = conexionHTTP.getResponseCode(); }
        catch (IOException e) { return _gestionarError(e, "Error del servidor."); }
        System.out.println("* Código de respuesta del servidor: " + codigoRespuesta);

        // Lee la respuesta, y devuelve un JSON de error, si lo hubo.
        StringBuilder respuesta = new StringBuilder();
        try (BufferedReader bufferReader = new BufferedReader(
            new InputStreamReader(conexionHTTP.getInputStream())
        )) {
            String lineaEntrante;
            while ((lineaEntrante = bufferReader.readLine()) != null) { respuesta.append(lineaEntrante); }
            bufferReader.close();
        } catch (IOException e) { return _gestionarError(e, "Error del servidor."); }

        // Devuelve el JSON, ya sea con error, o con la respuesta del servidor.
        return new JSONObject(respuesta.toString());
	}

}
