import socket
import struct
import sys
import time
import os
import errno
import signal

HOST = '127.0.0.1'
PORT = int(2890)            # The same port as used by the server
not_good_words = ["spongebob", "britney spears", "paris hilton", "norrkopping"]

def child():
	s.close()


	data = conn.recv(1024)
#	if not data:break
	url_stuff = get_url(data)
	host_stuff = get_host(data)
	if not allow_request(url_stuff):
		redirect_to(conn, "https://www.ida.liu.se/~TDTS04/labs/2011/ass2/error1.html")
	else:
		#Create socket as client
		client_req = data
		total_data = []
		#Initialize socket for talking to the server
		ss = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		ss.settimeout(5)
		#Get host name and send the same request the client did
		host_name = get_host_name(host_stuff)
		send_http_request(ss, host_name, url_stuff, host_stuff)
		#Start fetching until the end of the content.
		while 1:
			try:
				data  = ss.recv(4096)
				if len(data) == 0 :
					break
				#conn.send(data)
				total_data.append(data)
			except socket.error, e:
				if isinstance(e.args, tuple):
					if e[0] == errno.EPIPE:
						print "Detected remote disconnect"
					else:
						pass
				else:
					print "socket error ", e
				break
		page_txt = "".join(total_data)
		if not is_contenttype_text(page_txt):
			conn.sendall(page_txt)
		else:
			if is_analyize_content_ok(page_txt):
				conn.sendall(page_txt)
			else:
				redirect_to(conn, "https://www.ida.liu.se/~TDTS04/labs/2011/ass2/error2.html")
		ss.close()
	conn.close()
	os._exit(0)

def get_header(http_content):
	return http_content.split("\r\n\r\n")

def get_url(data):
	return data.split("\n")[0].strip()
def get_host(data):
	try:
		hn = data.split("\n")[1].strip()
	except IndexError:
		print data.split("\n")
		os._exit(1)
	return hn
def is_contenttype_text(data):
	return "text" in data[0:1000].lower().split("content-type:")[1].split("\n")[0]
def is_analyize_content_ok(content):
	low_content = content.lower()
	for el in not_good_words:
		if el in low_content:
			return False
	return True;
def redirect_to(socket, page):
	redirect = "HTTP/1.1 301 Moved Permanently\nLocation: "+page+"\nConnection: close\nContent-length: 0\r\n\r\n"
	socket.send(redirect)
def allow_request(url):
	return is_analyize_content_ok(url)
def get_host_name(host_line):
	try:
		hn = host_line.split(":")[1].strip()
	except IndexError:
		print host_line
		os._exit(1)
	return hn
def send_http_request(socket, hostname, url, host):
	socket.connect((hostname, 80))
	socket.send(''+url+'\r\n'+host+"\r\n\r\n")


signal.signal(signal.SIGCHLD,signal.SIG_IGN)
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
s.bind((HOST,PORT))
s.listen(100)
while 1:
	conn, addr = s.accept()
	pid=os.fork()
	if pid == 0:
		child()
	else:
		conn.close()
s.close()
