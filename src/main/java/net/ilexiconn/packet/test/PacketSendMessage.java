package net.ilexiconn.packet.test;

import net.ilexiconn.packet.ByteHelper;
import net.ilexiconn.packet.INetworkManager;
import net.ilexiconn.packet.IPacket;
import net.ilexiconn.packet.Side;

import java.net.Socket;

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
    public byte[] encode() {
        ByteHelper byteHelper = new ByteHelper();
        byteHelper.writeString(sender);
        byteHelper.writeString(message);

        return byteHelper.toBytes();
    }

    @Override
    public void decode(byte[] data) {
        ByteHelper helper = new ByteHelper(data);
        this.sender = helper.readString();
        this.message = helper.readString();
    }

    @Override
    public void handle(ByteHelper byteHelper, Side side, Socket from, INetworkManager networkManager) {
        boolean printMessage = true;

        if (side.isClient()) {
            if (ChatClient.username.equals(sender)) {
                printMessage = false;
            }
        }

        if (printMessage) {
            System.out.println("<" + sender + "> " + message);
        }

        if (side.isServer()) {
            networkManager.sendPacketToAllClients(this);
        }
    }
}
