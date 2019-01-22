package jarek;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class DeferredController {

  private class Entry {
    public DeferredResult<String> result;
    public long readyTime;
  }

  public static Thread worker = null;
  public static Object workerLock = new Object();
  public static ConcurrentLinkedDeque<Entry> queue = new ConcurrentLinkedDeque<>();
  public static AtomicInteger requestsProcessed = new AtomicInteger(0);

  @RequestMapping(
    value = "/waitAsync", produces = MediaType.TEXT_HTML_VALUE)
  public DeferredResult<String> waitAsync(@RequestParam int seconds)
  {
    System.out.println("received request "
      + requestsProcessed.incrementAndGet() + " on " + Thread.currentThread());
    startWorkerMaybe();
    Entry entry = new Entry();
    entry.result = new DeferredResult<>();
    entry.readyTime = System.currentTimeMillis() + (1000L * seconds);
    queue.add(entry);
    return entry.result;
  }

  private void startWorkerMaybe() {
    synchronized(workerLock) {
      if (worker == null) {
        worker = new Thread(DeferredController::processQueue);
        worker.start();
      }
    }
  }

  public static void processQueue() {
    while (true) {
      Entry entry = queue.peekFirst();
      if (entry == null) {
        Helper.INSTANCE.sleepNoThrow(100);
      } else {
        if (System.currentTimeMillis() > entry.readyTime) {
          StringBuilder sb = new StringBuilder();
          sb.append("<p>Ok, waited.</p>");
          entry.result.setResult(sb.toString());
          queue.removeFirst();
        }
      }
    }

  }
}
