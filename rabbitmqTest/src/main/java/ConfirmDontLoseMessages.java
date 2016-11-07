

 import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
 
 public class ConfirmDontLoseMessages {
     static int msgCount = 5;
     final static String QUEUE_NAME = "confirm-test";
     static ConnectionFactory connectionFactory;
     private static volatile SortedSet<Long> unconfirmedSet =
    		    Collections.synchronizedSortedSet(new TreeSet());
 
     public static void main(String[] args)
         throws IOException, InterruptedException
     {
         if (args.length > 0) {
                 msgCount = Integer.parseInt(args[0]);
         }
 
         connectionFactory = new ConnectionFactory();
         connectionFactory.setHost("192.168.32.126");
         connectionFactory.setUsername("tonyg");
         connectionFactory.setPassword("changeit");
 
         // Consume msgCount messages.
         (new Thread(new Consumer())).start();
         (new Thread(new Consumer())).start();
         // Publish msgCount messages and wait for confirms.
         (new Thread(new Publisher())).start();
     }
 
     @SuppressWarnings("ThrowablePrintedToSystemOut")
     static class Publisher implements Runnable {
         public void run() {
             try {
                 long startTime = System.currentTimeMillis();
 
                 // Setup
                 Connection conn = connectionFactory.newConnection();
                 Channel ch = conn.createChannel();
                 ch.queueDeclare(QUEUE_NAME, true, false, false, null);
                 ch.addConfirmListener(new ConfirmListener() {
                	    public void handleAck(long seqNo, boolean multiple) {
                	        if (multiple) {
                	            unconfirmedSet.headSet(seqNo+1).clear();
                	        } else {
                	            unconfirmedSet.remove(seqNo);
                	        }
                	    }
                	    public void handleNack(long seqNo, boolean multiple) {
                	    	System.out.println("handlerNck is raing");
                	        // handle the lost messages somehow
                	    }
                	});
                 
                 ch.confirmSelect();
 
                 // Publish
                 for (long i = 0; i < msgCount; ++i) {
                     unconfirmedSet.add(ch.getNextPublishSeqNo());
                     ch.basicPublish("", QUEUE_NAME,
                                     MessageProperties.PERSISTENT_BASIC,
                                     "nop".getBytes());
                 }
 
                 while (unconfirmedSet.size() > 0)
                     Thread.sleep(10);
                 
                 ch.waitForConfirmsOrDie();
 
                 // Cleanup
                 ch.queueDelete(QUEUE_NAME);
                 ch.close();
                 conn.close();
 
                 long endTime = System.currentTimeMillis();
                 System.out.printf("Test took %.fs\n",
                                   (float)(endTime - startTime)/1000);
             } catch (Throwable e) {
                 System.out.println("foobar :(");
                 System.out.print(e);
             }
         }
     }
 
     
     static class Publisher2 implements Runnable {
         public void run() {
             try {
                 long startTime = System.currentTimeMillis();
 
                 // Setup
                 Connection conn = connectionFactory.newConnection();
                 Channel ch = conn.createChannel();
                 ch.queueDeclare(QUEUE_NAME, true, false, false, null);
                 
                 // Publish 以事务的方式来操作
                 ch.txSelect();
                 for (long i = 0; i < msgCount; ++i) {
                     ch.basicPublish("", QUEUE_NAME,
                                     MessageProperties.PERSISTENT_BASIC,
                                     "nop".getBytes());
                     ch.txCommit();
                 }
 
                 // Cleanup
                 ch.queueDelete(QUEUE_NAME);
                 ch.close();
                 conn.close();
 
                 long endTime = System.currentTimeMillis();
                 System.out.printf("Test took %.fs\n",
                                   (float)(endTime - startTime)/1000);
             } catch (Throwable e) {
                 System.out.println("foobar :(");
                 System.out.print(e);
             }
         }
     }
     
     
     
     
     static class Consumer implements Runnable {
         public void run() {
             try {
                 // Setup
                 Connection conn = connectionFactory.newConnection();
                 Channel ch = conn.createChannel();
                 ch.queueDeclare(QUEUE_NAME, true, false, false, null);
 
                 // Consume
                 QueueingConsumer qc = new QueueingConsumer(ch);
                 ch.basicConsume(QUEUE_NAME, true, qc);
                 for (int i = 0; i < msgCount; ++i) {
                     qc.nextDelivery();
                 }
 
                 // Cleanup
                 ch.close();
                 conn.close();
             } catch (Throwable e) {
                 System.out.println("Whoosh!");
                 System.out.print(e);
             }
         }
     }
 }