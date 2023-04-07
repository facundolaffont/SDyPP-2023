package ar.edu.unlu.sdypp.grupo1;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.Exception;

public class Cliente {

    public Cliente(String urlEndpoint) {
        // Registra el endpoint de la API a la que se enviarán
        // las tareas.
        endpoint = urlEndpoint;
        
        // Carga las operaciones que el cliente conoce.
        tareasSoportadas = new ArrayList<String>();
        tareasSoportadas.add("suma");
        tareasSoportadas.add("calculo-pi");

        init();
    }

    public static void main(String[] args) {
        // Si no se pasó, como argumento, la URL del endpoint,
        // notifica al cliente y termina el programa.
        if(args.length != 1) {
            System.out.println(
                "SINOPSIS"
                + "\tX <url-endpoint>"
                + "\n\n"

                + "DESCRIPCIÓN"
                + "\tObtiene del usuario la tarea a ejecutar, y envía la petición de"
                + "\tprocesamiento a <url-endpoint>."
            );
        }

        new Cliente(args[0]);
    }


    /* Privado */

    private ArrayList<String> tareasSoportadas;
    private String endpoint;

    private void init() {
        System.out.println("Bienvenido!\n");

        do {
            // Le pide al usuario la tarea.
            String tarea = obtenerTarea();
            
            // Obtiene un arreglo JSON con los parámetros.
            JSONArray parametros = obtenerParametrosEnArregloJSON();
            
            // Construye el requerimiento en formato JSON.
            JSONObject objetoJSON = new JSONObject();
            objetoJSON.put("tarea", tarea);
            objetoJSON.put("parametros", parametros);
            
            // Envía el requerimiento al servidor, y obtiene y muestra
            // la respuesta.
            objetoJSON = postParaJSON(endpoint, objetoJSON);

            // Muestra el resultado.
            StringWriter writer = new StringWriter();
            objetoJSON.write(writer);
            System.out.println(writer.toString());
        } while (true);
    }

    /**
	 * Envía un JSON en un POST al endpoint {@param url}, y en base a la
     * respuesta obtenida (que debe ser un JSON), se retorna tal cual, o
     * se retorna un JSON que contiene un error.
	 * 
	 * @param urlString - Endpoint de la API.
     * @param json - JSON que se envía en el cuerpo de la petición.
	 * @return Un JSON, cuyo primer campo será el resultado, o será "Error" si hubo un problema.
	 */
	private JSONObject postParaJSON(String urlString, JSONObject json) {
        
        // Verifica si la URL está mal formada. Si es así, devuelve un JSON de error, si lo hubo.
        URL url;
        try { url = new URL(urlString); }
        catch (MalformedURLException e) { return gestionarError(e, "Error del servidor."); }

        // Establece la conexión, y devuelve un JSON de error, si lo hubo.
        HttpURLConnection conexionHTTP;
        try { conexionHTTP = (HttpURLConnection) url.openConnection(); }
        catch (IOException e) { return gestionarError(e, "Error del servidor."); }

        // Establece el método de envío, y envía un JSON de error, si lo hubo.
        try { conexionHTTP.setRequestMethod("POST"); }
        catch (ProtocolException e) { return gestionarError(e, "Error del servidor."); }
        conexionHTTP.setRequestProperty("Content-Type", "application/json");

        // Envía el post, y devuelve un JSON de error, si lo hubo.
        conexionHTTP.setDoOutput(true);
        OutputStreamWriter flujoSalida;
        try {
            flujoSalida = new OutputStreamWriter(conexionHTTP.getOutputStream());
            flujoSalida.write(json.toString());
            flujoSalida.flush();
            flujoSalida.close();
        }
        catch (IOException e) { return gestionarError(e, "Error del servidor."); }

        // Obtiene el código de respuesta, y devuelve un JSON de error, si lo hubo.
        int codigoRespuesta = 0;
        try { codigoRespuesta = conexionHTTP.getResponseCode(); }
        catch (IOException e) { return gestionarError(e, "Error del servidor."); }
        System.out.println("* Código de respuesta del servidor: " + codigoRespuesta);

        // Lee la respuesta, y devuelve un JSON de error, si lo hubo.
        StringBuilder respuesta = new StringBuilder();
        try (BufferedReader bufferReader = new BufferedReader(
            new InputStreamReader(conexionHTTP.getInputStream())
        )) {
            String lineaEntrante;
            while ((lineaEntrante = bufferReader.readLine()) != null) { respuesta.append(lineaEntrante); }
            bufferReader.close();
        } catch (IOException e) { return gestionarError(e, "Error del servidor."); }

        // Devuelve el JSON, ya sea con error, o con la respuesta del servidor.
        return new JSONObject(respuesta.toString());
	}

