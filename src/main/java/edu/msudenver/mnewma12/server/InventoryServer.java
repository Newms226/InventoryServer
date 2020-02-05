package edu.msudenver.mnewma12.server;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static edu.msudenver.mnewma12.core.Config.ASSIGNED_PORT;
import static edu.msudenver.mnewma12.server.Computer.ID_TO_COMPUTER;

class InventoryServer {

    public static void main(String[] args) throws SocketException {
        Listener listener = new Listener(ASSIGNED_PORT);
        ExecutorService exec = Executors.newCachedThreadPool();
        boolean morePackets = true;

        while(morePackets) {
            try {
                DatagramPacket packet = listener.accept();
                exec.execute(() -> {
                    try {
                        parse(packet, listener);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                morePackets = false;
            }
        }
    }

    private static void parse(DatagramPacket client, Listener listener)
            throws NumberFormatException, IOException {
        String requestStr = new String(client.getData()).trim();
//        System.out.println("REQUEST: " + requestStr);
        Computer comp = getComp(requestStr);
//        System.out.println("COMPUTER: " + comp);

        if (comp != null) {
            send200(client, listener, comp);
        } else {
            send404(client, listener, requestStr);
        }
    }

    private static Computer getComp(String reqKey) {
        int clientRequest = Integer.parseInt(reqKey);
//        System.out.println(ID_TO_COMPUTER);
        return ID_TO_COMPUTER.get(clientRequest);
    }

    private static final Gson gson = new Gson();

    private static void send200(DatagramPacket client, Listener listener,
                                Computer comp) throws IOException
    {
        listener.send(client, gson.toJson(comp));
    }

    private static void send404(DatagramPacket client, Listener listener,
                                String requestStr) throws IOException
    {
        listener.send(client,"Not found: " + requestStr);
    }
}
//
//    public static void main(String[] args) throws SocketException {
//        InventoryServer server = new InventoryServer(ASSIGNED_PORT);
//        server.serve();
//    }
//
//    static final int BYTE_ARRAY_SIZE = 256;
//
//    private final DatagramSocket udpServerSocket;
//
//    private Gson gson;
//
//    public InventoryServer(int port) throws SocketException {
//        udpServerSocket = new DatagramSocket(port);
//        gson = new Gson();
//    }
//
//    public void serve() {
//        byte[] buf = new byte[BYTE_ARRAY_SIZE];
//        boolean morePackets = true;
//        DatagramPacket clientPacket = null;
//        Computer comp;
//        int reqKey;
//        String matchStr;
//
//        while (morePackets) {
//            try {
//                System.out.println("Waiting...");
//                // receive UDP packet from clientPacket
//                clientPacket = new DatagramPacket(buf, buf.length);
//                udpServerSocket.receive(clientPacket);
//                reqKey = parse(clientPacket);
//                comp = ID_TO_COMPUTER.get(reqKey);
//
//                matchStr = "key: '" + reqKey + "' matched '" + comp + "'";
//                System.out.println("  " + matchStr);
//
//                if (comp != null) {
//                    sendComputer(clientPacket, comp);
//                } else {
//                    String errMsg = "Could not find " + matchStr;
//                    sendError(clientPacket, new NoSuchElementException(errMsg));
//                }
//
//                System.out.println("  sent.");
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Failed due to IOException. Goodbye ✌🏼");
//                morePackets = false;
//            } catch (NumberFormatException e) {
//                try {
//                    sendError(clientPacket, e);
//                } catch (IOException ex) {
//                    e.printStackTrace();
//                    System.out.println("Failed due to IOException. Goodbye ✌🏼");
//                    morePackets = false;
//                }
//            }
//        }
//
//        udpServerSocket.close();
//    }
//
//    private void sendError(DatagramPacket clientPacket, Exception e) throws IOException {
//        send(clientPacket.getPort(),
//             clientPacket.getAddress(),
//             gson.toJson(e));
//    }
//
//    private void sendComputer(DatagramPacket clientPacket, Computer comp) throws IOException {
//        send(clientPacket.getPort(),
//             clientPacket.getAddress(),
//             gson.toJson(comp));
//    }
//
//    void send(int port, InetAddress address, String message) throws IOException {
//        byte[] buf2 = message.getBytes();
//        DatagramPacket p = new DatagramPacket(buf2, buf2.length, address, port);
//        udpServerSocket.send(p);
//    }
//
//    int parse(DatagramPacket packet) throws NumberFormatException {
//        String fromClient = new String(packet.getData()).trim();
//        int clientRequest = Integer.parseInt(fromClient);
//        System.out.println("  read from client: " + fromClient);
//
//        return clientRequest;
//    }




//}