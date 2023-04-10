package tp01.ej01.src;

import java.io.*;
import java.net.*;

public class Servidor {

    public static void main(String[] args) {
        try {
            // Crea un socket de servidor.
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Escuchando en puerto " + PUERTO + "...");

            // Espera a que un cliente se conecte.
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + clientSocket.getInetAddress().getHostName() + ".");

            // Crea un objeto para leer los datos enviados por el cliente.
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Crea un objeto para enviar datos al cliente.
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Lee los datos enviados por el cliente y los envía de vuelta.
            String mensajeEntrante = in.readLine();
            System.out.println("Mensaje recibido: '" + mensajeEntrante + "'.");
            out.println(mensajeEntrante);

            // Cierra los flujos y los sockets.
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println( "Ocurrió un error: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    private static final int PUERTO = 5000;
}
