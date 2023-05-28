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
    private final Extremo extreme;

    /**
     * Servicio de archivos.
     */
    private final ServicioArchivos fileService;

    /**
     * Servicio de red.
     */
    private final ServicioRed networkService;

    @Autowired
    public Inicializador(Extremo extreme, ServicioArchivos fileService, ServicioRed networkService) {
        this.extreme = extreme;
        this.fileService = fileService;
        this.networkService = networkService;
    }

    @Override
    public void run(String... args) {
        // Valida los argumentos.
        if (args.length < 2) {
            System.err.println("El programa requiere dos parámetros. Visite la ayuda para más información.");
            System.exit(1);
        }
        // Levanta las direcciones IP de los nodos maestros.
        try {
            this.extreme.setMastersIPs(this.fileService.readFile(args[0]));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la lista de direcciones IP de los nodos maestros.");
            System.exit(1);
        }
        // Carga los archivos compartidos.
        try {
            this.extreme.setSharedFiles(this.fileService.getSharedFiles(args[1]));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("No se pudo iniciar la carpeta compartida.");
            System.exit(1);
        }
        // Informa la existencia del nodo a los maestros.
        boolean response = this.extreme.inform(this.networkService);
        /** @TODO Continuar con el inform... es un POST, pasar archivos... */
    }

}
