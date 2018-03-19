package ejerciciojava.mq.cliente;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejerciciojava.mq.util.MessageUtils;

public class ClienteHilo extends Thread {

	private Connection connection;
	private String nombreCliente;

	private static final Boolean transacted = false;
	private static final Integer acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
	private static final String NOMBRE_COLA = "client.messages";
	private MessageProducer producer;

	private static Logger LOG = LoggerFactory.getLogger(ClienteHilo.class);
	private boolean respuestaRecibida = false;

	public boolean isRespuestaRecibida() {
		return respuestaRecibida;
	}

	public void setRespuestaRecibida(boolean respuestaRecibida) {
		this.respuestaRecibida = respuestaRecibida;
	}

	public ClienteHilo(Connection connection, String nombreCliente) {
		this.connection = connection;
		this.nombreCliente = nombreCliente;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	@Override
	public void run() {
		super.run();
		try {
			LOG.info("SE INICIA EL CLIENTE " + nombreCliente);
			/**
			 * Inicio la conexion
			 */
			connection.start();
			/**
			 * creo una session
			 */
			Session session = connection.createSession(transacted, acknowledgeMode);
			/**
			 * accedo a la cola donde se enviaran los mensaje
			 */
			Destination adminQueue = session.createQueue(NOMBRE_COLA);

			/**
			 * Creo un productor de mensajes
			 */
			this.producer = session.createProducer(adminQueue);
			/**
			 * Indico que los mensajes no se guardaran de este lado
			 */
			this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			/**
			 * creo una cola temporaria donde el cliente esperará la respuesta
			 */
			Destination tempDest = session.createTemporaryQueue();
			MessageConsumer responseConsumer = session.createConsumer(tempDest);
			/**
			 * Indico cual sera el handler para cueando la respuesta sea
			 * recibida
			 */
			ClienteHandler ccc = new ClienteHandler(responseConsumer, session, this);
			/**
			 * se lo asigno a la cola temporaria
			 */
			responseConsumer.setMessageListener(ccc);

			/**
			 * Creo el mensaje a ser enviado en la llamada
			 */
			TextMessage txtMessage = session.createTextMessage();
			txtMessage.setText(nombreCliente);
			/**
			 * Se indica a donde quiero la respuesta, es una implementacion
			 * sencilla de un patron similar a push
			 */
			txtMessage.setJMSReplyTo(tempDest);
			/**
			 * Seteo un correlationId, es comun usarlo en ESBs con el objeto de
			 * tracear el mensaje, mas si hay orquestamiento
			 */
			String correlationId = MessageUtils.createRandomString();
			txtMessage.setJMSCorrelationID(correlationId);
			/**
			 * Envio el mensaje
			 */
			this.producer.send(txtMessage);
			LOG.info("CLIENTE" + nombreCliente + " LISTO Y ESPERANDO");

		} catch (JMSException e) {
			e.printStackTrace();
			LOG.error("ERROR EN EL CLIENTE " + nombreCliente, e);
		}
	}

}
