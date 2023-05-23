package ar.edu.unlu.sdypp.grupo1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 * Métodos de nivel de servicio.
 */
@Service
public class Servicio {

	/**
	 * Lee un archivo y devuelve una lista de strings, cada uno representando una
	 * línea del archivo.
	 * @param arg Ruta al archivo a leer.
	 * @return Lista con las líneas leídas del archivo.
	 * @throws IOException Si falla la lectura del archivo.
	 */
	public ArrayList<String> readFile(String arg) throws IOException {
		ArrayList<String> result = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(arg));
		String line;
		while ((line = br.readLine()) != null) {
			result.add(line);
		}
		br.close();
		return result;
	}

	/**
	 * Retorna una lista de instancias de `Archivo` con los archivos compartidos.
	 * @param arg Ruta a la carpeta compartida.
	 * @return Lista de instancias de `Archivo` con los archivos compartidos.
	 * @throws IllegalArgumentException Si el argumento no se corresponde a un
	 *                                  directorio.
	 * @throws SecurityException Si hay problemas al leer los archivos de la
	 *                           carpeta compartida.
	 */
	public ArrayList<Archivo> getSharedFiles(String arg) throws IllegalArgumentException, SecurityException {
		File folder = new File(arg);
		if (!folder.isDirectory()) {
			throw new IllegalArgumentException("El argumento #2 no apunta a un directorio válido.");
		}
		File[] filesAndFolders = folder.listFiles();
		if (filesAndFolders == null) {
			throw new SecurityException("No se pudo leer los archivos de la carpeta compartida.");
		}
		ArrayList<Archivo> files = new ArrayList<>();
		try {
			for (File fileOrFolder : filesAndFolders) {
				if (fileOrFolder.isFile()) {
					files.add(new Archivo(fileOrFolder));
				}
			}
		} catch (Exception e) {
			throw new SecurityException("Algún archivo de la carpeta compartida no pudo ser leído.");
		}
		return files;
	}

}
