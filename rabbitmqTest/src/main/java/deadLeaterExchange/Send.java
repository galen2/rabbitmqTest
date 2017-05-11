package deadLeaterExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import util.Tool;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {
	  static String QUEUE_NAME = "myqueue-deader-lettter";
	public static void main(String[] argsw) throws Exception {
		 Connection connection =  Tool.getConnectionInstance();
	    Channel channel = connection.createChannel();
	    
	    channel.exchangeDeclare("deader-letter.exchange", "direct");
	    Map<String, Object> args = new HashMap<String, Object>();
	    args.put("x-dead-letter-exchange", "deader-letter.exchange");
	    args.put("x-dead-letter-routing-key", "dead-routing-key");
	    args.put("x-message-ttl", 7000);
	    channel.queueDeclare("test.queue.dead.letter", false, false, false, args);
	    
        channel.queueBind("test.queue.dead.letter", "amq.direct", "test");
        
        
	    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			System.out.println("请输入内容：");
			String message = reader.readLine();
			if(message.equals("bye")){
				break;
			}
			channel.basicPublish("amq.direct", "test", null, message.getBytes("UTF-8"));
		}
	}
}
