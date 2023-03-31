package ar.edu.unlu.sdypp.grupo1;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BackPI {

    /* Público */

    /**
     * Recibe un JSON por POST para pedir el cálculo de PI, devolviendo un JSON con
     * el resultado, o con un mensaje de error, si lo hubo.
     * 
     * Precondiciones: asume que la primera clave es correcta: la clave "tarea" y
     * el valor "calculo-pi".
     * 
     * @param json - El JSON recibido.
     * @return - Un JSON con el resultado, o con un mensaje de error.
     */
    @PostMapping(
        value="/",
        headers="Content-Type=application/json"
    )
    public String ejecutarTareaRemota(@RequestBody String json) {
        // Determina si el texto recibido en el body es o no un JSON.
        // Si no es un JSON, devuelve un JSON con el mensaje de error.
        JSONObject objetoJSON = new JSONObject();
        try { objetoJSON = new JSONObject(json); }
        catch (JSONException e) { return _gestionarError(e, "JSON mal formado.").toString(); }

        // Determina si el JSON está bien formado.
        // Si no lo está, devuelve un JSON con el mensaje de error.
        if (objetoJSON.length() != 1) {
            return new JSONObject()
                .put("Error", "JSON mal formado.")
                .toString();
        }

        // Calcula PI y lo devuelve en formato JSON.
        return new JSONObject()
            .put("Respuesta", Math.PI)
            .toString();
    }

    public static void main( String[] args )
    { SpringApplication.run(BackPI.class, args); }


    /* Privado */

    private JSONObject _gestionarError(Exception e, String mensaje) {
        // Muestra el mensaje en la consola del servidor.
        e.printStackTrace();

        // Devuelve el mensaje para el usuario.
        JSONObject objetoJSON = new JSONObject();
        return objetoJSON.put(
            "Error",
            mensaje
        );
    }
}
