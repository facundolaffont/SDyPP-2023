package ar.edu.unlu.sdypp.grupo1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Clase con métodos estáticos de utilidad para las clases principales.
 */
public class Utilidades {

	/**
	 * Obtiene el hash MD5 de un archivo.
	 * @param file Archivo del cual se desea computar el hash MD5.
	 * @return Hash MD5 del archivo.
	 * @throws FileNotFoundException Si el archivo no existe, es un directorio o
	 *                               no se puede leer.
	 * @throws Exception Si ocurre alguna otra excepción al computar el hash.
	 */
	public static String md5sum(File file) throws FileNotFoundException, Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		FileInputStream fis = new FileInputStream(file);
		DigestInputStream dis = new DigestInputStream(fis, md);
		while (dis.read() != -1);
		StringBuilder sb = new StringBuilder();
		for (byte b : md.digest()) {
			sb.append(String.format("%02x", b));
		}
		dis.close();
		return sb.toString();
	}

}
