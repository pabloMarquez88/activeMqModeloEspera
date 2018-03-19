package ejerciciojava.mq.cliente;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClienteHandler implements MessageListener {

	private static Logger LOG = LoggerFactory.getLogger(ClienteHandler.class);

	private MessageConsumer responseConsumer;
	private Session session;
	private ClienteHilo padre;

	public ClienteHandler(MessageConsumer responseConsumer, Session session, ClienteHilo clienteHilo) {
		this.responseConsumer = responseConsumer;
		this.session = session;
		this.padre = clienteHilo;
	}

	public void onMessage(Message message) {
		LOG.info("SE RECIBIO RESPUESTA PARA EL CLIENTE " + padre.getNombreCliente());
		String messageText = null;
		try {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				messageText = textMessage.getText();
				LOG.info("Mensaje recibido para cliente " + padre.getNombreCliente() + "= " + messageText);
				/**
				 * Cierro las conexiones
				 */
				responseConsumer.close();
				session.close();
				/**
				 * Indico que la respuesta fue recibida
				 */
				padre.setRespuestaRecibida(true);
			}
		} catch (JMSException e) {
			LOG.error("ERROR PROCESANDO LA RESPUESTA PARA EL CLIENTE " + padre.getNombreCliente(), e);
		}

	}

}
