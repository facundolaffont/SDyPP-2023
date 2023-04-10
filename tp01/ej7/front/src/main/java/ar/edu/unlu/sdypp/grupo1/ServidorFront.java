package ar.edu.unlu.sdypp.grupo1;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.core.DefaultDockerClientConfig;

@SpringBootApplication
@RestController
public class ServidorFront {

    public ServidorFront() {

        dockerClientConfig = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
        
        dockerHttpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(dockerClientConfig.getDockerHost())
            .sslConfig(dockerClientConfig.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();
        
        dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);

        dockerClient.stopContainerCmd("suma").exec();
        dockerClient.stopContainerCmd("calculo-pi").exec();
        dockerClient.removeContainerCmd("suma").exec();
        dockerClient.removeContainerCmd("calculo-pi").exec();
    }

    @PostMapping(
        value="/ejecutar-tarea-remota",
        headers="Content-Type=application/json"
    )
    public String ejecutarTareaRemota(@RequestBody String json) {
        
        // Determina si el texto recibido en el body es o no un JSON.
        // Si no es un JSON, devuelve un JSON con el mensaje de error.
        JSONObject objetoJSON = new JSONObject();
        try { objetoJSON = new JSONObject(json); }
        catch (JSONException e) { return gestionarError(e, "JSON mal formado.").toString(); }

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
        catch (JSONException e) { return gestionarError(e, "El valor de la clave 'tarea' debe ser una cadena de texto.").toString(); }

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
        
        // Se crean las configuraciones para el host del contenedor.
        HostConfig hostConfig = new HostConfig()
            .withAutoRemove(true)
            .withNetworkMode("tp1-ej7_red-contenedores")
            .withPortBindings(
                new Ports(
                    new ExposedPort(Integer.parseInt(puerto)),
                    new Binding("0.0.0.0", puerto)
                )
            );
        
        // Se crea el container (pero todavía no se corre).
        CreateContainerResponse container = dockerClient
            .createContainerCmd("tp1-ej7-" + tarea)
            .withName(tarea)
            .withHostConfig(hostConfig)
            .exec();
        
        // Se corre el container.
        dockerClient
            .startContainerCmd(container.getId())
            .exec();
        
        CountDownLatch latch = new CountDownLatch(1);
        try { latch.await(5, TimeUnit.SECONDS); }
        catch (InterruptedException e) {}

        // Envía la petición de realización de la tarea al contenedor remoto, y obtiene la respuesta (
        // se hace uso de los nombres de contenedores como nombres de host que Docker habilita para todos los
        // contenedores que pertenezcan a una misma red).
        String jsonRespuesta = postParaJSON("http://" + tarea + ":" + puerto + "/", objetoJSON).toString();

        // Para el container, y se borra por la configuración AutoRemove.
        dockerClient
            .stopContainerCmd(container.getId().toString())
            .exec();

        // Devuelve la respuesta que generó la tarea llamada, en formato JSON, como String.
        return jsonRespuesta;
    }

    public static void main(String[] args)
    { SpringApplication.run(ServidorFront.class, args); }


    /* Privado */

    DockerClientConfig dockerClientConfig;
    DockerHttpClient dockerHttpClient;
    DockerClient dockerClient;

    private JSONObject gestionarError(Exception e, String mensaje) {
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

}
