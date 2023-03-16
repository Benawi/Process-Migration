package framework;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable{

	String str;
	boolean append;
	
	public TransactionalFileOutputStream(String string, boolean bool) {
		str = string;
		append = bool;
	}

	@Override
	public void write(int b) throws IOException {
		OutputStream out = new FileOutputStream(str, append);
		out.write(b);
		out.close();
	}

}
