package ejerciciojava.mq;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	private static String messageBrokerUrl;
	private static Logger LOG = LoggerFactory.getLogger(Server.class);

	static {
		messageBrokerUrl = "tcp://localhost:61616";
	}

	public Server() {
		try {
			/**
			 * Levanto un broker a mano para almacenar la cola de mensajeria
			 */
			LOG.info("LEVANTANDO ACTIVEMQ BROKER");
			BrokerService broker = new BrokerService();
			broker.setPersistent(false);
			broker.setUseJmx(false);
			broker.addConnector(messageBrokerUrl);
			broker.setUseShutdownHook(false);
			broker.start();
			LOG.info("BROKER ACTIVEMQ LEVANTADO");
			Object lock = new Object();
			synchronized (lock) {
				lock.wait();
			}
		} catch (Exception e) {
			// Handle the exception appropriately
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}