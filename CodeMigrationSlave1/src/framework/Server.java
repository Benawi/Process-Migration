package framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class Server {
	
	private ExecutorService threadPool;
	ServerSocket serverSocket;
	private String name;
	private int port;
	
	public Server(String name, int port) throws IOException{
		this.name = name;
		this.port = port;
		serverSocket = new ServerSocket(port);
		
	}
	
	public void startServer() {
		System.out.println("starting server");
		Thread server = new Thread(new Runnable() {
			public void run() {
				try {
					listenTask(serverSocket.accept());
				} catch (IOException e) {
					threadPool.shutdown();
				}
			}
		});

		server.start();

	}
	
	public int getPort(){
		return this.port;
	}
	
	public String getSName(){
		return this.name;
	}
	
	abstract public Runnable listenTask(Socket s);
	
	
}
