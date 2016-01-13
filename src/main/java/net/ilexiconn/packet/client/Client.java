package net.ilexiconn.packet.client;

import net.ilexiconn.packet.INetworkManager;
import net.ilexiconn.packet.IOUtils;
import net.ilexiconn.packet.IPacket;
import net.ilexiconn.packet.NetworkRegistry;
import net.ilexiconn.packet.Side;

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
    }

    public void listen() {
        try {
            InputStream in = server.getInputStream();
            byte[] data = IOUtils.toByteArray(in);
            IPacket packet = NetworkRegistry.constructFromId(NetworkRegistry.getId(data));
            packet.decode(data);
            packet.handle(null, Side.CLIENT, server, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPacketToServer(IPacket packet) {
        try {
            OutputStream out = server.getOutputStream();
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
    public void sendPacketToClient(IPacket packet, Socket client) {
    }
}
