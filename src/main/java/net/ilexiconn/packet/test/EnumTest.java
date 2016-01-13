package net.ilexiconn.packet.test;

import net.ilexiconn.packet.IPacket;
import net.ilexiconn.packet.IPacketHandler;

public enum EnumTest implements IPacketHandler {
    TEST {
        @Override
        public IPacket createPacket() {
            return new PacketTestData();
        }

        @Override
        public Class<? extends IPacket> getPacketClass()
        {
            return PacketTestData.class;
        }
    };

    @Override
    public int getId() {
        return ordinal();
    }
}
