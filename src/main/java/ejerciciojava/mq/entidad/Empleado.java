package ejerciciojava.mq.entidad;

import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Empleado {

	protected String nombre;
	protected Boolean ocupado = false;
	private static Logger LOG = LoggerFactory.getLogger(Empleado.class);

	/**
	 * Es la atencion de la llamada misma, la inicio y al final se colocan los
	 * estados en true y luego en false apra indicar si esta ocupado o no
	 * 
	 * @param cliente
	 * @return
	 */
	public String atenderLlamado(String cliente, String traza) {
		ocupado = true;
		try {
			System.out.println(cliente + " " + new Date());
			int tiempoEspera = this.generarDuracionLlamado();
			Thread.sleep(tiempoEspera);
			// System.out.println("Usted cliente " + cliente + " fue atendido
			// por " + nombre + " durante " + tiempoEspera + " " + new Date());
			LOG.info("Usted cliente " + cliente + " fue atendido por " + nombre + " durante " + tiempoEspera + " "
					+ new Date());
			return new String("Usted cliente " + cliente + " fue atendido por " + nombre + " durante " + tiempoEspera
					+ " " + new Date());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.ocupado = false;
		}

		return "";
	}

	/**
	 * Sirve para preguntar si el empleado esta ocupado con una llamada o no
	 * 
	 * @return
	 */
	public boolean estaOcupado() {
		return this.ocupado;
	}

	public Boolean getOcupado() {
		return ocupado;
	}

	public void setOcupado(Boolean ocupado) {
		this.ocupado = ocupado;
	}

	/**
	 * Genera un tiempo aleatorio para pausar el hilo y simular el tiempo de
	 * llamado entre 5 y 10 segundos
	 * 
	 * @return
	 */
	protected int generarDuracionLlamado() {
		Random r = new Random();
		int minimo = 5000;
		int maximo = 10000;
		int salida = r.nextInt(maximo - minimo) + minimo;
		return salida;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
