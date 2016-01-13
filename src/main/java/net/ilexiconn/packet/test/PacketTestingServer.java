package net.ilexiconn.packet.test;

import net.ilexiconn.packet.NetworkRegistry;
import net.ilexiconn.packet.server.Server;

import java.io.IOException;

public class PacketTestingServer
{
    public static void main(String[] args) throws IOException, InterruptedException {
        NetworkRegistry.registerPacket(EnumTest.TEST);

        final Server server = new Server(25565);

        Thread serverListen = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    server.listen();
                }
            }
        });

        serverListen.start();

        while (true) {
            server.waitForConnection();
        }
    }
}
