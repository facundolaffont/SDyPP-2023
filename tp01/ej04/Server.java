import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private static final int PORT = 8080;
    private Map<String, PrintWriter> clients = new HashMap<>();
    private Map<String, BlockingQueue<String>> messageQueues = new HashMap<>();

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                Thread thread = new Thread(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                        String clientID = in.readLine();
                        System.out.println("Client ID: " + clientID);
                        clients.put(clientID, out);

                        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
                        messageQueues.put(clientID, messageQueue);

                        boolean running = true;
                        while (running) {
                            String request = in.readLine();
                            if (request == null) {
                                break;
                            }

                            if (request.equals("getMessages")) {
                                sendMessages(out, messageQueue);
                            } else if (request.startsWith("ACK:")) {
                                String ackMessage = request.substring(4);
                                messageQueue.remove(ackMessage);
                                System.out.println("Message removed from queue: " + ackMessage);
                            } else if (request.equals("exit")) {
                                clients.remove(clientID);
                                messageQueues.remove(clientID);
                                running = false;
                            } else {
                                // Split the request into the recipient ID and the message content
                                String[] parts = request.split(":", 2);
                                String destID = parts[0];
                                String message = parts[1];

                                // Send the message to the recipient
                                PrintWriter destOut = clients.get(destID);
                                if (destOut != null) {
                                    destOut.println("Message from " + clientID + ": " + message);
                                    messageQueue.add(message);
                                } else {
                                    out.println("Recipient not found.");
                                }
                            }
                        }

                    } catch (IOException e) {
                        System.err.println("Error handling client: " + e.getMessage());
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.err.println("Error closing client socket: " + e.getMessage());
                        }
                    }
                });
                thread.start();
            }
        }
    }

    private void sendMessages(PrintWriter out, BlockingQueue<String> messageQueue) {
        String message;
        while ((message = messageQueue.poll()) != null) {
            out.println(message);
        }
        out.println("done");
    }

    public static void main(String[] args) throws IOException {
        new Server().start();
    }
}
