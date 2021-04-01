import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

public class Receiver {
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";
  private static final String HOST = "localhost";

  private static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();


    channel.exchangeDeclare(EXCHANGE_NAME,EXCHANGE_TYPE);
    String purchasesQueue = channel.queueDeclare().getQueue();
    channel.queueBind(purchasesQueue, EXCHANGE_NAME, "");

    System.out.println(" [*] Waiting for purchase data. To exit press CTRL+C");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String purchase = new String(delivery.getBody(), "UTF-8");
      System.out.println(" [x] Received '" + purchase + "'");
      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    };
    channel.basicConsume(purchasesQueue, true, deliverCallback, consumerTag -> { });
  }
}
