import java.net.*;
import java.io.*;
import java.util.*;

public class MailClient {
	final static int ServerPort = 1729;

	public static void main(String args[]) throws UnknownHostException, IOException {
		Scanner scanner = new Scanner(System.in);
		InetAddress ip = InetAddress.getByName("localhost");
		Socket socket = new Socket(ip, ServerPort);

		System.out.println("Client socket created");

		DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		System.out.println("Client request sent");

		Thread sendMessage = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.print("\nEnter your address: ");
				String address = scanner.nextLine();

				try {
					dataOutputStream.writeUTF(address);
				} catch (IOException e) {
					e.printStackTrace();
				}

				while (true) {
					try {
						System.out.print("\nEnter receiver's address: ");
						String recipient = scanner.nextLine();

						if (recipient.equals("logout") || recipient.equals("quit") || recipient.equals("bye")
								|| recipient.equals("exit")) {
							System.out.println("Session terminated");
							System.exit(0);
						} else {
							System.out.print("Enter your message: ");
							String subject = scanner.nextLine();
							dataOutputStream.writeUTF(subject + "#" + recipient);
						}

					} catch (IOException e) {
						System.out.println("An IO Exception occured");
					}
				}
			}
		});

		Thread readMessage = new Thread(new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						String received = dataInputStream.readUTF();
						try {
							StringTokenizer st = new StringTokenizer(received, "#");
							String MsgToSend = st.nextToken();
							String sender = st.nextToken();
							System.out.println("New message from " + sender);
							System.out.println(MsgToSend);
							System.out.print("\nEnter receiver's address: ");
						} catch (NoSuchElementException e) {
							System.out.println(received);
							System.out.print("\nEnter receiver's address: ");
						}
					} catch (IOException e) {
						System.out.println("An IO Exception occurred");
					}
				}
			}
		});

		sendMessage.start();
		readMessage.start();
	}
}
