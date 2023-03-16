package process;

import java.io.EOFException;
import java.io.IOException;

import framework.MigratableProcess;

public class TestProcess implements MigratableProcess {

	private String word;
	private int count;
	private volatile boolean suspending;

	public TestProcess(String[] args) throws Exception {
		if (args.length != 1) {
			throw new Exception("Too many arguments.");
		}
		word = args[0];
		count = 0;
	}

	@Override
	public void run() {
		while (!suspending) {

			if (count > 10)
				break;

			try {
				System.out.println(word + count);
				count++;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore it
			}
		}
		suspending = false;
	}

	public void suspend() {
		suspending = true;
		while (suspending) {
			System.out.println("suspendin");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
