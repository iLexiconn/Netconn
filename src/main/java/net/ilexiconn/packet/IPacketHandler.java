package net.ilexiconn.packet;

public interface IPacketHandler {
    IPacket createPacket();
    
    Class<? extends IPacket> getPacketClass();

    int getId();
}
