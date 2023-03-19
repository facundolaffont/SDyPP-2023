package ar.edu.unlu.sdypp.tp1.ej5;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

@SpringBootApplication
@RestController
public class ServerClima {

	/* Miembros públicos */

	public static void main(String[] args) {
		SpringApplication.run(ServerClima.class, args);
	}

	public ServerClima() {
		// Construye el manejador del archivo de la base de datos de las ubicaciones.
		bdGeoLite = new File("src/main/resources/geolite2-city-bd/GeoLite2-City.mmdb");
	}

	/**
	 * Devuelve el clima, o notifica si hubo algún problema.
	 * 
	 * Se ejecuta en respuesta a un GET HTTP a /clima.
	 * 
	 * @return Un JSON con los datos del clima, o una descripción de un eventual problema.
	 */
	@GetMapping("/clima")
	public String getClima() {
		// Intenta obtener la IP pública del servidor, y si no puede, notifica.
		String ip = _getJSON("https://api.ipify.org?format=json");
		if (ip == null) return "Error: no se pudo determinar la IP pública del servidor.";
		ip = (
			(IpPublica) _getObjetoJSON(ip, IpPublica.class)
		).getIp();

		// Intenta obtener el pais del servidor, y si no puede, notifica.
		String pais = _getPais(ip, bdGeoLite);
		if (pais == null) return "Error: no se pudo determinar la localización del servidor.";

		// Intenta obtener la ciudad del servidor, y si no puede, notifica.
		String ciudad = _getCiudad(ip, bdGeoLite);
		if (ciudad == null) return "Error: no se pudo determinar la localización del servidor.";

		// Intenta obtener la latitud del servidor, y si no puede, notifica.
		String latitud = _getLatitud(ip, bdGeoLite);
		if (latitud == null) return "Error: no se pudo determinar la localización del servidor.";

		// Intenta obtener la longitud del servidor, y si no puede, notifica.
		String longitud = _getLongitud(ip, bdGeoLite);
		if (longitud == null) return "Error: no se pudo determinar la localización del servidor.";

		// Intenta obtener los datos del clima, y si no puede, notifica.
		String respuesta = _getJSON(
			"https://api.open-meteo.com/v1/forecast?latitude=" + latitud
			+ "&longitude=" + longitud
			+ "&current_weather=true"
		);
		if (respuesta == null) return "Error: no se pudo recuperar el clima.";

		// Inserta, al principio del JSON, la IP, la ciudad y el país en donde está alojado el servidor.
		respuesta = _insertarStringEnJSON(respuesta, "country", pais);
		respuesta = _insertarStringEnJSON(respuesta, "city", ciudad);
		respuesta = _insertarStringEnJSON(respuesta, "ip", ip);

		return respuesta;
	}


	/* Miembros privados */

	File bdGeoLite; // Manejador del archivo de ubicaciones.

	/**
	 * Devuelve un JSON como resultado de una petición
	 * que se realiza al endpoint {@code url}.
	 * 
	 * @param url - Endpoint de la API.
	 * @return Un JSON, o {@code null}.
	 */
	private String _getJSON(String url) {
		try {
			URL _url = new URL(url);
			HttpURLConnection conexionHTTP = (HttpURLConnection) _url.openConnection();
			conexionHTTP.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(
				new InputStreamReader(
					conexionHTTP.getInputStream()
				)
			);

			String lineaEntrante;
			StringBuilder respuesta = new StringBuilder();
			while ((lineaEntrante = in.readLine()) != null) { respuesta.append(lineaEntrante); }
			in.close();

			return respuesta.toString();
		}
		catch (Exception e) { e.printStackTrace(); }

		return null;
	}

	/**
	 * Clase que se utiliza para mapear el JSON de la API de ipify.
	 */
	private static class IpPublica {
		//public IpPublica() {}
		
		//public void setIp(String ip) { this.ip = ip; }
		public String getIp() { return ip; }

		private String ip;
	}

