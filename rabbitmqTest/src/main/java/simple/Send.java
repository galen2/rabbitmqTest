package simple;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class Send {

  private final static String QUEUE_NAME = "helloTwo";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("192.168.33.14");
//    factory.setHost("192.168.32.125");
    factory.setUsername("tonyg");
    factory.setPassword("changeit");
    Connection connection = factory.newConnection();
  
    Channel channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    String message = "Hello World!";
    System.out.println(" [x] Sent '" + message + "'");
    channel.queueDelete(QUEUE_NAME);

    channel.close();
    connection.close();
  }
}