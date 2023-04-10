package tp01.ej01.src;

import java.io.*;
import java.net.*;

public class Cliente {

    public static void main(String[] args) {
        try {
            // Crea un socket de cliente y se conecta al servidor en el puerto especificado.
            Socket socket = new Socket("localhost", PUERTO);

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

    private static final int PUERTO = 5000;
}