## Program

``` c
// Configure the following services in the network-  FTP server, Web server, File server - Implementation and Demonstration.

#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <sys/socket.h>
#include<netinet/in.h>
#include<time.h>
#define PORT_FTP        21              /* FTP connection port */
#define SERVER_ADDR     "192.168.1.6"     /* localhost */
#define MAXBUF          1024

int main()
{   int sockfd;
    struct sockaddr_in dest;
    char buffer[MAXBUF];

    /*---Open socket for streaming---*/
    if ( (sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0 )
    {
        perror("Socket");
        exit(errno);
    }

    /*---Initialize server address/port struct---*/
    bzero(&dest, sizeof(dest));
    dest.sin_family = AF_INET;
    dest.sin_port = htons(PORT_FTP);
    if ( inet_aton(SERVER_ADDR, &dest.sin_addr.s_addr) == 0 )
    {
        perror(SERVER_ADDR);
        exit(errno);
    }

    /*---Connect to server---*/
    if ( connect(sockfd, (struct sockaddr*)&dest, sizeof(dest)) != 0 )
    {
        perror("Connect ");
        exit(errno);
    }

    /*---Get "Hello?"---*/
    bzero(buffer, MAXBUF);
    recv(sockfd, buffer, sizeof(buffer), 0);
    printf("%s", buffer);

    /*---Clean up---*/
    close(sockfd);
    return 0;
}


```
## Output

### Screenshot

<img src = "https://user-images.githubusercontent.com/35297280/87858389-3066e000-c94b-11ea-9788-f98729bd92c2.png" width = 45% >  <img src = "https://user-images.githubusercontent.com/35297280/87858390-3230a380-c94b-11ea-903c-1aac809b8a7d.png" width = 45% >


### Output

```
Serving HTTP on 0.0.0.0 port 8000 (http://0.0.0.0:8000/) ...
127.0.0.1 - - [18/Jul/2020 23:07:23] "GET / HTTP/1.1" 200 -
```

### ReadMe
1. Run the ```ftpclient.c``` file using ```gcc ftpclient.c && ./a.out```
2. Run the server using the command ```python3 -m http.server```
3. Navigate to the address ```localhost:8000``` to get the directory listed.