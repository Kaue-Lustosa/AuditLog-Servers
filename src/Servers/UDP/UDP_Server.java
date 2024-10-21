package Servers.UDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Clock.HybridLogicalClock;
import Audit.AuditLog;
import HeartBeat.Heartbeat;

public class UDP_Server {
    private HybridLogicalClock hlc = null;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Heartbeat heartBeatList;

    public UDP_Server(int port) {
        hlc = new HybridLogicalClock();
        System.out.println("Udp server started");
        heartBeatList = new Heartbeat("localhost", 9000); //HeartBeat Server --> Gateway
        scheduler.scheduleAtFixedRate(()->heartBeatList.sendHeartbeat(), 0, 5, TimeUnit.SECONDS);
        try {
            DatagramSocket serversocket = new DatagramSocket(port);
            while (true) {
                byte[] receivemessage = new byte[1024];
                DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
                serversocket.receive(receivepacket);
                executorService.submit(()->requisitionHandler(receivepacket, serversocket));
            }
        } catch (IOException e) {
            System.out.println("Udp server terminating");
            throw new RuntimeException(e);
        }
    }

    public void requisitionHandler(DatagramPacket ByteArray, DatagramSocket serversocket)  {
        String message = new String(ByteArray.getData()).trim();
        System.out.println("Received from client: [" + message + "]\nfrom: " + String.valueOf(ByteArray.getAddress()));
        try {
            int port;
            byte[] data = ByteArray.getData();

            if(message.startsWith("Message")){ //Database --> Gateway
                port = 8000;
            } else { //Gateway --> Database
                port = 9000;
                String[] potato = message.split(";"); //Client info
                String msg = potato[0];
                String address = potato[1];
                String clientPort = potato[2];

                //HLC implementation
                long receivedTime = System.currentTimeMillis();
                hlc.update(receivedTime, 0);
                long[] currentTime = hlc.getTime();

                //AuditLog implementation
                AuditLog auditLog = new AuditLog("UDP Config", address, Integer.parseInt(clientPort), currentTime, msg);
                auditLog.logConfigurationChange();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                os.writeObject(auditLog);
                data = outputStream.toByteArray();
            }
            DatagramPacket sendPacket = new DatagramPacket(
                    data, data.length,	InetAddress.getByName("localhost"), port);
            serversocket.send(sendPacket);
        } catch (IOException e){
            System.out.println("Abençoado seja Fernando (e o IntelIJ)!");
        }
    }
}