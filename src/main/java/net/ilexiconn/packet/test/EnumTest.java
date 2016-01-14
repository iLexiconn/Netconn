package net.ilexiconn.packet.test;

import net.ilexiconn.packet.IPacket;
import net.ilexiconn.packet.IPacketHandler;

public enum EnumTest implements IPacketHandler {
    CHAT_MESSAGE {
        @Override
        public IPacket createPacket() {
            return new PacketSendMessage();
        }

        @Override
        public Class<? extends IPacket> getPacketClass() {
            return PacketSendMessage.class;
        }
    };

    @Override
    public int getID() {
        return ordinal();
    }
}
