## Program

**DateServer.java** 
``` java
import java.net.*;
import java.io.*;
import java.util.*;

public class DateServer {
    public static void main(String[] args) throws Exception {

        DatagramSocket ss = new DatagramSocket(1234);
        System.out.println("Server socket created");
        while (true) {

            byte[] rd = new byte[100];
            byte[] sd = new byte[100];

            DatagramPacket rp = new DatagramPacket(rd, rd.length);
            ss.receive(rp);
            InetAddress ip = rp.getAddress();

            int port = rp.getPort();
            Date d = new Date();

            String time = d + "";
            sd = time.getBytes();
            DatagramPacket sp = new DatagramPacket(sd, sd.length, ip, port);
            ss.send(sp);
            rp = null;

            System.out.println("Server time sent");
        }
    }
}
```

**DateClient.java**
```java
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

```

## Output

### Screenshots

<img src = "https://user-images.githubusercontent.com/35297280/84594317-fa779d00-ae6e-11ea-8dfa-9c5f64c4114d.png" width = 45% >  <img src = "https://user-images.githubusercontent.com/35297280/84594331-0e230380-ae6f-11ea-9273-3357f7ef55ec.png" width = 45% >


### Output

**Server**
```
Server socket created
Server time sent
``` 

**Client**
```
Client socket created
SERVER DATE: Jun 14
SERVER TIME: 18:43:00
```

### ReadMe
1. Open first terminal
    1. ```javac DateServer.java```
    2. ```java DateServer```
   
2. Open second terminal
   1. ```javac DateClient.java```
   2. ```java DateClient``` -->