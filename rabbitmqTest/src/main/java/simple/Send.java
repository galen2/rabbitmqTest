package simple;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import util.Tool;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {

  private final static String QUEUE_NAME = "hello-tonyg";
  static Address[] addrArr = new Address[]{new Address("localhost", 5672)};
  public static void main(String[] argv) throws Exception {
	Connection connection =  Tool.getConnectionInstance();
	Channel channel = connection.createChannel();

	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	while(true){
		System.out.println("请输入内容：");
		String message = reader.readLine();
		if(message.equals("bye")){
			break;
		}
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
	}
    channel.close();
    connection.close();
  }
}