package net.ilexiconn.packet;

import java.net.Socket;

public interface IPacket {
    byte[] encode(ByteHelper byteHelper);

    void decode(ByteHelper byteHelper);

    void handle(ByteHelper byteHelper, Side side, Socket from, INetworkManager networkManager);
}
