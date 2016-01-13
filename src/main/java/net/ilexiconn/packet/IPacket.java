package net.ilexiconn.packet;

import java.net.Socket;

public interface IPacket {
    byte[] encode();

    void decode(byte[] data);

    void handle(ByteHelper byteHelper, Side side, Socket from, INetworkManager networkManager);
}
