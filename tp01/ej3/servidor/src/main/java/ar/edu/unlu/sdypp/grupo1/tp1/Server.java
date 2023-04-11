package ar.edu.unlu.sdypp.grupo1.tp1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final int DEFAULT_PORT = 8000;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        Map<String, Socket> clients = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port + ".");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());

                Thread thread = new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        // Register client ID and socket
                        String clientID = in.readLine();
                        clients.put(clientID, clientSocket);
                        System.out.println("Client " + clientID + " registered.");

                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            System.out.println("Received message from client " + clientID + ": " + inputLine);
                            String[] tokens = inputLine.split(":", 2);                           

                            if (tokens.length < 2) {
                                out.println("Invalid message format. Expected: 'destID:message'");
                                continue;
                            }

                            String destID = tokens[0];
                            String message = tokens[1];

                            Socket destSocket = clients.get(destID);
                            if (destSocket == null) {
                                out.println("Client " + destID + " is not connected.");
                            } else if (destSocket == clientSocket) {
                                out.println("You cannot send a message to yourself.");
                            } else {
                                PrintWriter destOut = new PrintWriter(destSocket.getOutputStream(), true);
                                destOut.println("Message from client " + clientID + ": " + message);
                            }
                        }

                        // Remove client from registry
                        clients.remove(clientID);
                        System.out.println("Client " + clientID + " disconnected.");

                    } catch (IOException e) {
                        System.err.println("Error handling client: " + e.getMessage());
                    }
                });
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}
