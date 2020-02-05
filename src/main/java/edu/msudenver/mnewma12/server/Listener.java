package edu.msudenver.mnewma12.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Listener {

    private final DatagramSocket serverSocket;

    public Listener(int port) throws SocketException {
        serverSocket = new DatagramSocket(port);
    }

    public DatagramPacket accept() throws IOException {
        byte[] buf = new byte[256];
        DatagramPacket clientPacket = new DatagramPacket(buf, buf.length);
        return accept(clientPacket);
    }

    public DatagramPacket accept(DatagramPacket clientPacket) throws IOException {
        serverSocket.receive(clientPacket);
        return clientPacket;
    }

    public Future<DatagramPacket> acceptAsync() throws IOException {
        return new FutureTask<>(this::accept);
    }


}
