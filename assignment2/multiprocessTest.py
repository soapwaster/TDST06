from multiprocessing import Process
#import os
from os import getpid
from os import getppid

def info(title):
    print('info')
#    print('parent process:', os.getppid())
    print('process id:', os.getpid())

def f(name):
    info('function f')
    
    print('process id:', os.getpid())

if __name__ == '__main__':
    
    p = Process(target=f, args=('bob',))
    p.start()
    p.join()
    print("went back to parent")
    print('process id:', os.getpid())