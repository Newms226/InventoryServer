package edu.msudenver.mnewma12.server;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPResponse {

    private DatagramPacket client;

    private String message;

    public UDPResponse() { }

    public UDPResponse(DatagramPacket client, String message) {
        this.client = client;
        this.message = message;
    }

    public InetAddress getAddress() {
        return client.getAddress();
    }

    public int getPort() {
        return client.getPort();
    }

    public String getMessage() {
        return message;
    }

//    public void setMessage(String message) {
//        this.message = message;
//    }

    @Override
    public String toString() {
        return message;
    }
}
