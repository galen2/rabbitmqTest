package simple;
import com.rabbitmq.client.*;

import java.io.IOException;

import util.Tool;

public class Recv {

  private final static String QUEUE_NAME = "hello-tonyg";
  static Address[] addrArr = new Address[]{new Address("localhost", 5672)};
  public static void main(String[] argv) throws Exception {
	Connection connection =  Tool.getConnectionInstance();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//    channel.queueBind(QUEUE_NAME, "amq.rabbitmq.trace", "deliver.#");
//    channel.queueBind(QUEUE_NAME, "amq.rabbitmq.trace", "publish.#");
    
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
      }
    };
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }
}