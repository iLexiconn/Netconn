package net.ilexiconn.netconn.client;

import net.ilexiconn.netconn.*;
import net.ilexiconn.netconn.packet.PacketDisconnect;
import net.ilexiconn.netconn.packet.PacketKeepAlive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements INetworkManager {
    private boolean connected;
    private String host;
    private int port;
    private Socket server;
    private List<IClientListener> clientListenerList;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.server = new Socket(host, port);
        this.clientListenerList = new ArrayList<IClientListener>();
        this.connected = true;

        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (connected) {
                    Client.this.update();
                }
            }
        });

        updateThread.start();

        NetconnRegistry.registerPacket(-1, PacketKeepAlive.class);
        NetconnRegistry.registerPacket(-2, PacketDisconnect.class);
    }

    public void addListener(IClientListener listener) {
        this.clientListenerList.add(listener);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void disconnect() {
        this.connected = false;
        for (IClientListener listener : this.clientListenerList) {
            listener.onDisconnected(this);
        }
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
                packet.handleClient(server, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
            System.exit(1);
        }
    }

    @Override
    public void sendPacketToServer(IPacket packet) {
        try {
            OutputStream out = server.getOutputStream();
            byte[] idBytes = java.nio.ByteBuffer.allocate(4).putInt(NetconnRegistry.getIDFromPacket(packet.getClass())).array();
            ByteBuffer byteBuffer = new ByteBuffer();
            packet.encode(byteBuffer);
            byte[] bytes = byteBuffer.toBytes();
            System.arraycopy(idBytes, 0, bytes, 0, idBytes.length);
            out.write(bytes);
        } catch (IOException e) {
            System.err.println("Failed to send packet with ID " + NetconnRegistry.getIDFromPacket(packet.getClass()));
        }
    }

    @Override
    public void sendPacketToClient(IPacket packet, Socket client) {

    }

    @Override
    public void sendPacketToAllClients(IPacket packet) {

    }

    public boolean isConnected() {
        return connected;
    }
}
