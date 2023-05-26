package ar.edu.unlu.sdypp.grupo1;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Archivo a compartir en la red P2P.
 */
public class Archivo {

    /**
     * Nombre del archivo.
     */
    private String name;

    /**
     * Tamaño del archivo (en bytes).
     */
    private long size;

    /**
     * Hash MD5 del archivo.
     */
    private String hash;

    public Archivo(File file) throws FileNotFoundException, Exception {
        this.name = file.getName();
        this.size = file.getTotalSpace();
        this.hash = Utilidades.md5sum(file);
    }

    public String getName() {
        return this.name;
    }

    public long getSize() {
        return this.size;
    }

    public String getHash() {
        return this.hash;
    }

}
