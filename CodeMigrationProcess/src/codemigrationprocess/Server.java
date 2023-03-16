package codemigrationprocess;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Server {

    private Socket socket = null;
    private ServerSocket server = null;
    static int port = 9000;

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startServer(port);
            server.acceptConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer(int port) throws IOException {
        server = new ServerSocket(port);
		System.out.println(server.getInetAddress().getHostName());
        System.out.println("Server started");
    }

    private void acceptConnection() throws Exception {
        socket = server.accept();
        System.out.println("Accepted a client connection");
        receiveMethodToExecute();
    }

    private void receiveMethodToExecute() throws Exception {
        System.out.println("Received code to execute");
        ObjectInputStream dataInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        Class<?> declaringClass = (Class<?>) dataInputStream.readObject();
        String methodName = dataInputStream.readUTF();
        Class<?>[] paramTypes = (Class<?>[]) dataInputStream.readObject();
        Object[] input = (Object[]) dataInputStream.readObject();
        try{
			Method method = declaringClass.getDeclaredMethod(methodName, paramTypes);
			Object result = method.invoke(this,input);
			System.out.println("Method executed. Results: "+result);
			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeObject(result);
			outputStream.flush();
		}catch (Exception e){
        	e.printStackTrace();
			System.out.println("Method failed to execute");
		}
    }


}
