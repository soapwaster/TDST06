import socket
import struct
import sys
import time


DHOST = sys.argv[1]
DPORT = int (sys.argv[2])

s=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)

f=open('video1.mpg','rb')

donneesvideo = f.read(7975)

while donneesvideo != "" :
	s.sendto(donneesvideo, (DHOST, DPORT))
	time.sleep(0.1)
	donneesvideo = f.read(7975)


print "Received"
s.close()
f.close()