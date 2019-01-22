package jarek

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.net.URL
import java.net.URLConnection
import java.util.*
import java.util.concurrent.Future
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.Response
import kotlin.system.exitProcess


object JaxrsClient {
  @JvmStatic
  fun main(args: Array<String>) {
    val t0 = System.currentTimeMillis()
    val futures = LinkedList<Future<Response>>()
    val client = ClientBuilder.newClient()

    for(i in 1..1000) {
      try {
        //.header("Content-type", "application/json")
        val req = client.target("http://localhost:8080/waitAsync?seconds=10").request()
        val fut = req.async().get()
        println("created future $i")
        futures.add(fut)
      } catch (e: Exception) {
        println("error at $i: $e")
        break
      }
    }
    println("getting futures, time: " + (System.currentTimeMillis() - t0)/1000.0)
    futures.forEach {
      val resp = it.get()!!
      println("" + resp.getStatus() + " " + resp.readEntity(String::class.java)
        + ", time: " + (System.currentTimeMillis() - t0)/1000.0)
      resp.close()
    }
    println("done")
    exitProcess(0)
  }
}