package jarek

import java.util.concurrent.{BlockingQueue, CountDownLatch, LinkedBlockingQueue, TimeUnit}
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BinaryOperator

import com.twitter.finagle.{Http, ListeningServer, Service, SimpleFilter}
import com.twitter.finagle.http.{Method, Request, Response}
import com.twitter.util.{Await, Duration, Future}
//import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpVersion, HttpResponseStatus, HttpRequest, HttpResponse}
import java.net.{SocketAddress, InetSocketAddress}
import com.twitter.finagle.builder.{Server, ServerBuilder}

object FinagleClient {
  
  def main(args: Array[String])
  {
    val server = System.getProperty("host", "localhost")
    val port = System.getProperty("port", "8080").toInt
    val delay = System.getProperty("delay", "10").toInt
    var t0 = System.currentTimeMillis()
    def time() = ", time: " + ((System.currentTimeMillis() - t0) / 1000.0)
    val url = "http://" + server + ":" + port + "/waitAsync?seconds=" + delay
    println("starting up")
    val con = Http.client
      .withTransport.connectTimeout(Duration(10, TimeUnit.SECONDS))
      .newService(server + ":" + port)
    println("service ready" + time())

    con.apply(Request(Method.Get, "http://" + server + ":" + port)).toJavaFuture.get()
    println("warmed up" + time() + ", restarting time")

    t0 = System.currentTimeMillis()
    val c = System.getProperty("count", "2000").toInt
    val futures = (1 to c).map { (i: Int) =>
      con.apply(Request(Method.Get, url))
    }
    println("collecting" + time())
    try {
      Future.collect(futures).toJavaFuture.get()
    } catch {
      case e: Exception => println("caught " + e)
    }
    println("results" + time())
    println("first result: " + futures.head.toJavaFuture.get())
    futures.foreach { (fut: Future[Response]) =>
      try {
        val resp = fut.toJavaFuture.get()
        //println(resp.contentString + time())
      } catch {
        case e: Exception => println("exception: " + e + time())
      }
    }
    println("done" + time())
  }

}
