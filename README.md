# asyncWww
Asynchronous http in java, client and server

## finagle client

To launch Twitter Finagle client do `sbt run`.
It's also posssible to run it from IntelliJ. Scala plugin required.

## Efficiency measurements ##

### Methodology ###

A standalone physical machine was running a Spring Boot server.
Another physical machine, connected by LAN, was running clients, one at a time.

#### Max open files problem ####

When launching 5000 connections simultaneously, a problem occured: client
could not connect to Linux server.
Seems like the server was flooded and denied to serve some requests.
Problem was fixed on linux with this hint:

https://stackoverflow.com/a/24535229/772981

/etc/security/limits.conf:

    * soft nofile 2048 # Set the limit according to your needs
    * hard nofile 2048

### Results ###



## Discovered limits ##

From w Windows 10 machine it was impossible to get much more than 9000
outgoing connections. When trying to get 10000 an "address already
in bind" error was raised. This happened before I discovered that
connections in SO do not clear just after terminating java process.

Some time (1 minute maybe) after closing the client program
the connections are still held by Windows. It's visible in `netstat`.
To make measurements more reliable I waited till these connections were gone.

java.net.SocketException: No buffer space available (maximum connections reached?): connect

https://www.ibm.com/developerworks/community/blogs/kevgrig/entry/no_buffer_space_available_maximum_connections_reached?lang=en
https://docs.microsoft.com/en-us/previous-versions/windows/it-pro/windows-server-2003/cc739819(v=ws.10)
Komputer\HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters
MaxUserPort, default 5000

AsyncRequestTimeoutException, HTTP 503
spring.mvc.async.request-timeout=300000 or -1

https://stackoverflow.com/a/42220767/772981
server.tomcat.max-connections=30000

Max connections on Windows 10 spring boot tomcat: 40000 (linux 19000 + Windows 7 9000 + Windows 10 12000)
All requests fired below 1 minute, 3 minutes delay.
"received request 40000 on Thread[http-nio-8080-exec-18,5,main]"
memory used on server: 4.6G

