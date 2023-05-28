package ar.edu.unlu.sdypp.grupo1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 * Métodos de nivel de servicio relacionados con ficheros locales.
 */
@Service
public class ServicioArchivos {

    /**
     * Lee un fichero y devuelve una lista de strings, cada uno representando una
     * línea leída del mismo.
     * @param arg Ruta del fichero a leer.
     * @return Lista con las líneas leídas.
     * @throws IllegalArgumentException Si el argumento no se corresponde a un
     *                                  fichero.
     * @throws IOException Si falla la lectura.
     */
    public ArrayList<String> readFile(String arg) throws IllegalArgumentException,
                                                         IOException {
        FileReader fr;
        try {
            fr = new FileReader(arg);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("El argumento #1 no apunta a un fichero válido.");
        }
        ArrayList<String> result = new ArrayList<>();
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        br.close();
        return result;
    }

    /**
     * Retorna una lista de instancias de `Archivo` con los archivos compartidos.
     * @param arg Ruta del directorio compartido.
     * @return Lista con los archivos compartidos.
     * @throws IllegalArgumentException Si el argumento no se corresponde a un
     *                                  directorio.
     * @throws SecurityException Si se impide la lectura del directorio por
     *                           cuestiones de seguridad.
     * @throws IOException Si falla la lectura.
     */
    public ArrayList<Archivo> getSharedFiles(String arg) throws IllegalArgumentException,
                                                                SecurityException,
                                                                IOException {
        File folder;
        try {
            folder = new File(arg);
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("El argumento #2 no apunta a un directorio válido.");
        }
        File[] filesAndFolders = folder.listFiles();
        if (filesAndFolders == null) {
            throw new IOException("No se pudo leer los archivos del directorio compartido.");
        }
        ArrayList<Archivo> files = new ArrayList<>();
        for (File fileOrFolder : filesAndFolders) {
            if (fileOrFolder.isFile() && !fileOrFolder.isHidden()) {
                files.add(new Archivo(fileOrFolder));
            }
        }
        return files;
    }

}
