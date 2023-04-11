package ar.edu.unlu.sdypp.grupo1.tp1;

import java.io.IOException;
import java.net.*;

public class ClienteUDP {

    /* Miembros públicos */

    public static void main(String[] args) {

        try (
            DatagramSocket socketUDP = new DatagramSocket() //  Crea el socket UDP que envía/recibe.
        )
        {
            // Crea el mensaje.
            String mensaje = "Hola, mundo!";
            byte[] mensajeEnBytes = mensaje.getBytes();

            // Genera el datagrama con el mensaje.
            InetAddress address = InetAddress.getByName(HOST);
            int puerto = PUERTO;
            DatagramPacket paquete = new DatagramPacket(mensajeEnBytes, mensajeEnBytes.length, address, puerto);

            // Envía el datagrama.
            socketUDP.send(paquete);
            System.out.println("Mensaje enviado al servidor: '" + mensaje + "'.");

            // Queda escuchando que se devuelva el mensaje.
            byte[] buffer = new byte[1024];
            paquete = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(paquete);

            // Imprime el mensaje.
            System.out.println(
                "Mensaje recibido: '"
                + new String(paquete.getData()) + "'."
            );

        } catch (IOException e) {
            System.out.println( "Ocurrió un error: " + e.getMessage() );
            e.printStackTrace();
        }
    }


    /* Miembros privados */
    
    private static final int PUERTO = 5000;
    private static final String HOST = "localhost";
}