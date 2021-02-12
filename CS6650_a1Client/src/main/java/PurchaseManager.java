import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.PurchaseApi;
import java.io.File;
import java.time.LocalDate;
import java.util.*;
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


    PurchaseTracker purchaseTracker = new PurchaseTracker();

    CountDownLatch phase2Latch = new CountDownLatch(1);
    CountDownLatch phase3Latch = new CountDownLatch(1);

    long tStart = System.currentTimeMillis();

    Thread purchaseTrackerThread = new Thread(purchaseTracker);
    purchaseTrackerThread.start();

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
          itemsPerPurchase, phase2Latch, phase3Latch, purchaseTracker));
      eastThreads[i].start();
    }

    phase2Latch.await();

    // Central Phase Stores
    Thread[] centralThreads = new Thread[numStores/4];
    for (int i = 0; i < numStores/4; i++) {
      centralThreads[i] = new Thread(new StoreThreadRunnable(i+1, serverAddress, date,
          customersPerStore, maxItemId, numPurchases,
          itemsPerPurchase, phase2Latch, phase3Latch, purchaseTracker));
      centralThreads[i].start();
    }

    phase3Latch.await();

    // West Phase Stores
    Thread[] westThreads = new Thread[numStores / 2];
    for (int i = 0; i < numStores/2; i++) {
      westThreads[i] = new Thread(new StoreThreadRunnable(i+1, serverAddress, date,
          customersPerStore, maxItemId, numPurchases,
          itemsPerPurchase, phase2Latch, phase3Latch, purchaseTracker));
      westThreads[i].start();
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

    long tEnd = System.currentTimeMillis();

    // Queue response codes from purchases to buffer
    purchaseTracker.queueToBuffer(0);
    purchaseTrackerThread.join();

    // Wall time calculation
    double duration = (double)(tEnd - tStart)/1000;

    // Print out result report
    System.out.println("----------------------------");
    System.out.println("RESULT REPORT:");
    System.out.println("----------------------------");
    System.out.println("There were " + purchaseTracker.getSuccessfulRequests() +
        " successful purchases posted!");
    System.out.println(purchaseTracker.getFailedRequests() +
        " requests failed to post.");
    System.out.println("A total of " + purchaseTracker.getTotalRequests() + " requests were made.");
    System.out.println("Processing purchases for the stores took " + duration + " seconds (Wall Time)");
    System.out.println("----------------------------");
  }
}
