package net.ilexiconn.packet.server;

import net.ilexiconn.packet.INetworkManager;
import net.ilexiconn.packet.IOUtils;
import net.ilexiconn.packet.IPacket;
import net.ilexiconn.packet.NetworkRegistry;
import net.ilexiconn.packet.Side;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Server implements INetworkManager {
    private int port;
    private ServerSocket serverSocket;
    private volatile List<Socket> aliveClients = new ArrayList<>();
    private volatile boolean clientConnecting;

    public Server(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);

        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Server.this.update();
                }
            }
        });

        updateThread.start();
    }

    public void waitForConnection() throws IOException {
        Socket client = this.serverSocket.accept();

        clientConnecting = true;

        if (!aliveClients.contains(client)) {
            System.out.println(client.getInetAddress() + ":" + client.getPort() + " has connected!");
            aliveClients.add(client);
        }

        clientConnecting = false;
    }

    @Override
    public void sendPacketToClient(IPacket packet, Socket client) {
        try {
            OutputStream out = client.getOutputStream();

            byte[] idBytes = ByteBuffer.allocate(4).putInt(NetworkRegistry.getHandlerForPacket(packet).getId()).array();

            byte[] bytes = packet.encode();

            for (int i = 0; i < idBytes.length; i++) {
                bytes[i] = idBytes[i];
            }

            out.write(bytes);
        } catch (IOException e) {
            System.err.println("Failed to send packet: " + packet);
            e.printStackTrace();
        }
    }

    @Override
    public void sendPacketToAllClients(IPacket packet) {
        for (Socket client : aliveClients) {
            sendPacketToClient(packet, client);
        }
    }

    @Override
    public void sendPacketToServer(IPacket packet) {
    }

    @Override
    public void update() {
    }

    public void listen() {
        while (clientConnecting);

        List<Socket> aliveClients = new ArrayList<>(this.aliveClients);

        for (Socket client : aliveClients) {
            try {
                InputStream in = client.getInputStream();
                if (in.available() != 0) {
                    byte[] data = IOUtils.toByteArray(in);
                    IPacket packet = NetworkRegistry.constructFromId(NetworkRegistry.getId(data));
                    packet.decode(data);
                    packet.handle(null, Side.SERVER, client, Server.this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
