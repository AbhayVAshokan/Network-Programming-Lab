import java.net.*;
import java.io.*;
import java.util.*;

public class DateClient {

    public static void main(String[] args) throws Exception {

        System.out.println("Client socket created");
        DatagramSocket cs = new DatagramSocket();
        InetAddress ip = InetAddress.getByName("localhost");

        byte[] rd = new byte[100];
        byte[] sd = new byte[100];

        DatagramPacket sp = new DatagramPacket(sd, sd.length, ip, 1234);
        DatagramPacket rp = new DatagramPacket(rd, rd.length);

        cs.send(sp);
        cs.receive(rp);

        String time = new String(rp.getData());
        String time1 = time.substring(4, 10);
        String date = time.substring(11, 19);
        System.out.println("SERVER DATE: " + time1);
        System.out.println("SERVER TIME: " + date);
        cs.close();
    }
}
