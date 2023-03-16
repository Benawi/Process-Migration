package framework;

import java.io.IOException;

public class SlaveMain {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Slave s1 = new Slave("SlaveOne", "10.194.119.40", 6001);
		
		s1.startServer();
		
	}


}