	/**
	 * Devuelve un JSON mapeado a un {@code Object}, o {@code null} si no fue posible el mapeo.
	 * 
	 * @param json - El JSON que se quiere mapear.
	 * @param clase - La clase que se va a utilizar para mapear el objeto.
	 * @return Un objeto JSON mapeado o {@code null}.
	 */
	private Object _getObjetoJSON(String json, Class clase) {
		Object objeto = null;

		// Mapea el JSON a un objeto IpPublica.
		try {
			objeto = (new ObjectMapper())
				.readValue(json, clase);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return objeto;
	}

	private String _insertarStringEnJSON(String json, String clave, String valor) {
		return "{"
			+ "\"" + clave + "\":"
			+ "\"" + valor + "\","
			+ json.substring(1)
		;
	}

	/**
	 * Devuelve la latitud de una IPv4.
	 * 
	 * @param ip - La dirección IPv4 de la cual se quiere obtener la latitud.
	 * @param archivoGeoLite - El manejador al archivo GeoLite.
	 * @return La latitud, o {@code null}, si no se pudo obtener.
	 */
	private String _getLatitud(String ip, File archivoGeoLite) {
		String latitud = null;

		// Obtiene la latitud del ip, en la base GeoLite.
		String[] componentesIp = ip.split("\\.");
		byte[] bytesIp = new byte[4];
		for (int indice = 0; indice < 4; indice++) {
			bytesIp[indice] = (byte) Integer.parseInt(componentesIp[indice]);
		}
		try (DatabaseReader databaseReader = new DatabaseReader.Builder(archivoGeoLite).build()) {
			InetAddress inetAddress = InetAddress.getByAddress(bytesIp);
			CityResponse cityResponse = databaseReader.city(inetAddress);
			latitud = cityResponse.getLocation().getLatitude().toString();
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (GeoIp2Exception f) { f.printStackTrace(); }
		
		return latitud;
	}

	/**
	 * Devuelve la longitud de una IPv4.
	 * 
	 * @param ip - La dirección IPv4 de la cual se quiere obtener la longitud.
	 * @param archivoGeoLite - El manejador al archivo GeoLite.
	 * @return La longitud, o {@code null}, si no se pudo obtener.
	 */
	private String _getLongitud(String ip, File archivoGeoLite) {
		String longitud = null;

		// Obtiene la longitud del ip, en la base GeoLite.
		String[] componentesIp = ip.split("\\.");
		byte[] bytesIp = new byte[4];
		for (int indice = 0; indice < 4; indice++) {
			bytesIp[indice] = (byte) Integer.parseInt(componentesIp[indice]);
		}
		try (DatabaseReader databaseReader = new DatabaseReader.Builder(archivoGeoLite).build()) {
			InetAddress inetAddress = InetAddress.getByAddress(bytesIp);
			CityResponse cityResponse = databaseReader.city(inetAddress);
			longitud = cityResponse.getLocation().getLongitude().toString();
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (GeoIp2Exception f) { f.printStackTrace(); }
		
		return longitud;
	}

	/**
	 * Devuelve la ciudad de una IPv4.
	 * 
	 * @param ip - La dirección IPv4 de la cual se quiere obtener la ciudad.
	 * @param archivoGeoLite - El manejador al archivo GeoLite.
	 * @return La ciudad, o {@code null}, si no se pudo obtener.
	 */
	private String _getCiudad(String ip, File archivoGeoLite) {
		String ciudad = null;

		// Obtiene la ciudad del ip, en la base GeoLite.
		String[] componentesIp = ip.split("\\.");
		byte[] bytesIp = new byte[4];
		for (int indice = 0; indice < 4; indice++) {
			bytesIp[indice] = (byte) Integer.parseInt(componentesIp[indice]);
		}
		try (DatabaseReader databaseReader = new DatabaseReader.Builder(archivoGeoLite).build()) {
			InetAddress inetAddress = InetAddress.getByAddress(bytesIp);
			CityResponse cityResponse = databaseReader.city(inetAddress);
			ciudad = cityResponse.getCity().getName().toString();
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (GeoIp2Exception f) { f.printStackTrace(); }
		
		return ciudad;
	}

	/**
	 * Devuelve el país de una IPv4.
	 * 
	 * @param ip - La dirección IPv4 de la cual se quiere obtener el país.
	 * @param archivoGeoLite - El manejador al archivo GeoLite.
	 * @return El país, o {@code null}, si no se pudo obtener.
	 */
	private String _getPais(String ip, File archivoGeoLite) {
		String pais = null;

		// Obtiene el país del ip, en la base GeoLite.
		String[] componentesIp = ip.split("\\.");
		byte[] bytesIp = new byte[4];
		for (int indice = 0; indice < 4; indice++) {
			bytesIp[indice] = (byte) Integer.parseInt(componentesIp[indice]);
		}
		try (DatabaseReader databaseReader = new DatabaseReader.Builder(archivoGeoLite).build()) {
			InetAddress inetAddress = InetAddress.getByAddress(bytesIp);
			CityResponse cityResponse = databaseReader.city(inetAddress);
			pais = cityResponse.getCountry().getName().toString();
		}
		catch (IOException e) { e.printStackTrace(); }
		catch (GeoIp2Exception f) { f.printStackTrace(); }
		
		return pais;
	}
}