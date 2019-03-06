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
    
## Discovered limits ##

From w Windows 10 machine it was impossible to get much more than 9000
outgoing connections. When trying to get 10000 an "address already
in bind" error was raised.
