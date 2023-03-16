package process;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;

import framework.MigratableProcess;
import framework.TransactionalFileInputStream;
import framework.TransactionalFileOutputStream;


/**
 * 
 * Appends given String to the end of each line
 *
 * Format: AppendMultiple <String> <source> <destination>
 */
public class AppendMultiple implements MigratableProcess{

	private String query;
	private TransactionalFileInputStream inFile;
	private TransactionalFileOutputStream outFile;
	
	private volatile boolean suspending;

	public AppendMultiple(String[] args) throws Exception{
		if (args.length != 3) {
			System.out
					.println("usage: AppendMultiple <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}
		
		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2], true);
		
	}
	
	
	@Override
	public void run() {
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		try {
			while (!suspending) {
				
				@SuppressWarnings("deprecation")
				String line = in.readLine();

				if (line == null) {
					break;
				}

				out.println(line.concat(query));

				// Make longer so that we can see it in process
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
		} catch (EOFException e) {
			// End of File
		} catch (IOException e) {
			System.out.println("AppendMultiple: Error: " + e);
		}

		suspending = false;
		
	}

	@Override
	public void suspend() {
		suspending = true;
		while (suspending)
			;
		
	}
	

}
