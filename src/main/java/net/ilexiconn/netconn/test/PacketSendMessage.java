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
        byteBuffer.writeString(sender);
        byteBuffer.writeString(message);
    }

    @Override
    public void decode(ByteBuffer byteBuffer) {
        this.sender = byteBuffer.readString();
        this.message = byteBuffer.readString();
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
