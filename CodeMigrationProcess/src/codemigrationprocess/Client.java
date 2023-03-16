package codemigrationprocess;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private Socket serverConnection;


    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.connectToServer();

            client.sendMergeSort();
            client.receiveResult();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private void connectToServer() throws IOException {
        serverConnection = new Socket(InetAddress.getLocalHost().getHostName(), 9000);
        System.out.println(serverConnection.getLocalAddress().getHostName());
    }


    private void sendMergeSort() throws Exception {
        MergeSort mergeSort = new MergeSort();
        Class clazz = Class.forName(mergeSort.getClass().getName());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverConnection.getOutputStream());
        Class<?>[] paramTypes = {int.class, int.class};
        Method method = clazz.getDeclaredMethod("mergeSort", paramTypes);
        objectOutputStream.writeObject(clazz);
        objectOutputStream.writeUTF(method.getName());
        objectOutputStream.writeObject(method.getParameterTypes());
        int[] arr = {5, 1, 6, 2, 3, 4};
        Object[] input = {arr, arr.length};
        objectOutputStream.writeObject(input);
        objectOutputStream.flush();
    }

    private void receiveResult() throws Exception {
        ObjectInputStream dataInputStream = new ObjectInputStream(new BufferedInputStream(serverConnection.getInputStream()));
        Object result = dataInputStream.readObject();
        System.out.println("Method was executed by server. Result = " + result);
    }


}
