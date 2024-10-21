package Gateway;

import Audit.AuditLog;
import Clock.HybridLogicalClock;
import HeartBeat.Heartbeat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gateway {
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private List<Integer> OnlineServersList = new ArrayList<>();

    public Gateway() {
        int Port = 9000; //Gateway Port
        System.out.println("Gateway initalized");
        try {
            DatagramSocket serversocket = new DatagramSocket(Port);
            while (true) {
                byte[] receivemessage = new byte[1024];
                DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
                serversocket.receive(receivepacket);
                executorService.submit(()->requisitionHandler(receivepacket, serversocket));
            }
        } catch (IOException e) {
            System.out.println("Gateway was BRUTALLY EXECUTED!");
        }
    }

    public void requisitionHandler(DatagramPacket ByteArray, DatagramSocket serversocket)  {
        String message = new String(ByteArray.getData()).trim();
        System.out.println("Received from client: [" + message + "]\nfrom: " + String.valueOf(ByteArray.getAddress()));
        try {
            int serverPort;
            String address;
            byte[] data;

            if(message.startsWith("Message")){ //Server --> Client
                String[] potato = message.split(";"); //Client info
                data = potato[0].getBytes();
                address = potato[1];
                serverPort = Integer.parseInt(potato[2]);
            }  else if (message.equals("~Baby my heart is beating")){
                OnlineServersList.add(ByteArray.getPort());
                return;
            } else { //Client --> Server
                //serverPort = 0;
                String serverReply = message + ";" + ByteArray.getAddress() + ";" + ByteArray.getPort();
                data = serverReply.getBytes();
                address = "localhost";
            }
            DatagramPacket sendPacket = new DatagramPacket(
                    data, data.length,	InetAddress.getByName(address), serverPort);
            serversocket.send(sendPacket);
        } catch (IOException e){
            System.out.println("Abençoado seja Fernando (e o IntelIJ)!");
        }
    }
}