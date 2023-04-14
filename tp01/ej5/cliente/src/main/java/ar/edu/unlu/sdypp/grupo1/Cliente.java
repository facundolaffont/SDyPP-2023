package ar.edu.unlu.sdypp.grupo1;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class Cliente {

    public static void main(String[] args) {
        // Si no se pasó como argumento la URL del endpoint,
        // notifica al cliente y termina el programa.
        if(args.length != 1)
            System.out.println(
                "Al ejecutar la aplicación debe pasar como argumento la URL del endpoint, sin esquema."
            );
        else {
            JSONObject json = getJSON("http://" + args[0]);

            System.out.println("\n" +
                (
                    json == null
                    ? new JSONObject("{}")
                        .put("Error", "Hubo un problema al obtener los datos del clima.")
                        .toString()
                    : json.toString()
                )
            );
        }
    }


    /* Privado */

    /**
	 * Envía una petición GET a {@param url}, esperando obtener un JSON,
     * y lo devuelve, si no hubo problemas, o devuelve {@code null}, en
     * caso contrario.
	 * 
	 * @param urlString - Endpoint de la API.
	 * @return Un JSON, o {@code null}, si hubo algún problema para obtenerlo.
	 */
	private static JSONObject getJSON(String urlString) {
        
        // Verifica si la URL está mal formada. Si es así, devuelve null;
        URL url;
        try { url = new URL(urlString); }
        catch (MalformedURLException e) { return null; }

        // Establece la conexión, y devuelve un JSON de error, si lo hubo.
        HttpURLConnection conexionHTTP;
        try { conexionHTTP = (HttpURLConnection) url.openConnection(); }
        catch (IOException e) { return null; }

        // Establece el método de envío, y envía un JSON de error, si lo hubo.
        try { conexionHTTP.setRequestMethod("GET"); }
        catch (ProtocolException e) { return null; }
        conexionHTTP.setRequestProperty("Content-Type", "application/json");

        // Obtiene el código de respuesta, y devuelve null, si hubo un error.
        int codigoRespuesta = 0;
        try { codigoRespuesta = conexionHTTP.getResponseCode(); }
        catch (IOException e) { return null; }
        System.out.println("\nCódigo de respuesta del servidor: " + codigoRespuesta);

        // Lee la respuesta, y devuelve un null, si hubo algún problema.
        StringBuilder respuesta = new StringBuilder();
        try (BufferedReader bufferReader = new BufferedReader(
            new InputStreamReader(conexionHTTP.getInputStream())
        )) {
            String lineaEntrante;
            while ((lineaEntrante = bufferReader.readLine()) != null) { respuesta.append(lineaEntrante); }
            bufferReader.close();
        } catch (IOException e) { return null; }

        // Detecta si la respuesta obtenida es un JSON.
        // Si es así, devuelve null.
        String jsonString = respuesta.toString();
        //JsonElement json = JsonParser.parseString(respuesta.toString());

        // Identifica si la respuesta está en formato JSON.
        // Si no lo está, devuelve un JSON con la notificación para el usuario.
        JSONObject jsonObject = new JSONObject();
        try {
            // Create a JSONObject from the JSON string
            jsonObject = new JSONObject(jsonString);
        } catch (Exception e) {
            return jsonObject.put("Error", "La respuesta no está en formato JSON.");
        }

        // Devuelve el JSON.
        return jsonObject;
	}

}