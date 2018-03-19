package ejerciciojava.mq.atencion;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejerciciojava.mq.entidad.Empleado;
import ejerciciojava.mq.util.PropertiesApp;
import ejerciciojava.mq.util.RecursosEmpresa;

/**
 * Esta clase es la encargada de procesar las llamadas y mandarlas a su atencion
 * segun haya disponibilidad
 * 
 * @author MAP53733
 *
 */
public class Dispatcher {

	private static int ackMode = Session.AUTO_ACKNOWLEDGE;
	private static String messageBrokerUrl = PropertiesApp.URL_BROKER;
	private static boolean transacted = false;
	private static String messageQueueName = "client.messages";
	private static MessageProducer replyProducer;
	private Session session = null;
	private static Logger LOG = LoggerFactory.getLogger(Dispatcher.class);

	public static void main(String[] args) {
		Dispatcher d = new Dispatcher();
		d.iniciarEscucha();
	}

	/**
	 * Este metodo es el principal par atender las llamadas, escucha la cola
	 * cada cierto tiempo segun tenga disponibilidad para atender mensajes no
	 * implemento un listener por que este lee directo de la cola cada vez que
	 * recibe un mensaje y si no tuviera empleados disponibles para ser atendido
	 * tendria que implementar mecanismos para reprocesar mensajes extraidos de
	 * la cola de forma accidental
	 */
	public void iniciarEscucha() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(transacted, ackMode);
			Destination adminQueue = session.createQueue(messageQueueName);

			/**
			 * Creo el productor destinado a responder al cliente
			 */
			replyProducer = session.createProducer(null);
			replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			/**
			 * Creo el lector para leer el dato de la cola.
			 */
			MessageConsumer consumer = session.createConsumer(adminQueue);
			Message mensaje = null;
			do {

				if (PropertiesApp.DESCARTAR_MENSAJES_OVERFLOW == true) {
					/**
					 * SI LA POLITICA ES QUE SE DESCARTEN LAS LLAMADAS CUANDO NO
					 * HAY QUIEN LAS ATIENDA, EL PARAMETRO EN PROPERTIES APP
					 * DEBE ESTAR SETEADO EN TRUE, PARA ESTE EJEMPLO LAS
					 * LLAMADAS SE ENCOLAN
					 */
					mensaje = consumer.receive(100L);
					Empleado empleadoDisponible = RecursosEmpresa.getRecursoLiberadorInmediato();
					if (empleadoDisponible != null) {
						empleadoDisponible.setOcupado(true);
						this.dispatchCall(empleadoDisponible, replyProducer, session, mensaje);
					}
				} else {
					/**
					 * Recupero un empleado disponible (operador, director o
					 * supervisor) este metodo va a limitar la cantidad de
					 * llamadas a ser atendidas debido a que usa un lista con
					 * estados
					 */
					Empleado empleadoDisponible = RecursosEmpresa.getRecursoLiberadorInmediato();
					if (empleadoDisponible != null) {
						empleadoDisponible.setOcupado(true);
						/**
						 * SI HAY UN EMPLEADO DISPONIBLE LEO UNA LLAMADA DE LA
						 * COLA
						 */
						mensaje = consumer.receive(1000L);
						this.dispatchCall(empleadoDisponible, replyProducer, session, mensaje);
					}
				}
				/**
				 * LOOP INFINITO PARA ATENDER LLAMADAS POR SIEMPRE
				 */
			} while (true);

		} catch (JMSException e) {
			LOG.error("PROBLEMAS DURANTE LA ATENCION DE LLAMADAS", e);
		} finally {
			try {
				/**
				 * FINALIZO LA CONEXION CUANDO ESTE TODOs LISTO
				 */
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Se podria llegar a ocupar en este metodo un threadPool, en este caso no
	 * lo hago debido a que el limitante esta puesto en el listado de personas
	 * disponibles y tambien por que si llego a limitar la ejecucion de hilos
	 * tambien produciria retrasos, aunque en la practica si se debe aplicar uno
	 * (cuyo tamaño debe ser tuneado), lo dejo simple para no complicar el
	 * codigo
	 * 
	 * @param empleado
	 * @param replyProducer
	 * @param session
	 * @param mensaje
	 * @throws JMSException
	 */
	public void dispatchCall(Empleado empleado, MessageProducer replyProducer, Session session, Message mensaje)
			throws JMSException {
		if (mensaje != null) {
			LOG.info(mensaje.getJMSCorrelationID() + " MENSAJE LEIDO DESDE " + mensaje + " " + new Date());
			/**
			 * Preparo el handler para ser enviado al hilo
			 */
			LlamadoHandler ll = new LlamadoHandler(empleado, replyProducer, session, mensaje);
			/**
			 * Mando a ejecutar el hilo
			 */
			AtencionHandler ah = new AtencionHandler(ll);
			ah.start();
		}

	}
}
