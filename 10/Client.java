import java.net.*;
import java.io.*;

class Client {
  public static void main(String[] args) {
    int bytesRead;
    byte[] aByte = new byte[1];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      Socket s = new Socket("127.0.0.1", 3000);
      System.out.println("Client socket created");

      DataOutputStream dos = new DataOutputStream(s.getOutputStream());
      DataInputStream dis = new DataInputStream(s.getInputStream());
      InputStream is = s.getInputStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String req = br.readLine();

      System.out.println("File: " + req);
      dos.writeUTF(req);
      FileOutputStream fos = null;
      fos = new FileOutputStream(req);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      bytesRead = is.read(aByte, 0, aByte.length);

      do {
        baos.write(aByte);
        bytesRead = is.read(aByte);
      } while (bytesRead != -1);

      bos.write(baos.toByteArray());
      bos.flush();
      bos.close();
      s.close();
    } catch (Exception e) {
    }

  }
}
