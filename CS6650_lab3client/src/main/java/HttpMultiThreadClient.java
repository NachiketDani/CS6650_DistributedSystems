import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpMultiThreadClient {

  private static final int NUM_THREADS = 100;



  public static void main(String[] args) throws InterruptedException {
    final MultiThreadRunnable client = new MultiThreadRunnable();
    CountDownLatch completed = new CountDownLatch((NUM_THREADS));

    Runnable thread = () -> {
      client.makeGetRequest();
      completed.countDown();
    };

    long tstart = System.currentTimeMillis();
    for (int i = 0; i < NUM_THREADS; i++) {
      new Thread(thread).start();
    }

    completed.await();

    long tend = System.currentTimeMillis();
    double duration = (double)(tstart - tend)/1000;
    System.out.println("The threads took" + duration + "seconds");
  }
}
