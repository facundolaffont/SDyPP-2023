package ar.edu.unlu.sdypp.grupo1;

import java.io.File;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador del nodo extremo.
 */
@Controller
public class Controlador {

    @Autowired
    private Extremo extreme;

    @Autowired
    private ResourceLoader rl;

    /**
     * Devuelve un frontend donde el usuario es capaz de realizar búsquedas,
     * seleccionar archivos a descargar y desconectarse del programa.
     * @return HTML del frontend.
     * @throws Exception Si falla la lectura de la vista.
     */
    @GetMapping("/")
    @ResponseBody
    public String index() throws Exception {
        Resource resource = this.rl.getResource("classpath:static/index.html");
        byte[] fileBytes = resource.getInputStream().readAllBytes();
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    /**
     * Realiza una búsqueda de archivos en la red P2P.
     * @param search Patrón de búsqueda.
     * @return String prefijado con uno de los siguientes valores:
     *         - "data=":  Respuesta JSON de los nodos maestros.
     *         - "error=": Error ocurrido en la búsqueda.
     */
    @GetMapping("/query")
    @ResponseBody
    public String query(@RequestParam("search") String search) {
        try {
            String data = this.extreme.query(search);
            return "data=" + data;
        } catch (ExcepcionMaestro e) {
            return "error=" + e.getMessage();
        }
    }

    /**
     * Transfiere un archivo compartido solicitado por otro nodo extremo.
     * @param search Nombre del archivo solicitado.
     * @return Archivo binario solicitado.
     * @throws Exception Si se produce algún error al leer el archivo.
     */
    @GetMapping("/download")
    public ResponseEntity<FileSystemResource> download(@RequestParam("name") String name) throws Exception {
        File file = this.extreme.getFile(name);
        if (!file.exists() || !file.isFile() || file.isHidden()) {
            // Error 404.
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", name);
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    /**
     * Da aviso a los nodos maestros que se desconecta de la red, y luego termina
     * el programa.
     */
    @GetMapping("/disconnect")
    @ResponseBody
    public void disconnect() {
        this.extreme.disconnect();
        System.exit(0);
    }

}
