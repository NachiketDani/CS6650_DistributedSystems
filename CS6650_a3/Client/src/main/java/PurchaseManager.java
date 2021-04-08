import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 *  Main controller class that addresses command line data collection and
 *  execution of the main method
 */
public class PurchaseManager {

  /**
   * Main Method
   * @param args Command line arguments
   * @throws InterruptedException
   * @throws InvalidArgumentsException
   */
  public static void main(String[] args) throws InterruptedException, InvalidArgumentsException {
    CmdLineParser cmdParser = new CmdLineParser();
    CmdLineData cmdLineData = cmdParser.parseInput(args);
    HashMap<String, Integer> options = cmdLineData.getCommandLineData();

    CountDownLatch phase2Latch = new CountDownLatch(1);
    CountDownLatch phase3Latch = new CountDownLatch(1);

    // Capture Start for Wall time
    long tStart = System.currentTimeMillis();

    LatencyConsumer latencyConsumer = new LatencyConsumer();
    Thread purchaseTrackerThread = new Thread(latencyConsumer);

    FileWriterConsumer fileWriterConsumer = new FileWriterConsumer();
    Thread fileWriterThread = new Thread(fileWriterConsumer);

    purchaseTrackerThread.start();
    fileWriterThread.start();

    // Create smaller variables to pass to threads
    Integer numStores = options.get("maxStores");
    Integer date = options.get("date");
    Integer customersPerStore = options.get("customersPerStore");
    Integer maxItemId = options.get("maxItemId");
    Integer numPurchases = options.get("numPurchases");
    Integer itemsPerPurchase = options.get("itemsPerPurchase");
    String serverAddress = cmdLineData.getServerPath();

    // Configuration Settings
    System.out.println("Configuration settings:");
    System.out.println(options);
    System.out.println("Server address queried:");
    System.out.println(cmdLineData.getServerPath());


    // East Phase Stores
    Thread[] eastThreads = new Thread[numStores/4];
    for (int i = 0; i < numStores/4; i++) {
      eastThreads[i] = new Thread(new StoreThreadRunnable(i+1, serverAddress, date,
          customersPerStore, maxItemId, numPurchases,
          itemsPerPurchase, phase2Latch, phase3Latch, latencyConsumer,
          fileWriterConsumer));
      eastThreads[i].start();
    }

    phase2Latch.await();

    // Central Phase Stores
    Thread[] centralThreads = new Thread[numStores/4];
    int j = 0;
    for (int i = numStores/4; i < numStores/2; i++) {
      centralThreads[j] = new Thread(new StoreThreadRunnable(i+1, serverAddress, date,
          customersPerStore, maxItemId, numPurchases,
          itemsPerPurchase, phase2Latch, phase3Latch, latencyConsumer,
          fileWriterConsumer));
      centralThreads[j].start();
      j++;
    }

    phase3Latch.await();

    // West Phase Stores
    Thread[] westThreads = new Thread[numStores / 2];
    int k = 0;
    for (int i = numStores/2; i < numStores; i++) {
      westThreads[k] = new Thread(new StoreThreadRunnable(i+1, serverAddress, date,
          customersPerStore, maxItemId, numPurchases,
          itemsPerPurchase, phase2Latch, phase3Latch, latencyConsumer,
          fileWriterConsumer));
      westThreads[k].start();
      k++;
    }

    // Join all threads
    for (int i = 0; i < numStores/4; i++) {
      eastThreads[i].join();
    }
    for (int i = 0; i < numStores/4; i++) {
      centralThreads[i].join();
    }
    for (int i = 0; i < numStores/2; i++) {
      westThreads[i].join();
    }

    // Capture end time for wall time
    long tEnd = System.currentTimeMillis();

    // Queue response codes from purchases to buffer
    ResponseObject endResponses = new ResponseObject();
    endResponses.setResponseCode(0);
    endResponses.setTStart(0L);
    endResponses.setTEnd(0L);
    endResponses.setResponseType("END");

    latencyConsumer.queueToBuffer(endResponses);
    fileWriterConsumer.queueToWritingBuffer(endResponses);
    purchaseTrackerThread.join();
    fileWriterThread.join();

    // Wall time calculation
    double duration = (double)(tEnd - tStart)/1000;
    double meanLatency = latencyConsumer.getMeanLatency();
    double medianLatency = latencyConsumer.getMedianLatency();
    double throughput = (latencyConsumer.getTotalRequests()/duration);
    double percentile99 = latencyConsumer.calculatePercentileLatency(0.99);

    // Print out result report
    System.out.println("----------------------------");
    System.out.println("RESULT REPORT:");
    System.out.println("----------------------------");
    System.out.println("There were " + latencyConsumer.getSuccessfulRequests() +
        " successful purchases posted!");
    System.out.println(latencyConsumer.getFailedRequests() +
        " requests failed to post.");
    System.out.println("A total of " + latencyConsumer.getTotalRequests() + " requests were made.");
    System.out.println("The requests were processed in " + duration + "seconds. (Wall Time)");
    System.out.println("Throughput: "+ throughput + " requests/second");
    System.out.println("The average latency was " + meanLatency + " seconds.");
    System.out.println("The median latency was " + medianLatency + " seconds.");
    System.out.println("99% of the requests took "+ percentile99 + " seconds");
    System.out.println("----------------------------");
  }
}
