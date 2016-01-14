package net.ilexiconn.netconn.test;

import net.ilexiconn.netconn.NetconnRegistry;
import net.ilexiconn.netconn.server.Server;

import java.io.IOException;
import java.util.Scanner;

public class ChatServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            NetconnRegistry.registerPacket(0, PacketSendMessage.class);

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
            Scanner in = new Scanner(System.in);

            new Thread() {
                public void run() {
                    try {
                        server.waitForConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            while (in.hasNextLine()) {
                String message = in.nextLine();
                System.out.println("<SERVER> " + message);
                server.sendPacketToAllClients(new PacketSendMessage("SERVER", message));
            }
        } else {
            System.err.println("Please specify a port to host this server on!");
        }
    }
}
