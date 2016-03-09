package net.ilexiconn.netconn;

public interface IPacket {
    void encode(ByteBuffer byteBuffer);

    void decode(ByteBuffer byteBuffer);

    void handleServer(INetworkManager networkManager);

    void handleClient(INetworkManager networkManager);
}
