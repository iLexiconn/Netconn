package net.ilexiconn.netconn;

public interface IPacket {
    byte[] encode(ByteBuffer byteBuffer);

    void decode(ByteBuffer byteBuffer);

    void handleServer(INetworkManager networkManager);

    void handleClient(INetworkManager networkManager);
}
