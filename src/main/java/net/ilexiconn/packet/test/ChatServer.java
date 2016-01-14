package net.ilexiconn.packet.test;

import net.ilexiconn.packet.NetworkRegistry;
import net.ilexiconn.packet.server.Server;

import java.io.IOException;

public class ChatServer
{
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            NetworkRegistry.registerPacket(EnumTest.CHAT_MESSAGE);

            int port = Integer.parseInt(args[0]);

            final Server server = new Server(port);

            System.out.println("Launched server on port: " + port);

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
        } else {
            System.err.println("Please specify a port to host this server on!");
        }
    }
}
