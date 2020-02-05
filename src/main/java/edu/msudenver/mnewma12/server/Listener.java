package edu.msudenver.mnewma12.server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Listener {

    private final DatagramSocket socket;

    public Listener(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }


}
