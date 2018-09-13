import socket
import struct
import sys
import time
import os
import signal

HOST = '127.0.0.1'
PORT = int(2890)            # The same port as used by the server

def child():
	s.close()
	while 1:

		data = conn.recv(1024)
		if not data:break
		url_stuff = data.split("\n")[0]
		if "prova" in url_stuff:
			print("not good")
			redirect = "HTTP/1.1 301 Moved Permanently\nLocation: https://www.liu.se//\nConnection: close\nContent-length: 0\r\n\r\n"
			conn.send(redirect)


	conn.close()
	os._exit(0)

def get_header(http_content):
	return http_content.split("\r\n")
#signal.signal(signal.SIGCHLD,signal.SIG_IGN)
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind((HOST,PORT))
s.listen(1)
while 1:
	conn, addr = s.accept()
#	pid=os.fork()
#	if pid == 0:
	child()
#	else:
	conn.close()
s.close()
