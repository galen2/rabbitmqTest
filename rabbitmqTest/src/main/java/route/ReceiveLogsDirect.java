package route;
import com.rabbitmq.client.*;

import java.io.IOException;

public class ReceiveLogsDirect {

  private static final String EXCHANGE_NAME = "direct_logs";
  static Address[] addrArr = new Address[]{new Address("192.168.33.14", 5672)};
  public static void main(String[] argv) throws Exception {
//	  argv=new String[]{"error"};
	argv=new String[]{"info"};
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("tonyg");
    factory.setPassword("changeit");
    Connection connection = factory.newConnection(addrArr);
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
    String queueName = channel.queueDeclare().getQueue();

//    if (argv.length < 1){
//      System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
//      System.exit(1);
//    }

    /**
     * 同一个队列绑定多个routing-key
     */
    for(String severity : argv){
      channel.queueBind(queueName, EXCHANGE_NAME, severity);
    }
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
      }
    };
    channel.basicConsume(queueName, true, consumer);
  }
}

