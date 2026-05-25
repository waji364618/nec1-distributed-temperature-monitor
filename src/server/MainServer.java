package server;

import server.socket.ServerSocketManager;

public class MainServer {

    public static void main(String[] args)
    {
        new ServerSocketManager(6789);
    }
}
