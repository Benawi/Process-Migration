package framework;

import java.io.IOException;

public class SlaveMain {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Slave s2 = new Slave("SlaveTwo", "10.194.111.36", 6001);
	
		s2.startServer();
		
	}


}
