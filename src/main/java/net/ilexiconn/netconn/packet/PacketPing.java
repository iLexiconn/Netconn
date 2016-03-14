package net.ilexiconn.netconn.packet;

import net.ilexiconn.netconn.ByteBuffer;
import net.ilexiconn.netconn.INetworkManager;
import net.ilexiconn.netconn.IPacket;
import net.ilexiconn.netconn.client.Client;

import java.net.Socket;

public class PacketPing implements IPacket {
    private int pingTime;

    public PacketPing() {}

    public PacketPing(int pingTime) {
        this.pingTime = pingTime;
    }

    @Override
    public void encode(ByteBuffer buffer) {
        buffer.writeInteger(pingTime);
    }

    @Override
    public void decode(ByteBuffer buffer) {
        pingTime = buffer.readInteger();
    }

    @Override
    public void handleServer(Socket sender, INetworkManager networkManager) {
    }

    @Override
    public void handleClient(Socket server, INetworkManager networkManager) {
        ((Client) networkManager).setPingTime(pingTime);
    }
}
