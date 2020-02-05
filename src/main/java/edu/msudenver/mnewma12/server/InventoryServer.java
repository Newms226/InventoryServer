package edu.msudenver.mnewma12.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static edu.msudenver.mnewma12.core.Config.ASSIGNED_PORT;

import com.google.gson.Gson;

import static edu.msudenver.mnewma12.server.Computer.ID_TO_COMPUTER;

public class InventoryServer {

    public static void main(String[] args) throws SocketException {
        InventoryServer server = new InventoryServer(ASSIGNED_PORT);
        server.serve();
    }

    static final int BYTE_ARRAY_SIZE = 256;

    private final DatagramSocket udpServerSocket;

    private Gson gson;

    public InventoryServer(int port) throws SocketException {
        udpServerSocket = new DatagramSocket(port);
        gson = new Gson();
    }

    public void serve() {
        byte[] buf = new byte[BYTE_ARRAY_SIZE];
        boolean morePackets = true;
        DatagramPacket clientPacket = null;
        Computer comp;
        int reqKey;
        String matchStr;

        while (morePackets) {
            try {
                System.out.println("Waiting...");
                // receive UDP packet from clientPacket
                clientPacket = new DatagramPacket(buf, buf.length);
                udpServerSocket.receive(clientPacket);
                reqKey = parse(clientPacket);
                comp = ID_TO_COMPUTER.get(reqKey);

                matchStr = "key: '" + reqKey + "' matched '" + comp + "'";
                System.out.println("  " + matchStr);

                if (comp != null) {
                    sendComputer(clientPacket, comp);
                } else {
                    String errMsg = "Could not find " + matchStr;
                    sendError(clientPacket, new NoSuchElementException(errMsg));
                }

                System.out.println("  sent.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed due to IOException. Goodbye ✌🏼");
                morePackets = false;
            } catch (NumberFormatException e) {
                try {
                    sendError(clientPacket, e);
                } catch (IOException ex) {
                    e.printStackTrace();
                    System.out.println("Failed due to IOException. Goodbye ✌🏼");
                    morePackets = false;
                }
            }
        }

        udpServerSocket.close();
    }

    private void sendError(DatagramPacket clientPacket, Exception e) throws IOException {
        send(clientPacket.getPort(),
             clientPacket.getAddress(),
             gson.toJson(e));
    }

    private void sendComputer(DatagramPacket clientPacket, Computer comp) throws IOException {
        send(clientPacket.getPort(),
             clientPacket.getAddress(),
             gson.toJson(comp));
    }

    void send(int port, InetAddress address, String message) throws IOException {
        byte[] buf2 = message.getBytes();
        DatagramPacket p = new DatagramPacket(buf2, buf2.length, address, port);
        udpServerSocket.send(p);
    }

    int parse(DatagramPacket packet) throws NumberFormatException {
        String fromClient = new String(packet.getData()).trim();
        int clientRequest = Integer.parseInt(fromClient);
        System.out.println("  read from client: " + fromClient);

        return clientRequest;
    }




}
