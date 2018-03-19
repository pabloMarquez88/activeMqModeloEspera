package ejerciciojava.mq.cliente;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Esta clase se va a encargar de simular ser los clientes que buscan ser
 * atendidos crea una coneccion al broker y se la pasa a los hilos siguientes
 * como la idea es que las llamadas sean concurrentes, se ocupan hilos, por otro
 * lado, en este punto no estoy usando un thread pool debido a que no deseo
 * restringir el envio de llamadas a la cola
 * 
 * @author MAP53733
 *
 */
public class ClienteGenerador {

	private static Logger LOG = LoggerFactory.getLogger(ClienteGenerador.class);
	private List<ClienteHilo> clientes = new ArrayList<>();
	/**
	 * LA CANTIDAD DE CLIENTES QUE VAN A TRATAR DE SER ATENDIDOS
	 */
	private static final int CANTIDAD_CLIENTES = 300;

	public static void main(String[] args) {
		/**
		 * DISPARO EL GENERADOR DE CLIENTES
		 */
		ClienteGenerador cc = new ClienteGenerador();
		cc.iniciar();
	}

	public void iniciar() {
		try {
			/**
			 * creo la conexion con el broker
			 */
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
			Connection connection = connectionFactory.createConnection();
			/**
			 * creo 10 clientes y los ejecuto, los mando a llamar al broker
			 */
			LOG.info("SE EMPIEZAN A CREAR LOS CLIENTES");
			for (int i = 0; i < CANTIDAD_CLIENTES; i++) {
				ClienteHilo cliente = new ClienteHilo(connection, "Cliente " + i);
				cliente.run();
				clientes.add(cliente);
			}
			LOG.info("SE ENVIARON TODAS LAS LLAMADAS");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("ERROR EN LA CREACION DE CLIENTES", e);
		}

		/**
		 * itero hasta que este todo terminado y finalmente salgo del grupo de
		 * consumidores, no hay problema en los metodos de consulta debido a que
		 * la lista de threads almacenada no se modifica
		 */
		while (clientesTerminados() == false) {

		}
		System.exit(0);
	}

	/**
	 * Metodo para saber si los hilos se quedan esperando, en este caso estoy
	 * usando este codigo debido a que los listener de jms con la implementacion
	 * de activemq se quedan esperando mas mensajes a pesar de que se cierran
	 * las conexiones y consumers de las colas temporales
	 * 
	 * @return
	 */
	private Boolean clientesTerminados() {
		Boolean salida = true;
		for (ClienteHilo c : clientes) {
			if (!c.isRespuestaRecibida()) {
				salida = false;
				break;
			}
		}
		return salida;
	}
}
