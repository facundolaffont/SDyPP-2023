package ar.edu.unlu.sdypp.grupo1;

import java.io.IOException;
import java.io.InputStream;
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
public class BackPI {

    /* Público */

    @PostMapping(
        value="/ejecutar-tarea",
        headers="Content-Type=application/json"
    )
    public String ejecutarTareaRemota(@RequestBody String json) {
        try { JSONObject objetoJSON = new JSONObject(json); }
        catch (JSONException e) { return _insertarStringEnJSON("{}", "Error", "El elemento recibido no es un JSON!"); }

        // ¡IMPORTANTE!: hay que levantar el servidor con permisos de root.
        String[] argumentos = new String[] {"/bin/bash", "-c", "docker run -d -p 9000:80 tp1-ej5-c1"};
        try {
            Process proceso = new ProcessBuilder(argumentos).start();
            InputStream inputStream = proceso.getErrorStream();
            int byteLeido;
            while((byteLeido = inputStream.read()) > -1) {
                System.out.print((char) byteLeido);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return _insertarStringEnJSON("{}", "Mensaje", "Hola Mundo!");
    }

    @GetMapping("ayuda")
    public String mostrarAyuda() {
        return "";
    }

    public static void main( String[] args )
    {
        SpringApplication.run(BackPI.class, args);
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
