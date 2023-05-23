package ar.edu.unlu.sdypp.grupo1;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** @TODO Documentar */

@Component
public class Inicializador implements CommandLineRunner {

	private final Extremo extremo;

	private final Servicio servicio;

	@Autowired
	public Inicializador(Extremo extremo, Servicio servicio) {
		this.extremo = extremo;
		this.servicio = servicio;
	}

	@Override
	public void run(String... args) {
		if (args.length < 2) {
			System.err.println("El programa requiere dos parámetros. Visite la ayuda para más información.");
			System.exit(1);
		}
		try {
			this.extremo.setMastersIPs(this.servicio.readFile(args[0]));
		} catch (Exception e) {
			System.err.println("No se pudo cargar la lista de direcciones IP de los nodos maestros.");
			System.exit(1);
		}
		try {
			this.extremo.setSharedFiles(this.servicio.getSharedFiles(args[1]));
		} catch (Exception e) {
			System.err.println("No se pudo iniciar la carpeta compartida.");
			System.exit(1);
		}
		JSONObject response = this.extremo.makeRequest("inform");
		System.out.println("data = " + response.opt("data"));
		System.out.println("error = " + response.opt("error"));
		System.out.println("FIN");
		/** @TODO Continuar con el inform... es un POST, pasar archivos... */
	}

}
