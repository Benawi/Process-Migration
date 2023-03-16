package framework;

import java.io.IOException;

public class MasterMain {

    /**
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Master m = new Master("ProcessManager", 9000);
        Slave s1 = new Slave("SlaveOne", "10.194.119.85", 6001);
        Slave s2 = new Slave("SlaveTwo", "10.194.119.82", 6002);
        //Slave s3 = new Slave("SlaveOne", "127.0.0.1", 6000);

        m.addSlave(s1);
        m.addSlave(s2);
        //m.addSlave(s3);
        m.start();
    }

}
