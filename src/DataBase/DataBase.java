package DataBase;

import Audit.AuditLog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Vector;

public class DataBase {

    public DataBase() {
        Vector<AuditLog> logs = new Vector<>();
        System.out.println("Database server started");
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(9000); //Database port
            while (true) {
                byte[] receiveMessage = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);
                serverSocket.receive(receivePacket);
                byte[] data = receivePacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                try {
                    AuditLog msg = (AuditLog) is.readObject();
                    synchronized (this) {
                        logs.add(msg);
                        logs.lastElement().logConfigurationChange();
                    }
                    String reply = "Message " + msg + " received;" + msg.getUserId() + ";" + msg.getPort();
                    byte[] replyMsg = reply.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(replyMsg, replyMsg.length, receivePacket.getAddress(), receivePacket.getPort());
                    serverSocket.send(sendPacket);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
        System.out.println("UDP server terminating");
    }

    public static void main(String[] args) {
        new DataBase();
    }
}
