package jarek

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.net.URL
import java.net.URLConnection

object SimpleJavaClient {
  @JvmStatic
  fun main(args: Array<String>) {
    val connections = mutableListOf<URLConnection>()
    for(i in 1..3) {
      try {
        val c = URL(Pars.serverUrl).openConnection()!!
        c.connect()
        c.getInputStream()
        connections.add(c)
        if (i < 4)
          println("connection $i ok")
      } catch (e: Exception) {
        println("error at $i: $e")
        break
      }
      //s.getOutputStream().write("GET / HTTP/1.1\nHost: localhost\n\n".toByteArray())
      //val br = BufferedReader(InputStreamReader(s.getInputStream()))
      //println(br.readLine())
    }
    println("reading all data")
    var requestsDone = 0
    connections.forEach {
      val bytes = it.getInputStream().readBytes()
      if (requestsDone < 10) {
        println("read bytes " + bytes.size)
        requestsDone++
      }
    }
    println("done")
  }
}