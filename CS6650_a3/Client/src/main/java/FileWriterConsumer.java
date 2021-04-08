import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * FileWriter Class
 */
public class FileWriterConsumer implements Runnable {
  private BlockingQueue<ResponseObject> buffer;

  /**
   * Constructor for FileWriter class
   */
  public FileWriterConsumer(){
    this.buffer = new LinkedBlockingQueue<>();
  }

  public void queueToWritingBuffer(ResponseObject responseObject) throws InterruptedException {
    this.buffer.put(responseObject);
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
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.csv"))) {
      writer.write("StartTime,RequestType,Latency,ResponseCode");
      writer.newLine();
      ResponseObject responseObject;
      while (!((responseObject = buffer.take()).getResponseCode() == 0)) {
        writer.write(responseObject.toString());
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
