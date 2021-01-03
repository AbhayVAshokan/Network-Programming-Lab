#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <linux/ip.h>
#include <linux/udp.h>

#define PCKT_LEN 8192

unsigned short csum(unsigned short *buf, int nwords)
{
  unsigned long sum;
  for (sum = 0; nwords > 0; nwords--)
    sum += *buf++;
  sum = (sum >> 16) + (sum & 0xffff);
  sum += (sum >> 16);
  return (unsigned short)(~sum);
}

int main(int argc, char const *argv[])
{
  if (argc != 5)
  {
    printf("Error: Invalid parameters!\n");
    printf("Usage: %s <source hostname/IP> <source port> <target hostname/IP> <target port>\n", argv[0]);
    exit(1);
  }

  u_int16_t src_port, dst_port;
  u_int32_t src_addr, dst_addr;
  src_addr = inet_addr(argv[1]);
  dst_addr = inet_addr(argv[3]);
  src_port = atoi(argv[2]);
  dst_port = atoi(argv[4]);

  printf("\nSOURCE IP: %s", argv[1]);
  printf("\nSOURCE PORT: %s", argv[2]);
  printf("\nDESTINATION IP: %s", argv[3]);
  printf("\nDESTINATION PORT: %s\n", argv[4]);

  int sd;
  char buffer[PCKT_LEN];
  struct iphdr *ip = (struct iphdr *)buffer;
  struct udphdr *udp = (struct udphdr *)(buffer + sizeof(struct iphdr));

  struct sockaddr_in sin;
  int one = 1;
  const int *val = &one;

  memset(buffer, 0, PCKT_LEN);

  sd = socket(PF_INET, SOCK_RAW, IPPROTO_UDP);
  if (sd < 0)
  {
    perror("socket() error");
    exit(2);
  }
  printf("OK: a raw socket is created.\n");

  if (setsockopt(sd, IPPROTO_IP, IP_HDRINCL, val, sizeof(one)) < 0)
  {
    perror("setsockopt() error");
    exit(2);
  }
  printf("OK: socket option IP_HDRINCL is set.\n");

  sin.sin_family = AF_INET;
  sin.sin_port = htons(dst_port);
  sin.sin_addr.s_addr = inet_addr("127.0.0.1");

  ip->ihl = 5;
  ip->version = 4;
  ip->tos = 16;
  ip->tot_len = sizeof(struct iphdr) + sizeof(struct udphdr);
  ip->id = htons(54321);
  ip->ttl = 64;
  ip->protocol = 17;
  ip->saddr = src_addr;
  ip->daddr = dst_addr;

  udp->source = htons(src_port);
  udp->dest = htons(dst_port);
  udp->len = htons(sizeof(struct udphdr));

  ip->check = csum((unsigned short *)buffer,
                   sizeof(struct iphdr) + sizeof(struct udphdr));

  if (sendto(sd, buffer, ip->tot_len, 0,
             (struct sockaddr *)&sin, sizeof(sin)) < 0)
  {
    perror("sendto()");
    exit(3);
  }

  printf("\nPACKET LENGTH: %d", ip->tot_len);
  printf("\nHOPS: %d", ip->ttl);
  printf("\nOK: one packet is sent.\n");

  close(sd);
  return 0;
}