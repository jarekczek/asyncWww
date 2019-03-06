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
    fun time() = ", time: " + ((System.currentTimeMillis() - t0) / 1000.0)
    val futures = LinkedList<Future<Response>>()
    val client = ClientBuilder.newClient()

    for(i in 1..Pars.count) {
      try {
        //.header("Content-type", "application/json")
        val req = client.target(Pars.serverUrl).request()
        val fut = req.async().get()
        if (Pars.verbose)
          println("created future $i" + time())
        futures.add(fut)
      } catch (e: Exception) {
        println("error at $i: $e" + time())
        break
      }
    }
    println("getting futures, time: " + (System.currentTimeMillis() - t0)/1000.0)
    futures.forEach {
      val resp = it.get()!!
      if (Pars.verbose)
        println("" + resp.getStatus() + " " + resp.readEntity(String::class.java) + time())
      resp.close()
    }
    println("done" + time())
    exitProcess(0)
  }
}