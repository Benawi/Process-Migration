package process;

import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

import framework.MigratableProcess;
import framework.TransactionalFileInputStream;
import framework.TransactionalFileOutputStream;

public class GrepProcess implements MigratableProcess {
	private TransactionalFileInputStream inFile;
	private TransactionalFileOutputStream outFile;
	private String query;

	private volatile boolean suspending;

	public GrepProcess(String args[]) throws Exception {
		if (args.length != 3) {
			System.out
					.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}

		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2], true);
	}

	public void run(){
		PrintStream out = new PrintStream(outFile);
		DataInputStream in = new DataInputStream(inFile);

		try {
			while (!suspending) {
				@SuppressWarnings("deprecation")
				String line = in.readLine();

				if (line == null) {
					System.out.println("done grep");
					break;
				}

				if (line.contains(query)) {
					//System.out.println(line);
					out.println(line);
					
				}

				// Make grep take longer so that we don't require extremely
				// large files for interesting results
				try {
					Thread.sleep(12000);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
		} catch (EOFException e) {
			// End of File
		} catch (IOException e) {
			System.out.println("GrepProcess: Error: " + e);
		}

		suspending = false;
	}

	public void suspend() {
		suspending = true;
		while (suspending)
			;
	}

}