package rabbitmqTest;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class Client {

	 private static volatile SortedSet<Integer> unconfirmedSet =
 		    Collections.synchronizedSortedSet(new TreeSet());
	 @Test
	public void tt(){
		unconfirmedSet.add(1);
		unconfirmedSet.add(2);
		unconfirmedSet.add(3);
		unconfirmedSet.add(4);
		unconfirmedSet.add(5);

//		unconfirmedSet.headSet(3).clear();
		
		System.out.println(unconfirmedSet.size());
		System.out.println(unconfirmedSet);

		
	}
}
