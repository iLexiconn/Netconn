package net.ilexiconn.netconn.client;

import net.ilexiconn.netconn.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
            IPacket packet = NetconnRegistry.constructFromID(NetconnRegistry.getIDFromBytes(data));
            if (packet != null) {
                packet.decode(new ByteBuffer(data));
                packet.handleClient(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void sendPacketToServer(IPacket packet) {
        try {
            OutputStream out = server.getOutputStream();
            byte[] idBytes = java.nio.ByteBuffer.allocate(4).putInt(NetconnRegistry.getIDFromPacket(packet.getClass())).array();
            byte[] bytes = packet.encode(new ByteBuffer());
            System.arraycopy(idBytes, 0, bytes, 0, idBytes.length);
            out.write(bytes);
        } catch (IOException e) {
            System.err.println("Failed to send packet with ID " + NetconnRegistry.getIDFromPacket(packet.getClass()));
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
