package simple;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import util.Tool;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class Recv {

	private final static String QUEUE_NAME = "hello-tonyg";
	static Address[] addrArr = new Address[] { new Address("localhost", 5672) };

	public static void main(String[] argv) throws Exception {
//		consutmerQueueConsumerSimulation();
//		consutmerQueueConsumer();
	}
	
	
	public static void consutmerQueueConsumer() throws IOException{
		final Connection connection = Tool.getConnectionInstance();
		final Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, true, false, false,null);
		channel.basicQos(1);
		final QueueingConsumer consumer = new QueueingConsumer(channel); 
	    channel.basicConsume(QUEUE_NAME, false,consumer);
	    for (int i =0; i < 2; i ++){
	    	 Thread work = new Thread(new Runnable() {
	 			public void run() {
	 				while(true){
	 					try {
	 						Delivery delivery = consumer.nextDelivery();
	 						System.out.println(" [x] Received '" +delivery.getBody()
	 								+ "'"
	 								+ Thread.currentThread().getName());
	 						channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
	 					} catch (InterruptedException e) {
	 					} catch (IOException e) {
							e.printStackTrace();
						}
	 				}
	 			}
	 		});
	 	    work.start();
	    }
	}

	/**
	 *  一个channel就是一个轻量级连接，后面一个消息必须在前面消息消费掉之后才会推送给客户端；
	 *  这里有两方面情况 如果这个channel里面某些消息没有被ack，那么这个channel后面的 消息就会被阻塞
	 * @throws IOException
	 */
	public static void consutmerQueueConsumerSimulation() throws IOException{
		final Connection connection = Tool.getConnectionInstance();
		final Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, true, false, false,null);
		channel.basicQos(1);
		final LinkedBlockingQueue queue = new LinkedBlockingQueue<Delivery>();
		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag,
					Envelope envelope,
					AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				queue.add(envelope);
			}
		};
		final boolean autoAck = true;
	    channel.basicConsume(QUEUE_NAME, autoAck,consumer);
	    for (int i =0; i < 2; i ++){
	    	 Thread work = new Thread(new Runnable() {
	 			public void run() {
	 				while(true){
	 					try {
	 						Envelope envelope = (Envelope)queue.take();
	 						System.out.println(" [x] Received '" + envelope.getDeliveryTag()
	 								+ "'"
	 								+ Thread.currentThread().getName());
	 						if (!autoAck) {
	 							channel.basicAck(envelope.getDeliveryTag(),
 								false);
	 						}
	 					
	 					} catch (InterruptedException e) {
	 					} catch (IOException e) {
							e.printStackTrace();
						}
	 				}
	 			}
	 		});
	 	    work.start();
	    }
	}

	public static void consumerMutiThread() throws Exception {
		final Connection connection = Tool.getConnectionInstance();
		
		final Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, true, false, false,null);
		
		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag,
					Envelope envelope,
					AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + message
						+ "'"
						+ Thread.currentThread().getName());
				channel.basicAck(envelope.getDeliveryTag(),
						false);
			}
		};
		channel.basicConsume(QUEUE_NAME, false, consumer);

	}
}