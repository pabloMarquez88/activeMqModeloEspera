package ejerciciojava.mq.util;

import java.util.ArrayList;
import java.util.List;

import ejerciciojava.mq.entidad.Director;
import ejerciciojava.mq.entidad.Empleado;
import ejerciciojava.mq.entidad.Operador;
import ejerciciojava.mq.entidad.Supervisor;

public class RecursosEmpresa {
	/**
	 * Ls lista con los recursos que tiene la empresa, todos heredan de empleado
	 * y luego se particularizan para cada rol.
	 */
	public static List<Empleado> RECURSOS = new ArrayList<>();
	static {
		Operador o1 = new Operador("Operador 1");
		Operador o2 = new Operador("Operador 2");
		Operador o3 = new Operador("Operador 3");
		Operador o4 = new Operador("Operador 4");
		Operador o5 = new Operador("Operador 5");
		Operador o6 = new Operador("Operador 6");
		Operador o7 = new Operador("Operador 7");
		Supervisor s1 = new Supervisor("Supervisor 1");
		Supervisor s2 = new Supervisor("Supervisor 2");
		Director d1 = new Director("Director 1");

		RECURSOS.add(o1);
		RECURSOS.add(o2);
		RECURSOS.add(o3);
		RECURSOS.add(o4);
		RECURSOS.add(o5);
		RECURSOS.add(o6);
		RECURSOS.add(o7);
		RECURSOS.add(s1);
		RECURSOS.add(s2);
		RECURSOS.add(d1);
	}

	/**
	 * Este metodo retorna el recurso liberado mas inmeadiato, como estoy usando
	 * una lista verifico primero que los operarios sean los primeros
	 * desocupados, luego los supervisores y finalmente los directores, el orden
	 * de consulta va a estar dado por el orden de que fueron insertados en el
	 * array si fuera necesario agregar mas empleados, convendria implementar un
	 * comparator para reordenar la lista luego de cada insercion
	 * 
	 * @return
	 */
	public static Empleado getRecursoLiberadorInmediato() {
		for (Empleado e : RECURSOS) {
			if (!e.estaOcupado()) {
				return e;
			}
		}
		return null;
	}
}
