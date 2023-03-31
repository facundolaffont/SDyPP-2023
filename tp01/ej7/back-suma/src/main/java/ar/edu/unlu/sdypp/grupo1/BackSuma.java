package ar.edu.unlu.sdypp.grupo1;

import java.math.BigDecimal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BackSuma {

    /* Público */

    /**
     * Recibe un JSON por POST, y suma los números, devolviendo un JSON con el resultado,
     * o con un mensaje de error, si lo hubo.
     * 
     * Precondiciones: asume que la primera clave es correcta: la clave "tarea" y
     * el valor "suma".
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
        if (objetoJSON.length() != 2) {
            return new JSONObject()
                .put("Error", "JSON mal formado.")
                .toString();
        }
        if (!objetoJSON.has("sumandos")) {
            return new JSONObject()
                .put("Error", "JSON mal formado.")
                .toString();
        }

        // Verificar que todos los sumandos sean numéricos.
        // Si algún elemento no es numérico, notifica del error al cliente.
        // Si todos los elementos son numéricos, realiza la suma.
        JSONArray arrayJSON = objetoJSON.getJSONArray("sumandos");
        BigDecimal suma = new BigDecimal(0.0);
        for (Object elemento : arrayJSON) {
            if(elemento instanceof Integer)
                suma = suma.add(
                    BigDecimal.valueOf(
                        (Integer) elemento
                    )
                );
            else return new JSONObject()
                    .put("Error", "JSON mal formado.")
                    .toString();
        }

        System.out.println("\n* Petición de suma: " + objetoJSON.toString());

        return new JSONObject()
            .put("Respuesta", String.valueOf(suma))
            .toString();
    }

    public static void main( String[] args )
    { SpringApplication.run(BackSuma.class, args); }


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
