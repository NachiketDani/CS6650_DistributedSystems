import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to track http request success and failure data
 */
public class PurchaseTracker implements Runnable {
  private int successfulRequests;
  private int failedRequests;
  private int totalRequests;
  private BlockingQueue<Integer> buffer;

  /**
   * Constructor for the PurchaseTracker class
   */
  public PurchaseTracker() {
    this.successfulRequests = 0;
    this.failedRequests = 0;
    this.totalRequests = 0;
    this.buffer = new LinkedBlockingQueue<>();
  }

  /**
   * Getter method for successful request count
   * @return successful server request count
   */
  public int getSuccessfulRequests() {
    return successfulRequests;
  }

  /**
   * Getter method for failed request count
   * @return failed server request count
   */
  public int getFailedRequests() {
    return failedRequests;
  }

  /**
   * Getter method for total request count
   * @return total server requests processed
   */
  public int getTotalRequests() {
    return totalRequests;
  }

  /**
   * Method to queue server responses into a Buffer queue implemented in the
   * Producer - Consumer architecture
   * @param responseCode response received from server
   */
  public void queueToBuffer(Integer responseCode) {
    this.buffer.add(responseCode);
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    int postResult;
    while (true) {
      try {
        postResult = buffer.take();
        if (!(postResult == 0)) {
          if (postResult == 201) {
            successfulRequests++;
          } else {
            failedRequests++;
          }
          totalRequests++;
        } else {
          break;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
