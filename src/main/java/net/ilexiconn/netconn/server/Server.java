package net.ilexiconn.netconn.server;

import net.ilexiconn.netconn.*;
import net.ilexiconn.netconn.packet.PacketDisconnect;
import net.ilexiconn.netconn.packet.PacketKeepAlive;
import net.ilexiconn.netconn.packet.PacketPing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements INetworkManager {
    private int port;
    private ServerSocket serverSocket;
    private volatile List<Socket> aliveClients = new ArrayList<Socket>();
    private volatile List<Socket> deadClients = new ArrayList<Socket>();
    private List<IServerListener> serverListenerList;
    private boolean running;

    private Map<Socket, Long> keepAliveSendTimes = new HashMap<Socket, Long>();
    private Map<Socket, Integer> pingTimes = new HashMap<Socket, Integer>();

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
        NetconnRegistry.registerPacket(-3, PacketPing.class);

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
                    for (Socket client : deadClients) {
                        disconnectClient(client);
                    }
                    deadClients.clear();
                }

                keepAliveSendTimes.clear();
                for (Socket client : new ArrayList<Socket>(aliveClients)) {
                    deadClients.add(client);
                    sendPacketToClient(new PacketKeepAlive(), client);
                    keepAliveSendTimes.put(client, System.currentTimeMillis());
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
        return aliveClients;
    }

    public List<Socket> getDeadClients() {
        return deadClients;
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
        this.pingTimes.remove(client);
    }

    public void stop() {
        running = false;
        for (Socket client : this.aliveClients) {
            if (client != null) {
                for (IServerListener listener : this.serverListenerList) {
                    listener.onClientDisconnected(this, client);
                }
            }
        }

        this.aliveClients.clear();
        this.pingTimes.clear();
    }

    public void waitForConnection() throws IOException {
        Socket client = this.serverSocket.accept();

        if (!aliveClients.contains(client)) {
            for (IServerListener listener : this.serverListenerList) {
                listener.onClientConnected(this, client);
            }
            aliveClients.add(client);
            final Socket finalClient = client;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (getAliveClients().contains(finalClient)) {
                            InputStream in = finalClient.getInputStream();
                            if (in.available() != 0) {
                                byte[] data = IOUtils.toByteArray(in);
                                IPacket packet = NetconnRegistry.constructFromID(NetconnRegistry.getIDFromBytes(data));
                                if (packet != null) {
                                    packet.decode(new ByteBuffer(data));
                                    packet.handleServer(finalClient, Server.this);
                                }
                            }
                        }
                    } catch (IOException e) {
                    }
                }
            }).start();
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
            disconnectClient(client);
        }
    }

    @Override
    public void sendPacketToAllClients(IPacket packet) {
        for (Socket client : new ArrayList<Socket>(aliveClients)) {
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
    }

    public void receiveKeepAlive(Socket client) {
        int ping = (int) (System.currentTimeMillis() - keepAliveSendTimes.get(client));
        deadClients.remove(client);
        pingTimes.put(client, ping);
        sendPacketToClient(new PacketPing(ping), client);
    }

    public int getPingTime(Socket client) {
        return pingTimes.get(client);
    }

    public boolean isRunning() {
        return running;
    }
}
