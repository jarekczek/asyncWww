package jarek

import java.util.concurrent.{BlockingQueue, CountDownLatch, LinkedBlockingQueue}
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
    var t0 = System.currentTimeMillis()
    def time() = ", time: " + ((System.currentTimeMillis() - t0) / 1000.0)
    println(1)
    val con = Http.client.newService("localhost:8080")
    println("service ready" + time() + ", restarting time")
    t0 = System.currentTimeMillis()
    val c = 5000
    val url = "http://localhost:8080/waitAsync?seconds=120"
    val futures = (1 to c).map { (i: Int) =>
      con.apply(Request(Method.Get, url))
    }
    println("collecting" + time())
    Future.collect(futures)
    println("results" + time())
    futures.foreach { (fut: Future[Response]) =>
      val resp = fut.toJavaFuture.get()
      //println(resp.contentString)
    }
    println("done" + time())
  }

}
