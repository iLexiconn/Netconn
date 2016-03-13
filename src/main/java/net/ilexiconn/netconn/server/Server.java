package net.ilexiconn.netconn.server;

import net.ilexiconn.netconn.*;
import net.ilexiconn.netconn.packet.PacketDisconnect;
import net.ilexiconn.netconn.packet.PacketKeepAlive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Server implements INetworkManager {
    private int port;
    private ServerSocket serverSocket;
    private volatile List<Socket> aliveClients = new ArrayList<Socket>();
    private volatile List<Socket> deadClients = new ArrayList<Socket>();
    private List<IServerListener> serverListenerList;
    private boolean running;

    public Server(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.serverListenerList = new ArrayList<IServerListener>();
        this.running = true;

        Thread updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    Server.this.update();
                }
            }
        });

        NetconnRegistry.registerPacket(-1, PacketKeepAlive.class);
        NetconnRegistry.registerPacket(-2, PacketDisconnect.class);

        updateThread.start();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!running) {
                    timer.cancel();
                    timer.purge();
                }

                if (!deadClients.isEmpty()) {
                    for (Socket client : getDeadClients()) {
                        disconnectClient(client);
                    }
                    deadClients.clear();
                }

                for (Socket client : getAliveClients()) {
                    deadClients.add(client);
                    sendPacketToClient(new PacketKeepAlive(), client);
                }
            }
        }, 5000, 5000);
    }

    public void addListener(IServerListener listener) {
        this.serverListenerList.add(listener);
    }

    public int getPort() {
        return port;
    }

    public List<Socket> getAliveClients() {
        return new ArrayList<Socket>(aliveClients);
    }

    public List<Socket> getDeadClients() {
        return new ArrayList<Socket>(deadClients);
    }

    public void disconnectClient(Socket client) {
        for (IServerListener listener : this.serverListenerList) {
            listener.onClientDisconnected(this, client);
        }
        try {
            client.close();
        } catch (Exception e) {
        }
        this.aliveClients.remove(client);
    }

    public void stop() {
        running = false;
        for (Socket client : getAliveClients()) {
            if (client != null) {
                for (IServerListener listener : this.serverListenerList) {
                    listener.onClientDisconnected(this, client);
                }
            }
        }
        this.aliveClients.clear();
    }

    public void waitForConnection() throws IOException {
        Socket client = this.serverSocket.accept();

        if (!aliveClients.contains(client)) {
            for (IServerListener listener : this.serverListenerList) {
                listener.onClientConnected(this, client);
            }
            aliveClients.add(client);
        }
    }

    @Override
    public void sendPacketToClient(IPacket packet, Socket client) {
        try {
            OutputStream out = client.getOutputStream();
            byte[] idBytes = java.nio.ByteBuffer.allocate(4).putInt(NetconnRegistry.getIDFromPacket(packet.getClass())).array();
            ByteBuffer byteBuffer = new ByteBuffer();
            packet.encode(byteBuffer);
            byte[] bytes = byteBuffer.toBytes();
            System.arraycopy(idBytes, 0, bytes, 0, idBytes.length);
            out.write(bytes);
        } catch (IOException e) {
            deadClients.remove(client);
            aliveClients.remove(client);
            disconnectClient(client);
        }
    }

    @Override
    public void sendPacketToAllClients(IPacket packet) {
        for (Socket client : getAliveClients()) {
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
        for (Socket client : getAliveClients()) {
            if (client != null) {
                try {
                    InputStream in = client.getInputStream();
                    if (in.available() != 0) {
                        byte[] data = IOUtils.toByteArray(in);
                        IPacket packet = NetconnRegistry.constructFromID(NetconnRegistry.getIDFromBytes(data));
                        if (packet != null) {
                            packet.decode(new ByteBuffer(data));
                            packet.handleServer(client, Server.this);
                        }
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }
}
