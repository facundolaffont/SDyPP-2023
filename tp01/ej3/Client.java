import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        // Get client ID and source port
        System.out.print("Enter your client ID: ");
        String clientID = scanner.nextLine();
        System.out.print("Enter your source port number: ");
        int sourcePort = scanner.nextInt();
        scanner.nextLine();

        // Get server port
        System.out.print("Enter the server port number: ");
        int serverPort = scanner.nextInt();
        scanner.nextLine();

        try (Socket socket = new Socket(InetAddress.getLocalHost(), serverPort, InetAddress.getLocalHost(), sourcePort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server.");

            // Send client ID to server
            out.println(clientID);

            boolean running = true;
            while (running) {
                // Prompt user to select an option
                System.out.println("Select an option:");
                System.out.println("1. Send message");
                System.out.println("2. Check messages");
                System.out.println("3. Exit");
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        // Prompt user to enter message and send it to server
                        System.out.print("Enter recipient ID: ");
                        String destID = scanner.nextLine();
                        System.out.print("Enter message: ");
                        String message = scanner.nextLine();
                        out.println(destID + ":" + message);
                        System.out.println("Message sent.");
                        break;
                    case 2:
                        // Request messages from server
                        out.println("getMessages");
                        String response;
                        boolean receivedResponse = false;
                        while (!receivedResponse) {
                            try {
                                // Wait for response for up to 5 seconds
                                socket.setSoTimeout(5000); // Timeout set to 5 seconds
                                while ((response = in.readLine()) != null && !response.equals("done")) {
                                    System.out.println(response);
                                }
                                receivedResponse = true;
                            } catch (IOException e) {
                                System.err.println("Error: " + e.getMessage());
                                System.err.println("No response from server. Please try again later.");
                                break;
                            }
                        }
                        break;
                    case 3:
                        // Exit program
                        out.println("exit");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                        break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

    }
}
