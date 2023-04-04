package ar.edu.unlu.sdypp.grupo1;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    public void requerirProcesamiento(String json) {
        try {
            // Crea un socket de cliente y se conecta al servidor en el puerto especificado.
            Socket socket = new Socket("localhost", 5000);

            // Crea un objeto PrintWriter para enviar datos al servidor.
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Envía un mensaje al servidor.
            String mensajeEnviado = "¡Hola, servidor!";
            out.println(mensajeEnviado);

            // Crea un objeto para leer los datos enviados por el servidor.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Lee los datos enviados por el servidor, verifica que sean los mismos, y los imprime en la consola.
            String mensajeEntrante = in.readLine();
            System.out.println("Mensaje recibido del servidor:");
            System.out.println("\t" + mensajeEntrante);
            System.out.println(
                mensajeEnviado.equals(mensajeEntrante)
                ? "Se recibió el mismo mensaje que fue previamente enviado!"
                : "El mensaje recibido es distinto al que se envió previamente."
            );

            // Cierra los flujos y el socket.
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println( "Ocurrió un error: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Bienvenido!\n");

        // Le pide al usuario que ingrese los datos para realizar la petición de la tarea.
        Peticion peticion;
        String tarea;
        do {
            tarea = obtenerTarea();
        } while (tarea == null);
        do {
            parametros = obtenerParametros();
        } while (parametros == null);
    }

    private class Peticion {
        Peticion() {

        }
    }

    private static String obtenerTarea() {
        // Lee la entrada del usuario.
        String input;
        Scanner scanner = new Scanner(System.in);
        input = null;
        System.out.print("Ingrese la tarea que desea realizar (o Enter para salir): ");
        try { input = scanner.nextLine(); }
        catch (Exception e) {
            e.getStackTrace();
            scanner.close();
            System.exit(1);
        }

        // Determina si es válida la operación.
        String tarea = null;
        if(input.isEmpty()) {
            scanner.close();
            System.exit(0);
        }
        else switch(input) {
            case "suma":
            case "calculo-pi":
                tarea = input;
            break;
            default:
                System.out.println("Operación no soportada. Vuelva a intentarlo.");
        }

        return tarea;
    }
}