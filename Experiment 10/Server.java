import java.io.*;
import java.net.*;

class Server {
  public static void main(String[] args) {
    while (true) {
      Socket cs = null;
      String file = null;
      ServerSocket socket = null;
      BufferedOutputStream outToClient = null;

      try {
        socket = new ServerSocket(3000);
        cs = socket.accept();
        DataInputStream dis = new DataInputStream(cs.getInputStream());
        outToClient = new BufferedOutputStream(cs.getOutputStream());
        file = dis.readUTF();

        System.out.println("Server socket created");
      } catch (IOException ex) {
      }

      if (outToClient != null) {

        System.out.println("Requested file: " + file);
        File myFile = new File(file);

        if (myFile.exists())
          System.out.println("File found! sending");

        else
          System.out.println("File not found");
        FileInputStream fis = null;
        byte[] mybytearray = new byte[(int) myFile.length()];
        try {
          fis = new FileInputStream(myFile);
        } catch (FileNotFoundException ex) {
        }
        BufferedInputStream bis = new BufferedInputStream(fis);

        try {
          bis.read(mybytearray, 0, mybytearray.length);
          outToClient.write(mybytearray, 0, mybytearray.length);
          outToClient.flush();
          outToClient.close();
          cs.close();

          return;
        } catch (IOException ex) {
        }
      }
    }
  }
}
