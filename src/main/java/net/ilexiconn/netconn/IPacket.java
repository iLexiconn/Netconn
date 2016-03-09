package net.ilexiconn.netconn;

import java.net.Socket;

public interface IPacket {
    void encode(ByteBuffer byteBuffer);

    void decode(ByteBuffer byteBuffer);

    void handleServer(Socket sender, INetworkManager networkManager);

    void handleClient(Socket server, INetworkManager networkManager);
}
