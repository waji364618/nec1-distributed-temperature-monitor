package server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerSocketManager {

        public static List<ServerClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

         public ServerSocketManager(int port)
    {
         System.out.println("Starting server...");

        try
        {

            ServerSocket welcomeSocket = new ServerSocket(port);

            while (true)
            {
                System.out.println("Waiting for client connection...");


                Socket socket = welcomeSocket.accept();

                System.out.println("Client connected!");

                ServerClientHandler clientHandler = new ServerClientHandler(socket);

                clients.add(clientHandler);

                Thread thread = new Thread(clientHandler);

                thread.start();
            }
        }
          catch (IOException e)
        {
            System.out.println("Server failed to start.");
        }
    }
}