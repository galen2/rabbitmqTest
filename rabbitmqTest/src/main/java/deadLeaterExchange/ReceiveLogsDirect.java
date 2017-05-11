package deadLeaterExchange;
import java.io.IOException;

import util.Tool;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ReceiveLogsDirect {

  public static void main(String[] argv) throws Exception {
	  Connection connection =  Tool.getConnectionInstance();
	  Channel channel = connection.createChannel();

    channel.queueDeclare("queue.dlq", false, false, false, null);
    channel.queueBind("queue.dlq", "deader-letter.exchange", "dead-routing-key");
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
      }
    };
    channel.basicConsume("queue.dlq", true, consumer);
  }
}

