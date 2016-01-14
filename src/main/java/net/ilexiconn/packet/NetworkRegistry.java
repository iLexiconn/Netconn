package net.ilexiconn.packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class NetworkRegistry {
    private static Map<Integer, IPacketHandler> packetHandlerMap = new HashMap<>();

    public static void registerPacket(IPacketHandler packetHandler) {
        packetHandlerMap.put(packetHandler.getID(), packetHandler);
    }

    public static void registerPackets(IPacketHandler... packetHandlers) {
        for (IPacketHandler packetHandler : packetHandlers) {
            packetHandlerMap.put(packetHandler.getID(), packetHandler);
        }
    }

    public static IPacket constructFromId(int id) {
        return packetHandlerMap.get(id).createPacket();
    }

    public static int getId(byte[] data) {
        return ByteBuffer.wrap(new byte[]{data[0], data[1], data[2], data[3]}).getInt();
    }

    public static IPacketHandler getHandlerForPacket(Class<? extends IPacket> packet) {
        for (Map.Entry<Integer, IPacketHandler> entry : packetHandlerMap.entrySet()) {
            IPacketHandler handler = entry.getValue();
            if (handler.getPacketClass().equals(packet)) {
                return handler;
            }
        }
        return null;
    }
}
