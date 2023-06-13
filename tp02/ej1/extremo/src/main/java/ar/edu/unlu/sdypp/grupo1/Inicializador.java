package ar.edu.unlu.sdypp.grupo1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente responsable de inicializar el nodo extremo.
 */
@Component
public class Inicializador implements CommandLineRunner {

    /**
     * Clase principal (posee las direcciones IP de los nodos maestros y los
     * archivos compartidos).
     */
    @Autowired
    private Extremo extreme;

    /**
     * Servicio de archivos.
     */
    @Autowired
    private ServicioArchivos fileService;

    @Override
    public void run(String... args) throws Exception {
        this.extreme.setSharedFolder(args[1]);
        try {
            // Levanta las direcciones IP de los nodos maestros.
            this.extreme.setMastersIPs(this.fileService.readFile(args[0]));
            // Carga los archivos compartidos.
            this.extreme.setSharedFiles(this.fileService.getSharedFiles(args[1]));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("No se pudo iniciar el nodo. Verifique los permisos de la carpeta compartida y del"
                    + " fichero de direcciones de los maestros.");
            System.exit(1);
        }
        // Informa la existencia del nodo a los maestros.
        System.out.println("Informándose a los nodos maestros...");
        if (!this.extreme.inform()) {
            System.err.println("No se pudo comunicar con ningún nodo maestro.");
            System.exit(1);
        }
        // Intenta abrir el puerto 8080 al exterior.
        try {
            System.out.println("Intentando abrir el puerto 8080 al exterior...");
            UPnP.openPort();
        } catch (Exception e) {
            // No hace nada. Se utiliza el programa sin abrir el puerto externo.
        }
        System.out.println("Aplicación iniciada.");
        // Abre un bucle infinito para reportarse cada cierto tiempo.
        while (true) {
            Thread.sleep(Extremo.INFORM_INTERVAL);
            this.extreme.inform();
        }
    }

}
