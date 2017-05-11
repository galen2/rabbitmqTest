package PublishSubscribe;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import util.Tool;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

/**
 * 1、发布消息给所有的消费者，每一个消费者消费的消息内容都是一样的
 * 
 *
 */
public class EmitLog {

  private static final String EXCHANGE_NAME = "cust-logs";
//  private static final String EXCHANGE_NAME = "amq.fanout2";
  
  static Address[] addrArr = new Address[]{new Address("localhost", 5672),new Address("192.168.32.125", 5672)};

  public static void main(String[] argv) throws Exception {
    Connection connection =  Tool.getConnectionInstance();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");


    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	while(true){
		System.out.print("请输入内容：");
		String message = reader.readLine();
		if(message.equals("bye")){
			break;
		}
	    channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
	    System.out.println(" [x] Sent '" + message + "'");
	}

    channel.close();
    connection.close();
  }

  private static String getMessage(String[] strings){
    if (strings.length < 1)
    	    return "info: Hello World!";
    return joinStrings(strings, " ");
  }

  private static String joinStrings(String[] strings, String delimiter) {
    int length = strings.length;
    if (length == 0) return "";
    StringBuilder words = new StringBuilder(strings[0]);
    for (int i = 1; i < length; i++) {
        words.append(delimiter).append(strings[i]);
    }
    return words.toString();
  }
}