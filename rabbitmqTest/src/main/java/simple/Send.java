package simple;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {

  private final static String QUEUE_NAME = "helloTwo";
//  static Address[] addrArr = new Address[]{new Address("192.168.33.14", 5672)};
  static Address[] addrArr = new Address[]{new Address("192.168.33.14", 5672),new Address("192.168.32.125", 5672)};

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("tonyg");
    factory.setPassword("changeit");
    Connection connection = factory.newConnection(addrArr);
    Channel channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    
//    channel.queueBind(QUEUE_NAME, "amq.rabbitmq.trace", "deliver.#");
//    channel.queueBind(QUEUE_NAME, "amq.rabbitmq.trace", "publish.#");
    
    String message = "Hello World trace 33333 !";
    channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + message + "'");

    channel.close();
    connection.close();
  }
}