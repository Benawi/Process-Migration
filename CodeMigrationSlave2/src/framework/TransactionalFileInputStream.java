package framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Serializable;

public class TransactionalFileInputStream extends InputStream implements Serializable{
	
	String str;
	int offset;
	
	public TransactionalFileInputStream(String string) {

		str = string;
		offset = 0;

	}

	@Override
	public int read() throws IOException {
		int ret;
		InputStream in = new FileInputStream(str);
		in.skip(offset);
		ret = in.read();
		offset++;
		in.close();
		return ret;
	}

}
