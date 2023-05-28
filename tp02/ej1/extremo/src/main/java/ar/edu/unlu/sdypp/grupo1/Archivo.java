package ar.edu.unlu.sdypp.grupo1;

import java.io.File;

/**
 * Archivo a compartir en la red P2P.
 */
public class Archivo {

    /**
     * Nombre del archivo.
     */
    private final String name;

    /**
     * Tamaño del archivo (en bytes).
     */
    private final long sizeInBytes;

    public Archivo(File file) throws SecurityException {
        this.name = file.getName();
        this.sizeInBytes = file.length();
    }

    public String getName() {
        return this.name;
    }

    public long getSizeInBytes() {
        return this.sizeInBytes;
    }

}
