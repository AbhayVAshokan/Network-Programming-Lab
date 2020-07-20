import java.net.*;
import java.io.*;

public class Server {
    // initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;

    // constructor with port
    public Server(int port) {
        // starts server and waits for a connection
        try {
            server = new ServerSocket(port);
            System.out.println("Server socket created");

            socket = server.accept();

            // takes input from the client socket
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String line = "";

            // reads message from client until "Over" is sent
            while (!line.equals("Over")) {
                try {
                    line = in.readUTF();
                    System.out.println("From Client: " + line);

                } catch (IOException i) {
                }
            }

            // close connection
            socket.close();
            in.close();
        } catch (IOException i) {
        }
    }

    public static void main(String args[]) {
        Server server = new Server(5000);
    }
}