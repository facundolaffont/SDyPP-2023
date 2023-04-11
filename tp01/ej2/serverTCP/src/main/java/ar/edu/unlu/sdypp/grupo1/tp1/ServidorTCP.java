package ar.edu.unlu.sdypp.grupo1.tp1;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorTCP {
    
    /* Miembros públicos */

    public static void main(String[] args) {

        // Se crea la pool de hilos que se utilizarán, determinando una máxima cantidad de hilos concurrentes.
        ExecutorService pool = Executors.newFixedThreadPool(MAX_HILOS);

        try ( ServerSocket socketDeServer = new ServerSocket(PUERTO) ) {
            System.out.println("Escuchando en puerto " + PUERTO + "...");

            while (true) {
                // Espera a se establezca una conexión con el socket del server, crea un socket de cliente y lo
                // vincula con la conexión establecida.
                Socket socketDeCliente = socketDeServer.accept();

                System.out.println("\nNuevo cliente conectado desde " + socketDeCliente.getInetAddress().getHostName() + ".");

                // Ejecuta la tarea en un nuevo hilo.
                pool.execute(new ManejadorDeCliente(socketDeCliente));
            }

        }
        catch (IOException e) { ServidorTCP.mostrarErrorIO(e); }
        finally { pool.shutdown(); } // Este método para la ejecución de tareas, pero permitiendo
                                     // que las tareas ya aceptadas se finalicen.
    }


    /* Miembros privados */

    private static final int PUERTO = 5000;
    private static final int MAX_HILOS = 10;

    private static class ManejadorDeCliente implements Runnable { // Implementa Runnable para que las instancias de
                                                                  // esta clase puedan ser ejecutadas en hilos.

        public ManejadorDeCliente(Socket socketDeCliente) {
            this.socketDeCliente = socketDeCliente;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(
                    new InputStreamReader( socketDeCliente.getInputStream() )
                );
                PrintWriter out = new PrintWriter(socketDeCliente.getOutputStream(), true)
            ) {
                String lineaEntrante;
                while ( ( lineaEntrante = in.readLine() ) != null ) {
                    System.out.println("\nMensaje recibido: '" + lineaEntrante + "'.");
                    out.println(lineaEntrante);
                }
                
            }
            catch (IOException e) { ServidorTCP.mostrarErrorIO(e); }
            finally {
                try { socketDeCliente.close(); }
                catch (IOException e) {  ServidorTCP.mostrarErrorIO(e); }
            }
        }

        private Socket socketDeCliente;
    }

    static private void mostrarErrorIO(IOException e) {
        System.out.println( "Ocurrió un error: " + e.getMessage() );
        e.printStackTrace();
    }
}
