import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;

public class MailServer {
    static Vector<ClientHandler> ar = new Vector<>();
    static int i = 0;

    public static void main(final String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(1729);
        System.out.println("Server socket created");
        Socket socket;
        final ArrayBlockingQueue<MessageQueue> queue = new ArrayBlockingQueue<MessageQueue>(25);

        while (true) {
            socket = serverSocket.accept();
            System.out.println("New client request received");

            final DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            final ClientHandler clientHandler = new ClientHandler(socket, dataInputStream, dataOutputStream, queue);
            final Thread t = new Thread(clientHandler);

            ar.add(clientHandler);
            t.start();
            i++;
        }
    }
}

class ClientHandler implements Runnable {
    private String name;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    Socket socket;
    boolean isloggedin;
    ArrayBlockingQueue<MessageQueue> queue;

    public ClientHandler(final Socket socket, final DataInputStream dataInputStream, final DataOutputStream dataOutputStream,
            final ArrayBlockingQueue<MessageQueue> queue) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.socket = socket;
        this.isloggedin = true;
        this.queue = queue;
    }

    @Override
    public void run() {
        String received;

        try {
            this.name = dataInputStream.readUTF();
        } catch (final IOException e) {
            System.out.println("An IO Exception occured");
        }

        while (true) {
            try {
                for (final MessageQueue q : queue) {
                    if (q.name.equals(name)) {
                        System.out.println(q);
                        dataOutputStream.writeUTF(q.message + "#" + q.sender);
                        queue.remove(q);
                    }
                }

                received = dataInputStream.readUTF();
                System.out.println(received);
                if (received.equals("logout") || received.equals("exit") || received.equals("bye")
                        || received.equals("quit")) {
                    System.out.println("Session terminated");
                    this.isloggedin = false;
                    this.socket.close();
                    break;
                }

                try {
                    final StringTokenizer st = new StringTokenizer(received, "#");
                    final String MsgToSend = st.nextToken();
                    final String recipient = st.nextToken();
                    boolean found = false;
                    for (final ClientHandler mc : SMTPServer.ar) {
                        if (mc.name.equals(recipient) && mc.isloggedin == true) {
                            mc.dataOutputStream.writeUTF(MsgToSend + "#" + this.name);
                            found = true;
                            break;
                        }
                    }
                    if (found == false) {
                        dataOutputStream.writeUTF(
                                "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\bRecipient not online, message added to queue");
                        final MessageQueue q = new MessageQueue();
                        q.name = recipient;
                        q.sender = name;
                        q.message = MsgToSend;
                        queue.add(q);
                    }
                } catch (final NoSuchElementException e) {
                    System.out.println(received);
                    System.out.println("Enter address of recipient.");
                }

            } catch (final IOException e) {
            }
        }

        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();

        } catch (final IOException e) {
        }
    }
}

class MessageQueue {
    public String name;
    public String sender;
    public String message;
}
