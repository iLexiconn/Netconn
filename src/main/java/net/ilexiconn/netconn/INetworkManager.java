package net.ilexiconn.netconn;

import java.net.Socket;

public interface INetworkManager {
    void update();

    void listen();

    void sendPacketToServer(IPacket packet);

    void sendPacketToClient(IPacket packet, Socket client);

    void sendPacketToAllClients(IPacket packet);
}
