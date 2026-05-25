package server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerSocketManager {

    // Thread-safe liste med alle connected clients
    public static List<ServerClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());


     // Starter serveren
    public ServerSocketManager(int port)
    {
        System.out.println("Starting server...");

        try
        {
            // Server socket lytter på port
            ServerSocket welcomeSocket = new ServerSocket(port);

            while (true)
            {
                System.out.println("Waiting for client connection...");

                // Blocking metode:
                // server venter indtil en client connecter
                Socket socket = welcomeSocket.accept();

                System.out.println("Client connected!");

                // Opret client handler
                ServerClientHandler clientHandler = new ServerClientHandler(socket);

                // Tilføj client til listen
                clients.add(clientHandler);

                // Opret separat thread til client
                Thread thread = new Thread(clientHandler);

                // Start client thread
                thread.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}