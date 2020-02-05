package edu.msudenver.mnewma12.client;

import com.google.gson.Gson;
import edu.msudenver.mnewma12.server.Computer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static edu.msudenver.mnewma12.core.Config.ASSIGNED_PORT;
import static edu.msudenver.mnewma12.server.Computer.COMPUTERS;
import static edu.msudenver.mnewma12.server.Computer.ID_TO_COMPUTER;

public class InventoryClient {

    private static final String computerStr = "ID Description\n" +
            COMPUTERS.stream()
            .map(comp -> comp.ID + "  " + comp.description)
            .collect(Collectors.joining("\n"));

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java UDPClient <hostname>");
            return;
        }

        InventoryClient client = new InventoryClient(args[0]);
        client.run();
    }


    private DatagramSocket udpSocket;
    private BufferedReader sysIn;
    private InetAddress serverAddress; // TODO final for security reasons?
    private transient long startTime, endTime;
    private transient Gson gson;

    InventoryClient(String serverDNSName) throws IOException {
        sysIn = new BufferedReader(new InputStreamReader(System.in));
        udpSocket = new DatagramSocket();
        serverAddress = InetAddress.getByName(serverDNSName);
        gson = new Gson();
    }

    void run() throws IOException {
        String fromUser, fromServer;
        System.out.println(computerStr + "\nSearch by ID:");

        while(true) {
            fromUser = getLine();
            sendRequest(fromUser);
            // block
            fromServer = getResponse();
            parseResponse(fromServer);

            if (!shouldContinue()) break;
        }

        udpSocket.close();
    }

    String getLine() throws IOException {
        while (true) {
            try {
                System.out.print("Please enter an id:\n>");
                String line = sysIn.readLine();
                if (line == null) continue;

                int key = Integer.parseInt(line);

                if (ID_TO_COMPUTER.containsKey(key)) {
                    System.out.println("User input: " + key);
                    return line;
                } else {
                    System.out.println("That is not a valid key.\n" + computerStr);
                    // continue
                }
            } catch (NumberFormatException e) {
                System.out.println("Must enter an integer.");
            }
        }
    }

    void sendRequest(String fromUser) throws IOException {
        byte[] buf = fromUser.getBytes();
        DatagramPacket udpPacket =
                new DatagramPacket(buf, buf.length, serverAddress, ASSIGNED_PORT);
        startTime = System.nanoTime();
        udpSocket.send(udpPacket);
    }

    /** blocking */
    String getResponse() throws IOException {
        byte[] buf2 = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf2, buf2.length);
        udpSocket.receive(packet);
        endTime = System.nanoTime();

        return new String(packet.getData(), 0, packet.getLength());
    }

    private boolean shouldContinue() throws IOException {
        while (true) {
            System.out.print("Would you like to continue? (Y/n)\n>");
            String line = sysIn.readLine();
            System.out.println("User Input: " + line);

            if (line == null || line.toLowerCase().startsWith("y")) {
                return true;
            } else if (line.toLowerCase().startsWith("n")) {
                return false;
            } else {
                System.out.println("Invalid entry");
            }
        }
    }

    private void parseResponse(String fromServer) {
        Computer computer = gson.fromJson(fromServer, Computer.class);
        System.out.println("\nFOUND: " + computer);
        System.out.print("  RTT of Query: " + (endTime - startTime) + " nanoseconds.\n\n");
    }
}
