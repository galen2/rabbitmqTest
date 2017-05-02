package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Tool {
//	  static Address[] addrArr = new Address[]{new Address("localhost", 5672),new Address("192.168.32.125", 5672)};
	private static Address[] addrArr = new Address[]{new Address("localhost", 5672)};
	public static final Connection getConnectionInstance(){
		try {
			
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setUsername("tonyg");
		    factory.setPassword("changeit");
		    factory.setVirtualHost("/tonyg");
		/*	ExecutorService service = Executors.newFixedThreadPool(5);  
			factory.setSharedExecutor(service);*/
			Connection connection = factory.newConnection(addrArr);
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		Connection connection  = getConnectionInstance();
		for(int i =0 ;i< 10;i++){
			Channel channel = connection.createChannel();
			System.out.println(channel.hashCode());
		}
	}
}
