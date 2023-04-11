package ar.edu.unlu.sdypp.grupo1.tp1;

import java.io.*;
import java.net.*;

public class ServidorUDP {
    
    /* Miembros públicos */

    public static void main(String[] args) {

        try (
            DatagramSocket socketUDP = new DatagramSocket(PUERTO) // El server comienza a escuchar en un socket UDP.
        ) {
            System.out.println("Escuchando en puerto " + PUERTO + "...");

            while (true) {

                // Queda escuchando en el socket.
                byte[] buffer = new byte[TAMANIO_BUFFER];
                DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(paqueteRecibido);
                
                // Convierte el mensaje recibido en String y lo muestra.
                String mensajeRecibido = new String( paqueteRecibido.getData(), 0, paqueteRecibido.getLength() );
                System.out.println(
                    "Nuevo mensaje recibido desde <"
                    + paqueteRecibido.getAddress().getHostName() + ":"
                    + paqueteRecibido.getPort() + ">: '"
                    + mensajeRecibido + "'."
                );

                // Obtiene los datos del cliente y le devuelve el mensaje.
                InetAddress direccionCliente = paqueteRecibido.getAddress();
                int puertoCliente = paqueteRecibido.getPort();
                byte[] mensajeEnBytes = mensajeRecibido.getBytes();
                DatagramPacket respuesta = new DatagramPacket(mensajeEnBytes, mensajeEnBytes.length, direccionCliente, puertoCliente);
                socketUDP.send(respuesta);
            }

        }
        catch (IOException e) { 
            System.out.println( "Ocurrió un error: " + e.getMessage() );
            e.printStackTrace();
        }
    }


    /* Miembros privados */

    private static final int PUERTO = 5000;
    private static final int TAMANIO_BUFFER = 1024;
}