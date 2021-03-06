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
import kotlinx.coroutines.experimental.CancellationException
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
      for(i in 1..Pars.count) {
        val coroutine = async {
          try {
            if (Pars.verbose)
              println("inside async")
            val cli = HttpClient(CIO)
            if (Pars.verbose)
              println("client ready")
            val res = cli.get<String>(Pars.serverUrl)
            if (Pars.verbose)
              println("read msg " + res + ", " + res.length + ", " + time())
          } catch (e: Exception) {
            e.printStackTrace()
            System.exit(1)
          }
        }
        coroutines.add(coroutine)
      }
      coroutines.forEach {
        it.join()
      }
    }
    println("done" + time())
  }
}