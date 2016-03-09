package net.ilexiconn.netconn.test;

import net.ilexiconn.netconn.ByteBuffer;
import net.ilexiconn.netconn.INetworkManager;
import net.ilexiconn.netconn.IPacket;

public class PacketSendMessage implements IPacket {
    private String sender;
    private String message;

    public PacketSendMessage() {

    }

    public PacketSendMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void encode(ByteBuffer byteBuffer) {
        byteBuffer.writeStringShort(sender);
        byteBuffer.writeStringShort(message);
    }

    @Override
    public void decode(ByteBuffer byteBuffer) {
        this.sender = byteBuffer.readStringShort();
        this.message = byteBuffer.readStringShort();
    }

    @Override
    public void handleServer(INetworkManager networkManager) {
        System.out.println("<" + sender + "> " + message);
        networkManager.sendPacketToAllClients(this);
    }

    @Override
    public void handleClient(INetworkManager networkManager) {
        System.out.println("<" + sender + "> " + message);
    }
}
