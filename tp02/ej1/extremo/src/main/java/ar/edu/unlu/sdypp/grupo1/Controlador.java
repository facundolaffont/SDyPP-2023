package ar.edu.unlu.sdypp.grupo1;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
