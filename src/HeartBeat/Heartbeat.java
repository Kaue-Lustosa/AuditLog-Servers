package HeartBeat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Heartbeat {
    private final int port;
    private final String address;

    public Heartbeat(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void sendHeartbeat() {
        try {
            DatagramSocket socket = new DatagramSocket();
            String heartbeatMessage = "~Baby my heart is beating";
            byte[] buffer = heartbeatMessage.getBytes();
            InetAddress inetAddress = InetAddress.getByName(address);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}