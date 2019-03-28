package jarek

import io.ktor.util.decodeString
import io.ktor.util.moveToByteArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.*

object SocketClient {
  @JvmStatic
  fun main(args: Array<String>) {
    val sockets = LinkedList<SocketChannel>()
    val t0 = System.currentTimeMillis()
    val verbose = Pars.verbose
    fun time() = ", time: " + ((System.currentTimeMillis() - t0) / 1000.0)
    for(i in 1..Pars.count) {
      val s = SocketChannel.open(InetSocketAddress(Pars.host, Pars.port))
      if (verbose && i <= 5)
        println("connection ok on port " + s.localAddress + ", blocking: " + s.isBlocking)
      sockets.add(s)

      val req = "GET /waitAsync?seconds=${Pars.delay} HTTP/1.1\n" +
        "Host: ${Pars.host}\n" +
        "Accept: text/plain, text/html\n" +
        "\n"
      s.configureBlocking(true)
      s.write(ByteBuffer.wrap(req.toByteArray()))
    }

    println("reading sockets" + time())
    var allDisconnected = false
    var done = 0
    while(!allDisconnected) {
      allDisconnected = true
      val it = sockets.iterator()
      while (it.hasNext()) {
        val s = it.next()
        //if (verbose) println("connected: " + s.isConnected)
        if (s.isConnected) {
          s.configureBlocking(false)
          val buf = java.nio.ByteBuffer.allocate(1000)
          val bytesRead = s.read(buf)
          if (bytesRead > 0) {
            buf.position(0)
            s.close()
            it.remove()
            done++
            val data = buf.decodeString()
            val ok = data.startsWith("HTTP/1.1 200")
            if (verbose || !ok) {
              println("bytes read: $bytesRead, done items: $done"
                + ", time: " + (System.currentTimeMillis() - t0) / 1000.0)
              println(data)
            }
          } else {
            allDisconnected = false
          }
        }
        //val br = BufferedReader(InputStreamReader(s.getInputStream()))
        //println(br.readLine() + ", time: " + (System.currentTimeMillis() - t0)/1000.0)
      }
      //Helper.sleepNoThrow(10)
    }

    println("ok, " + ", time: " + (System.currentTimeMillis() - t0)/1000.0)
  }
}