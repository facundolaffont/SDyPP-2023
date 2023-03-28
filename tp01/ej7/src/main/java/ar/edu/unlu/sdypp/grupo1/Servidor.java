package ar.edu.unlu.sdypp.grupo1;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Servidor
{
    public static void main( String[] args )
    {
        SpringApplication.run(Servidor.class, args);
        
    }

    @PostMapping(
        value="/ejecutar-tarea",
        headers="Content-Type=application/json"
    )
    public String ejecutarTareaRemota(@RequestBody String json) {
        try { JSONObject objetoJSON = new JSONObject(json); }
        catch (JSONException e) { return _insertarStringEnJSON("{}", "Error", "El elemento recibido no es un JSON!"); }

        return _insertarStringEnJSON("{}", "Mensaje", "Hola Mundo!");
    }

    @GetMapping("ayuda")
    public String mostrarAyuda() {
        return "";
    }


    /* Privado */

    private String _insertarStringEnJSON(String json, String clave, String valor) {
        String lugarDeComa = json.charAt(1) == '}' ? "" : ",";
		return "{"
			+ "\"" + clave + "\":"
			+ "\"" + valor + "\"" + lugarDeComa
			+ json.substring(1)
		;
	}
}