    private JSONObject gestionarError(Exception e, String mensaje) {
        // Muestra el mensaje en la consola del servidor.
        e.printStackTrace();

        // Devuelve el mensaje para el usuario.
        return new JSONObject().put(
            "Error",
            mensaje
        );
    }

    /**
     * Le permite al usuario indicar si quiere terminar la ejecución o ingresar una tarea,
     * notificando a la función que llama, en este último caso, si la tarea ingresada es
     * soportada o no.
     *
     * Itera hasta conseguir la tarea, o hasta que el usuario decida terminar la ejecución
     * del programa.
     *
     * @return Una tarea válida ingresada por el usuario.
     */
    private String obtenerTarea() {
        String tarea = null;
        Scanner scanner = new Scanner(System.in);
        do {
            // Lee la entrada del usuario.
            System.out.print("Ingrese la tarea que desea realizar (o \"salir\" para terminar el programa): ");
            try { tarea = scanner.nextLine(); }

            // Si hubo un problema, termina el proceso.
            catch (Exception e) {
                e.getStackTrace();
                scanner.close();
                System.exit(1);
            }

            // Si ingresó "salir", termina el proceso.
            if(tarea.equals("salir")) {
                scanner.close();
                System.exit(0);
            } 

            // Compara al texto ingresado por el usuario con una lista de comandos.
            // Si no lo encuentra, notifica al usuario que la operación no está soportada.
            else if(!tareasSoportadas.contains(tarea)) {
                tarea = null;
                System.out.println("Operación no soportada. Vuelva a intentarlo.");
            }
        } while (tarea == null);

        // Devuelve el nombre de una tarea válida.
        scanner.close();
        return tarea;
    }

    // Le pide al usuario que ingrese un entero, y devuelve el mismo,
    // o null si no es soportada. Si sólo presiona Enter, se termina
    // el proceso, y si ocurre alguna excepción, termina el proceso.
    private JSONArray obtenerParametrosEnArregloJSON() {
        String entrada = null;
        boolean seguir = true;
        int entero;
        Scanner scanner = new Scanner(System.in);
        JSONArray arrayJSON = new JSONArray();
        do {
            // Lee la entrada del usuario.
            System.out.print(
                "Ingrese un entero; " +
                (
                    !arrayJSON.isEmpty()
                    ? "presione Enter sin realizar ningún ingreso, para indicarle al sistema que no quiere agregar más enteros; "
                    : ""
                ) +
                "o ingrese \"salir\" para terminar la ejecución del programa.");
            try { entrada = scanner.nextLine(); }

            // Si hubo un problema, termina el proceso.
            catch (Exception e) {
                e.getStackTrace();
                scanner.close();
                System.exit(1);
            }

            // Si ingresó "salir", termina la ejecución del programa, normalmente.
            if(entrada.equals("salir")) {
                scanner.close();
                System.exit(0);
            }

            // Si el usuario sólo presionó Enter, pero el arreglo JSON está vacío,
            // notifica al usuario que debe volver a realizar un ingreso.
            if(entrada.isEmpty()) {
                if(arrayJSON.isEmpty())
                    System.out.println("El ingreso no es válido; por favor, vuelva a intentar.");

            // Si el usuario sólo presionó Enter, y el arreglo JSON no está vacío,
            // deja de pedirle ingresos al usuario.
                else seguir = false;
            }

            // Añade el entero ingresado por el usuario al arreglo JSON.
            else try {
                entero = Integer.valueOf(entrada);
                arrayJSON.put(entero);
            }
            
            // Vuelve a pedirle un ingreso, porque el valor no es un entero.
            catch(Exception e) { System.out.println("Debe ingresar un entero."); }
        } while (seguir);

        // Devuelve el nombre de una tarea válida o null.
        scanner.close();
        return arrayJSON;
    }
}
