package ejerciciojava.mq.atencion;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejerciciojava.mq.entidad.Empleado;

public class LlamadoHandler {

	private Empleado empleado;
	private MessageProducer replyProducer;
	private Session session = null;
	private Message message = null;
	private static Logger LOG = LoggerFactory.getLogger(LlamadoHandler.class);

	public LlamadoHandler(Empleado empleado, MessageProducer replyProducer, Session session, Message message) {
		this.empleado = empleado;
		this.replyProducer = replyProducer;
		this.session = session;
		this.message = message;
	}

	public void atenderLlamado() {
		LOG.info("INICIO SE ATIENDEN EL LLAMADO POR EL OPERADOR " + empleado.getNombre());
		try {
			/**
			 * CREO EL MENSAJE
			 */
			TextMessage response = session.createTextMessage();
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String messageText = txtMsg.getText();
				response.setText(empleado.atenderLlamado(messageText, message.getJMSCorrelationID()));
			}

			/**
			 * RECUPERO EL CORRELATION ID ENVIADO POR EL CLIENTE Y LO SETEO EN
			 * EL RESPONSE PARA QUE EL CLIENTE SEPA DE QUE MENSAJE ES LA
			 * RESPUESTA
			 */
			response.setJMSCorrelationID(message.getJMSCorrelationID());

			/**
			 * ENVIO EL MENSAJE A LA COLA TEMPORAL QUE INDICO EL CLIENTE DONDE
			 * DESEA RECIBIR LA RESPUESTA
			 */
			this.replyProducer.send(message.getJMSReplyTo(), response);
			LOG.info("FIN SE ATIENDEN EL LLAMADO POR EL OPERADOR " + empleado.getNombre());
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

}
