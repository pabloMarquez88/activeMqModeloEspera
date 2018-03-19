package ejerciciojava.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ejerciciojava.mq.Server;

public class Caso10Test {

//	@BeforeClass
	public static void inicializar() {
		System.out.println("LEVANTAR SERVER ");
		Server server = new Server();
	}

//	@Test
	public void prueba10Cliente() {
//		System.out.println("SE EJECUTA EL CODIGO");
		Assert.assertFalse(false);
	}

//	@AfterClass
	public static void finalizarEjecucion() {
	}
}
