package framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Slave {

	private String name;
	private MigratableProcess mp;
	private String hostname;
	private int port;
	private ServerSocket serverSocket;
	private ArrayList<ThreadPlus> threadTable;

	public static void main(String[] args) throws IOException {

		Slave s1 = new Slave("One", "127.0.0.1", 8000);
		s1.startServer();
	}

	public Slave(String name, String hostname, int port) throws IOException {
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		threadTable = new ArrayList<ThreadPlus>(20);
		serverSocket = new ServerSocket(port);
	}

	private void addThread(ThreadPlus t) {
		threadTable.add(t);
	}

	@SuppressWarnings("unused")
	private void removeThread(ThreadPlus t) throws ConcurrentModificationException {
		threadTable.remove(t);
	}
	
	public String getHostname() {
		return this.hostname;
	}

	public int getPort() {
		return this.port;
	}

	public String getSName() {
		return this.name;
	}

	public void startServer() throws IOException {
		System.out.println("Starting Slave node...");

		Thread server = new Thread(new Runnable() {
			public void run() {
				while (true) {
					// block until connection is made
					try {
						Socket clientSocket = serverSocket.accept();
						BufferedReader input = new BufferedReader(
								new InputStreamReader(clientSocket
										.getInputStream()));
						PrintWriter out = new PrintWriter(clientSocket
								.getOutputStream());
						String task = input.readLine();
						String[] taskArgs = task.split(" ");
						
						//Parsing through the various commands
						switch (taskArgs[0]) {
						case "launch":
							try {
								String processName = taskArgs[1];

								// tries to find process. 'process' is appended
								// because
								// all processes
								// should be located in the process package
								Class<?> process = Class.forName("process."
										+ processName);

								// make the arg list to pass on to the process
								String[] pArgs;
								// remove the task and the process name
								task = task.substring(taskArgs[0].length());
								task = task.trim();
								task = task.substring(processName.length());
								task = task.trim();

								pArgs = task.split(" ");

								// get constructor to make the process
								Constructor pCon = process
										.getConstructor(new Class[] { String[].class });

								// get runnable instance
								mp = (MigratableProcess) pCon
										.newInstance((Object) pArgs);

								// start thread
								ThreadPlus pThread = new ThreadPlus(mp);

								pThread.start();
								String pName = name + "_" + processName + "_"
										+ pThread.getId();

								pThread.setName(pName);
								addThread(pThread);

								out.println(pName);
								out.flush();


							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("No process named");
							} catch (ClassNotFoundException e) {
								System.out
										.println("Process not found. Try again");
							} catch (NoSuchMethodException e) {
								System.out
										.println("No constructor? Should never reach here.");
							} catch (SecurityException e) {
								System.out.println("security exception");
							} catch (InstantiationException e) {
								System.out
										.println("Unable to call process. Formatting issue");

							} catch (IllegalAccessException e) {
							} catch (IllegalArgumentException e) {
							} catch (InvocationTargetException e) {
								System.out
										.println("Oops! You did something that the process didn't like.");
							} catch (NoClassDefFoundError e) {
								System.out
										.println("Class not found. Please check the casing of your command.");
							} catch (Exception e) {

							}

							clientSocket.close();
							break;
						
						case "migrate":
							MigratableProcess toMigrate;
							boolean flag = false;
							for(ThreadPlus searchThreadPlus : threadTable) {
								if(searchThreadPlus.getName().equals(taskArgs[1])) {
									flag = true;
									searchThreadPlus.getMP().suspend();
									toMigrate = searchThreadPlus.getMP();
									ObjectOutputStream outObject = new ObjectOutputStream(clientSocket.getOutputStream());
									outObject.writeObject(toMigrate);
									break;
								}
							}
							if(!flag)
								out.println("Process not found.");
							out.flush();
							clientSocket.close();
							break;
							
						case "receive":
							String processName = taskArgs[1];
							ObjectInputStream recvObj = new ObjectInputStream(clientSocket.getInputStream());
							MigratableProcess process = (MigratableProcess) recvObj.readObject();
							
							// start thread
							ThreadPlus newThread = new ThreadPlus(process);
							newThread.start();
							String newPName = name + "_" + processName + "_"
									+ newThread.getId();

							newThread.setName(newPName);
							addThread(newThread);

							out.println(newPName);
							out.flush();
							break;
							
						case "kill":
							String pName = taskArgs[1];
							Set<Thread> threadList = Thread.getAllStackTraces().keySet();
							Thread[] thTable = threadList.toArray(new Thread[threadList.size()]);
							flag = false;
							for(Thread searchThread : thTable) {
								if(searchThread.getName().equals(pName)) {
									searchThread.stop();
									flag = true;
								}
							}
							if (flag)
								out.println(pName + " has been killed successfully.\n");
							else
								out.println("Error: Could not kill. " + pName
										+ "already executed or killed.\n");
							/*
							for(ThreadPlus searchThreadPlus : threadTable) {
								if(searchThreadPlus.getName().equals(pName))
									removeThread(searchThreadPlus);
							}
							*/
							out.flush();
							clientSocket.close();
							break;
						
						case "peek":
							Set<Thread> tList = Thread.getAllStackTraces().keySet();
							Thread[] ttable = tList.toArray(new Thread[tList.size()]);
							ArrayList<String> tt = new ArrayList<String>(tList.size());
							String[] sTable;
							for(Thread t : ttable){
								if(t.getName().startsWith(name)){
									tt.add(t.getName());
								}
							}
							sTable = tt.toArray(new String[tt.size()]);
							
							ObjectOutputStream outobj = new ObjectOutputStream(clientSocket.getOutputStream());
							outobj.writeObject(sTable);
							clientSocket.close();
							break;
						
						default:
							out.println("Invalid command used. Try again.\n");
						
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

				}
			}
		});

		server.start();
	}
	
	public Thread[] peek(){
	return null;	
	}
	
}
