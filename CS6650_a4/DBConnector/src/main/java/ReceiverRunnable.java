import com.google.gson.Gson;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


public class ReceiverRunnable implements Runnable{
  private static final String KAFKA_HOST = System.getProperty("KAFKA_HOST");
  private static final String KAFKA_PORT = System.getProperty("KAFKA_PORT");
  private static final String KAFKA_DB_TOPIC = "purchases";
  private static final int NUM_SHARDS = 3;
  private static IPurchaseDao[] purchaseDaos;
  private KafkaConsumer<String, String> consumer;

  public ReceiverRunnable () {
    purchaseDaos = new IPurchaseDao[NUM_SHARDS];
    purchaseDaos[0] = new PurchaseDao1();
    purchaseDaos[1] = new PurchaseDao1();
    purchaseDaos[2] = new PurchaseDao1();

    Properties props = new Properties();
    props.setProperty("bootstrap.servers", KAFKA_HOST + ":" + KAFKA_PORT);
    props.setProperty("group.id", "DBConsumer");
    props.setProperty("enable.auto.commit", "true");
    props.setProperty("auto.commit.interval.ms", "20");
    props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    consumer = new KafkaConsumer<>(props);
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
    consumer.subscribe(Arrays.asList(KAFKA_DB_TOPIC));
    boolean successfulInsert = false;
    String purchaseString = "";
    System.out.println("Kafka topic subscribed!");
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10));

      for (ConsumerRecord<String, String> record : records) {
        System.out.println("Record removed from kafka; line55 ReceiverRunnable");
        System.out.println(record.value());
        purchaseString = record.value();
        successfulInsert = makePurchase(purchaseString);
        if (!successfulInsert) {
          System.err.println("ERROR: Unable to insert " + purchaseString + " into the DB");
        }
      }
    }
  }

  private boolean makePurchase (String purchase) {
    PurchaseModel newPurchase = new Gson().fromJson(purchase, PurchaseModel.class);
    int key = hash(newPurchase);
//    System.out.println("ReceiverRunnable line 57: attempt to db write");
    IPurchaseDao purchaseDao = purchaseDaos[key];
    return purchaseDao.createPurchase(newPurchase);
  }

  private int hash(PurchaseModel purchase) {
    return (purchase.getCustomerID() + purchase.getStoreID()) % NUM_SHARDS ;
  }
}

