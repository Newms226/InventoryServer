package edu.msudenver.mnewma12.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

class Listener {

    private final DatagramSocket serverSocket;

    public Listener(int port) throws SocketException {
        serverSocket = new DatagramSocket(port);
    }

    public DatagramPacket accept() throws IOException {
        byte[] buf = new byte[256];
        DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
        return accept(clientPacket);
    }

    private DatagramPacket accept(DatagramPacket clientPacket) throws IOException {
        serverSocket.receive(clientPacket);
        return clientPacket;
    }

    public Future<DatagramPacket> acceptAsync() {
        return new FutureTask<>(this::accept);
    }

    public void send(DatagramPacket client, String message) throws IOException {
        System.out.println("MSG: " + message);
        byte[] buf = message.getBytes();
        DatagramPacket p = new DatagramPacket(buf, buf.length,
                client.getAddress(), client.getPort());
        serverSocket.send(p);
    }


}
