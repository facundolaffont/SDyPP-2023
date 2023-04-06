package ar.edu.unlu.sdypp.grupo1;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Cliente {

    public Cliente() {
        tareasSoportadas = new ArrayList<String>();
        tareasSoportadas.add("suma");
        tareasSoportadas.add("calculo-pi");
        init();
    }

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
        new Cliente();
    }


    /* Privado */
    private ArrayList<String> tareasSoportadas;

    private void init() {
        System.out.println("Bienvenido!\n");

        // Le pide al usuario que ingrese la tarea.
        String tarea;
        do {
            tarea = obtenerTarea();
        } while (tarea == null);
        
        // TODO: obtener parámetros.
        // Le pide al usuario que ingrese los parámetros.
        String parametro = null, parametros = null;
        do {
            /* Obtiene, de una función, el arreglo entero de enteros. */

            /* Obtiene, de una función, sólo un entero.
            * Obtiene el retorno del método A. (1)
            * Si el retorno es un entero.
                * Añade el entero al arreglo JSON.
            * Si no, si la función indica que el usuario no
            quiere seguir ingresando nada (con null):
                * El sistema construye el requerimiento y lo envía
                al servidor.

            Método A)
            * Se obtiene el ingreso del usuario, teniendo en cuenta que, antes
            de presionar Enter, el usuario puede: (a) ingresar un entero,
            (b) ingresar algo que no sea un entero, (c) no ingresar nada, o
            (d) ingresar "salir".
            * Si (d):
                * El sistema termina su ejecución normalmente.
            * Si no, si (b):
                * El sistema notifica al usuario y a la función que llama.
            * Si no, si (c):
                * El sistema notifica, a la función que llama, que no se ingresan
                más números.
            * Si no, si (a):
                * Devuelve, a la función que llama, el parámetro ingresado, en formato
                entero
            */ 
            parametro = obtenerParametros();
            if(para
        } while (parametro == null);
    }

    /**
     * Le permite al usuario indicar si quiere terminar la ejecución o ingresar una tarea,
     * notificando a la función que llama, en este último caso, si la tarea ingresada es
     * soportada o no.
     *
     * La función
     *
     * @return Una tarea válida ingresada
    private String obtenerTarea() {
        // Lee la entrada del usuario.
        String tarea = null;
        Scanner scanner = new Scanner(System.in);
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
        
        // Compara al texto ingresado por el usuario con una lista de comandos.
        // Si no lo encuentra, notifica al usuario que la operación no está soportada.
        } else if(!tareasSoportadas.contains(tarea)) {
            tarea = null;
            System.out.println("Operación no soportada. Vuelva a intentarlo.");
        }

        // Devuelve el nombre de una tarea válida o null.
        scanner.close();
        return tarea;
    }

    // Le pide al usuario que ingrese un entero, y devuelve el mismo,
    // o null si no es soportada. Si sólo presiona Enter, se termina
    // el proceso, y si ocurre alguna excepción, termina el proceso.
    private String obtenerEntero() {
        // Lee la entrada del usuario.
        String parametros = null;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese los parámetros numéricos (enteros) que desea procesar; un carácter punto (.) para no agregar más parámetros; o Enter para finalizar: ");
        try { tarea = scanner.nextLine(); }

        // Si hubo un problema, termina el proceso.
        catch (Exception e) {
            e.getStackTrace();
            scanner.close();
            System.exit(1);
        }

        // Si ingresó un punto, no
        // Si sólo presionó Enter, termina el proceso.
        if(tarea.isEmpty()) {
            scanner.close();
            System.exit(0);
        
        // Compara al texto ingresado por el usuario con una lista de comandos.
        // Si no lo encuentra, notifica al usuario que la operación no está soportada.
        } else if(!tareasSoportadas.contains(tarea)) {
            tarea = null;
            System.out.println("Operación no soportada. Vuelva a intentarlo.");
        }

        // Devuelve el nombre de una tarea válida o null.
        scanner.close();
        return tarea;
    }
}
