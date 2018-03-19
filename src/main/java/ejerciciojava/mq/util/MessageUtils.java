package ejerciciojava.mq.util;

import java.util.Random;

public class MessageUtils {

	/**
	 * Sirve para generar el correlationId
	 * 
	 * @return
	 */
	public static String createRandomString() {
		Random random = new Random(System.currentTimeMillis());
		long randomLong = random.nextLong();
		return Long.toHexString(randomLong);
	}
}
