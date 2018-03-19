package ejerciciojava.mq.util;

/**
 * Esta clase sirve para setear parametros, lo ideal es implementar una lectura
 * de properties, pero para no complicar el codigo agragando codigo sencillo,
 * simplemente coloco constantes
 * 
 * @author MAP53733
 *
 */
public class PropertiesApp {

	/**
	 * Esta property indica la politica de atencion de llamadas para cuando el
	 * sistema este saturado, se puede decidir descartar las llamadas o ponerlas
	 * en espera, puesto en false, significa que estaran en espera.
	 */
	public static final Boolean DESCARTAR_MENSAJES_OVERFLOW = false;

	public static final String URL_BROKER = "tcp://localhost:61616";
}
