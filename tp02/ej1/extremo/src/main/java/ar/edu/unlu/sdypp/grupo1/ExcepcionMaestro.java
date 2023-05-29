package ar.edu.unlu.sdypp.grupo1;

/**
 * Excepción lanzada cuando no se obtiene respuesta a una petición de ningún nodo
 * maestro, o se obtiene una respuesta errónea.
 */
public class ExcepcionMaestro extends Exception {

    public ExcepcionMaestro(String message) {
        super(message);
    }

}
