package net.ilexiconn.netconn.server;

import java.net.Socket;

public interface IServerListener {
    void onClientConnected(Server server, Socket client);

    void onClientDisconnected(Server server, Socket client);
}
