import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class to track http request success and failure data
 */
public class LatencyConsumer implements Runnable {
  private final int MS_CONVERSION_FACTOR = 1000;
  private final int MAX_LATENCY = 10000;
  private final int BUCKET_WIDTH = 5;
  private final int BUCKET_COUNT = MAX_LATENCY/BUCKET_WIDTH;
  private final double MEDIAN_PERCENTILE = 0.5;

  private int successfulRequests;
  private int failedRequests;
  private int totalRequests;
  private BlockingQueue<ResponseObject> buffer;
  private double meanLatency;
  private double sumLatencies;
  private long[] latencies;
  private double medianLatency;

  /**
   * Constructor for the LatencyConsumer class
   */
  public LatencyConsumer() {
    this.successfulRequests = 0;
    this.failedRequests = 0;
    this.totalRequests = 0;
    this.buffer = new LinkedBlockingQueue<>();
    this.meanLatency = 0;
    this.sumLatencies = 0;
    this.medianLatency = 0;
    this.latencies = new long[BUCKET_COUNT];
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
   * Getter method for mean latency
   * @return mean latency
   */
  public double getMeanLatency() {
    this.meanLatency = (this.sumLatencies/this.totalRequests);
    return this.meanLatency;
  }

  /**
   * Method to queue server responses into a Buffer queue implemented in the
   * Producer - Consumer architecture
   * @param responseObject response object created based on response elements
   */
  public void queueToBuffer(ResponseObject responseObject) {
    this.buffer.add(responseObject);
  }


  public double calculatePercentileLatency(double percentile) {
    double percentileLatency = percentileCalculator(percentile);
    return percentileLatency;

  }

  private double percentileCalculator(double percentile) {
    int stopCount = (int) (this.totalRequests * percentile);
    int bucketNumber = 0;
    int curr = 0;
    while(curr < stopCount) {
      curr += latencies[bucketNumber];
      bucketNumber++;
    }
    int bucketLowerLimit = bucketNumber * BUCKET_WIDTH;
    int bucketUpperLimit = (bucketLowerLimit + BUCKET_WIDTH)-1;
    return (double)(bucketLowerLimit + bucketUpperLimit)/2;
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
    ResponseObject postResult;
    while (true) {
      try {
        postResult = buffer.take();
        if (postResult.getResponseCode() != 0) {
          if (postResult.getResponseCode() == 201) {
            successfulRequests++;
            sumLatencies += postResult.getLatency();
          } else {
            failedRequests++;
          }
          // Calculate bucket for the latency
          int index = (int)(postResult.getLatency()/BUCKET_WIDTH);
          // If latency is greater than max, put in last bucket
          if (index > latencies.length-1) {
            index = latencies.length-1;
          }
          latencies[index] += 1;
          totalRequests++;
        } else {
          break;
        }
        this.medianLatency = calculatePercentileLatency(MEDIAN_PERCENTILE);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Getter method for median latency
   * @return median latency
   */
  public double getMedianLatency() {
    return this.medianLatency;
  }
}
