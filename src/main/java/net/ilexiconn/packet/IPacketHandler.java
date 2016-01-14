package net.ilexiconn.packet;

public interface IPacketHandler<T extends IPacket> {
    T createPacket();

    Class<T> getPacketClass();

    int getID();
}
