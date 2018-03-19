package ejerciciojava.mq.atencion;

/**
 * Este es el hilo para tomar las llamadas, tiene solo como atributo el objeto
 * para manejar la llamada
 * 
 * @author MAP53733
 *
 */
public class AtencionHandler extends Thread {

	private LlamadoHandler llamadoHandler;

	public AtencionHandler(LlamadoHandler lla) {
		this.llamadoHandler = lla;
	}

	@Override
	public void run() {
		super.run();
		this.llamadoHandler.atenderLlamado();
	}

}
