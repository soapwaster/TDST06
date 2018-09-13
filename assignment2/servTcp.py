#import socket
#import sys

#HOST = ''
#PORT = int (sys.argv[1])
#s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#s.bind((HOST,PORT))

#s.listen(1)
#conn, addr = s.accept()
#while 1 :
#	data = conn.recv(1024)
#	if not data : break
#	conn.sendall("Received %s" %(data))
#conn.close()
#s.close()

###################################################################

#import socket
#import signal
#import os

#def do_echo(self) :
#	s.close()
#	while 1:
#		data = conn.recv(1024)
#		if not data : break
#		conn.sendall(data)
#	conn.close()
#	os._exit(0)

#HOST = ''
#PORT = 5000
#signal.signal(signal.SIGCHLD, signal.SIG_IGN); #zombie mngmt
#s=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
#s.bind((HOST,PORT))
#s.listen(1)
#while 1:
#	conn, addr = s.accept()
#	pid = os.fork()
#	if pid == 0 :
#		do_echo()
#	else :
#		conn.close()
#	s.close()

###################################################################

import socket
import struct 
import sys
import time 
import os
import signal
from eliza import analyze

HOST = ''
PORT = int(sys.argv[1])            # The same port as used by the server

def child():
	s.close()
	while 1:
		time.sleep(3)
		data = conn.recv(1024)
		
		if not data:break
		conn.sendall(analyze(data))
	conn.close()
	os._exit(0)

signal.signal(signal.SIGCHLD,signal.SIG_IGN)
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST,PORT))
s.listen(1)
while 1:
	conn, addr = s.accept()
	pid=os.fork()
	if pid == 0:
		child()
	else:
		conn.close()
s.close()
		
