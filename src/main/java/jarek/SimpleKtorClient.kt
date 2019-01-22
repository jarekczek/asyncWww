package jarek

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.net.URL
import java.net.URLConnection

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.config
import io.ktor.client.request.get
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

object SimpleKtorClient {
  @JvmStatic
  fun main(args: Array<String>) {
    val t0 = System.currentTimeMillis()
    fun time() = ", time: " + ((System.currentTimeMillis() - t0) / 1000.0)
    runBlocking(newFixedThreadPoolContext(5, "apool")) {
      val coroutines = Collections.synchronizedList(mutableListOf<Deferred<Unit>>())
      for(i in 1..5000) {
        val coroutine = async {
          //println("inside async")
          val cli = HttpClient(CIO)
          //println("client ready")
          val res = cli.get<String>("http://localhost:8080/waitAsync?seconds=120")
          //println("read msg " + res + time())
        }
        coroutines.add(coroutine)
      }
      coroutines.forEach { it.join() }
    }
    /*
    val connections = mutableListOf<URLConnection>()
    for(i in 1..100000) {
      try {
        val c = URL("http://localhost:8080").openConnection()!!
        c.connect()
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
    var first = true
    connections.forEach {
      val bytes = it.getInputStream().readBytes()
      if (first) {
        println("read bytes " + bytes.size)
        first = false
      }
    }
    */
    println("done" + time())
  }
}