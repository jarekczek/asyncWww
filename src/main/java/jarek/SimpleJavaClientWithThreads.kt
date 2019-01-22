package jarek

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.Socket
import java.net.URL
import java.net.URLConnection
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

object SimpleJavaClientWithThreads {
  val latch = CountDownLatch(1)
  var finishLatch: CountDownLatch? = null
  val sem1 = Semaphore(0)
  val sem2 = Semaphore(0)
  val successCount = AtomicInteger(0)
  val requestsDone = AtomicInteger(0)
  val t0 = System.currentTimeMillis()

  @JvmStatic
  fun main(args: Array<String>) {
    val connections = Collections.synchronizedList(mutableListOf<URLConnection>())
    val threadCount = 5000
    for(i in 1..threadCount) {
      Thread({
        var c: URLConnection? = null
        try {
          c = URL("http://localhost:8080/waitAsync?seconds=120").openConnection()!!
          c.connect()
          connections.add(c)
          if (i < 40000)
            println("connection $i ok")
          successCount.incrementAndGet()
        } catch (e: Exception) {
          //println(e)
          c = null
        }

        sem1.release()
        //latch.await()

        if (c != null) {
          val bytes = c.getInputStream().readBytes()
          if (requestsDone.incrementAndGet() < 10000) {
            println("read bytes " + bytes.size + ", time: " + (System.currentTimeMillis() - t0)/1000.0)
          }
        }

        sem2.release()
      }).start()
    }
    sem1.acquire(threadCount)
    latch.countDown()
    println("waiting for bye")
    sem2.acquire(threadCount)
    println("threads $threadCount, success: $successCount")
    println("done")
  }
}