package ar.edu.unlu.sdypp.grupo1;

import java.io.IOException;

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
public class Servidor {

    /* Público */

    @PostMapping(
        value="/ejecutar-tarea",
        headers="Content-Type=application/json"
    )
    public String ejecutarTareaRemota(@RequestBody String json) {
        try { JSONObject objetoJSON = new JSONObject(json); }
        catch (JSONException e) { return _insertarStringEnJSON("{}", "Error", "El elemento recibido no es un JSON!"); }

        /*
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("tcp://localhost:2376")
            //.withDockerTlsVerify(false)
            //.withDockerCertPath("/home/user/.docker")
            //.withRegistryUsername(registryUser)
            //.withRegistryPassword(registryPass)
            //.withRegistryEmail(registryMail)
            //.withRegistryUrl(registryUrl)
            .build();

        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(dockerClientConfig.getDockerHost())
            .sslConfig(dockerClientConfig.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();

        Request request = Request.builder()
            .method(Request.Method.GET)
            .path("/_ping")
            .build();

        try (Response response = httpClient.execute(request)) {
            assertThat(response.getStatusCode(), equalTo(200));
            assertThat(IOUtils.toString(response.getBody()), equalTo("OK"));
        }

        DockerClient dockerClient = DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);

        dockerClient.pingCmd().exec();
        */

        String[] argumentos = new String[] {"/bin/bash", "-c", "code"};
        try { Process proceso = new ProcessBuilder(argumentos).start(); }
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
        SpringApplication.run(Servidor.class, args);
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
