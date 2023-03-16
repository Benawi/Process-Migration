package framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Master {

    private String name;
    private int port;
    private MigratableProcess mp;
    private LinkedList<Slave> slaveList;
    private int currentSlave;
    private ArrayList<String> pNameTable;

    public Master(String name, int port) throws IOException {
        this.name = name;
        this.port = port;
        slaveList = new LinkedList<Slave>();
        currentSlave = 0;
        pNameTable = new ArrayList<String>(20);
    }

    public void addSlave(Slave s) {
        slaveList.add(s);

    }

    private void addPname(String s) {
        pNameTable.add(s);
    }

    public void start() throws IOException, ClassNotFoundException {

        Slave currentSlave;
        Socket s;
        String dst;
        String pName;
        boolean flag;

        System.out.println("Started Master node1. \n");

        PrintWriter out;
        BufferedReader input;

        while (true) {

            System.out.print("Command: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            String line = in.readLine();

            String[] lineSplit = line.split(" ");
            try {
                switch (lineSplit[0]) {
                    case "launch":
                        currentSlave = slaveList.poll();
                        s = new Socket(currentSlave.getHostname(), currentSlave.getPort());
                        out = new PrintWriter(s.getOutputStream(), true);
                        input = new BufferedReader(
                                new InputStreamReader(s.getInputStream()));
                        out.println(line);
                        pName = input.readLine();
                        System.out.println("Created a new process: " + pName + "\n");
                        slaveList.add(currentSlave);
                        break;

                    case "migrate":
                        pName = lineSplit[1];
                        dst = lineSplit[2];
                        System.out.println("Migrating thread " + pName + " to " + dst + "...");
                        Slave dstSlave = null;
                        String[] pNameSplit = pName.split("_");

                        //Check if destination slave exists
                        flag = false;
                        for (Slave slave : slaveList) {
                            if (dst.equals(slave.getSName())) {
                                flag = true;
                                dstSlave = slave;
                                break;
                            }
                        }
                        if (!flag) {
                            System.out.println("Destination slave does not exist. Try again\n");
                            break;
                        }

                        //find the slave that matches the origin name
                        flag = false;
                        for (Slave srcSlave : slaveList) {
                            if (pNameSplit[0].equals(srcSlave.getSName())) {
                                flag = true;

                                //Suspend & Receive process from source slave
                                Socket srcSocket = new Socket(srcSlave.getHostname(), srcSlave.getPort());
                                PrintWriter outSrc = new PrintWriter(srcSocket.getOutputStream(), true);
                                outSrc.println(line);
                                ObjectInputStream recvObj = new ObjectInputStream(srcSocket.getInputStream());
                                MigratableProcess toMigrate = (MigratableProcess) recvObj.readObject();
                                outSrc.flush();

                                //Sending process to the destination
                                Socket dstSocket = new Socket(dstSlave.getHostname(), dstSlave.getPort());
                                PrintWriter outDst = new PrintWriter(dstSocket.getOutputStream(), true);
                                BufferedReader inDst = new BufferedReader(new InputStreamReader(dstSocket.getInputStream()));
                                String command = "receive " + pNameSplit[1];
                                outDst.println(command);
                                ObjectOutputStream outObject = new ObjectOutputStream(dstSocket.getOutputStream());
                                outObject.writeObject(toMigrate);
                                String message = inDst.readLine();
                                System.out.println("Successfully migrated to " + dst + ". New pName: " + message + "\n");
                            }
                        }
                        if (!flag) {
                            System.out.println("Origin Slave does not exist. Try again.\n");
                        }
                        break;

                    case "kill":
                        pName = lineSplit[1];
                        String[] nameSplit = pName.split("_");
                        String sName;
                        int port = 0;
                        String hostname = "";
                        flag = false;
                        for (Slave slave : slaveList) {
                            sName = slave.getSName();
                            if (sName.equals(nameSplit[0])) {
                                port = slave.getPort();
                                hostname = slave.getHostname();
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            System.out.println("Slave does not exist. Try again.\n");
                        } else {
                            s = new Socket(hostname, port);
                            out = new PrintWriter(s.getOutputStream(), true);
                            input = new BufferedReader(
                                    new InputStreamReader(s.getInputStream()));
                            out.println(line);
                            String message = input.readLine();
                            System.out.println(message);
                        }
                        break;

                    case "peek":
                        ArrayList<Thread> ttable = new ArrayList<Thread>(50);
                        System.out.println("Process List1:");
                        for (Slave slave : slaveList) {

                            s = new Socket(slave.getHostname(), slave.getPort());
                            out = new PrintWriter(s.getOutputStream(), true);

                            out.println("peek");
                            ObjectInputStream inobj = new ObjectInputStream(s.getInputStream());

                            try {
                                String[] newObj = (String[]) inobj.readObject();
                                for (String t : newObj) {
                                    if (!t.equals("")) {
                                        System.out.println(t);
                                    }
                                }
                                System.out.println("\n");
                                s.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        for (Thread t : ttable) {
                            System.out.println(t.getName());
                        }
                        break;

                    default:
                        System.out.println("Please enter a valid command (launch/migrate/remove).\n");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out
                        .println("Please enter a valid command (launch/migrate/remove).\n");
            }

        }
    }
}
