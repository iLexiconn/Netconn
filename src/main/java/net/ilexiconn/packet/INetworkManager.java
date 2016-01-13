package net.ilexiconn.packet;

import java.net.Socket;

public interface INetworkManager {
    void listen();

    void sendPacketToServer(IPacket packet);

    void sendPacketToClient(IPacket packet, Socket client);
}
