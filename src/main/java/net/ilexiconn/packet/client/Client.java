package net.ilexiconn.packet.client;

import net.ilexiconn.packet.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client implements INetworkManager {
    private String host;
    private int port;
    private Socket server;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.server = new Socket(host, port);

        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Client.this.update();
                }
            }
        });

        updateThread.start();
    }

    @Override
    public void update() {

    }

    @Override
    public void listen() {
        try {
            InputStream in = server.getInputStream();
            byte[] data = IOUtils.toByteArray(in);
            IPacket packet = NetworkRegistry.constructFromId(NetworkRegistry.getId(data));
            packet.decode(new ByteHelper(data));
            packet.handle(new ByteHelper(data), Side.CLIENT, server, this);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void sendPacketToServer(IPacket packet) {
        try {
            OutputStream out = server.getOutputStream();
            byte[] idBytes = ByteBuffer.allocate(4).putInt(NetworkRegistry.getHandlerForPacket(packet.getClass()).getID()).array();
            byte[] bytes = packet.encode(new ByteHelper());
            System.arraycopy(idBytes, 0, bytes, 0, idBytes.length);
            out.write(bytes);
        } catch (IOException e) {
            System.err.println("Failed to send packet: " + packet);
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void sendPacketToClient(IPacket packet, Socket client) {

    }

    @Override
    public void sendPacketToAllClients(IPacket packet) {

    }
}
