package net.ilexiconn.netconn.packet;

import net.ilexiconn.netconn.ByteBuffer;
import net.ilexiconn.netconn.INetworkManager;
import net.ilexiconn.netconn.IPacket;
import net.ilexiconn.netconn.client.Client;
import net.ilexiconn.netconn.server.Server;

import java.net.Socket;

public class PacketDisconnect implements IPacket {
    @Override
    public void encode(ByteBuffer byteBuffer) {

    }

    @Override
    public void decode(ByteBuffer byteBuffer) {

    }

    @Override
    public void handleServer(Socket sender, INetworkManager networkManager) {
        ((Server) networkManager).disconnectClient(sender);
    }

    @Override
    public void handleClient(Socket server, INetworkManager networkManager) {
        ((Client) networkManager).disconnect();
    }
}
