package server.socket;

import server.model.SensorStatistics;
import shared.JsonUtil;
import shared.Message;
import shared.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClientHandler implements Runnable {

    // Socket forbindelse til client
    private final Socket socket;

    // Input stream fra client
    private  BufferedReader in;

    // Output stream til client
    private PrintWriter out;

    // Statistik objekt til temperaturdata
    private final SensorStatistics statistics = new SensorStatistics();

    // Sørger for at warning kun sendes en gang
    private boolean warningSent = false;


     // Constructor

    public ServerClientHandler(Socket socket)
    {
        this.socket = socket;

        try
        {
            // Opret input stream
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Opret output stream
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException e)
        {
            System.out.println("Failed to create streams.");
        }
    }


     // Kører i separat thread for hver client

    @Override
    public void run()
    {
        System.out.println("Client handler thread started!");

        try
        {
            while (true)
            {
                // Læs besked fra client
                String request = in.readLine();

                // Hvis client disconnecter
                if (request == null)
                {
                    break;
                }

                // Konverter JSON til Message objekt
                Message message = JsonUtil.fromJson(request);

                // Tilføj temperatur til statistik
                statistics.addTemperature(message.getTemperature());

                // Print temperatur information
                System.out.println("Client " + message.getClientId() + " sent temperature: " + message.getTemperature());

                System.out.println("Average temperature: " + Math.round(statistics.getAverageTemperature() * 100.0) / 100.0);

                System.out.println("Highest temperature: " + statistics.getHighestTemperature());

                System.out.println("Measurements: " + statistics.getMeasurementCount());

                // Hvis temperatur er over 25 grader
                if (message.getTemperature() > 25)
                {
                    if (!warningSent)
                    {
                        warningSent = true;

                        // Send nyt interval til client
                        Message intervalMessage =
                                new Message(MessageType.CHANGE_INTERVAL, 1000);

                        String intervalJson =
                                JsonUtil.toJson(intervalMessage);

                        out.println(intervalJson);

                        // Send broadcast til alle clients
                        synchronized (ServerSocketManager.clients)
                        {
                            for (ServerClientHandler client
                                    : ServerSocketManager.clients)
                            {
                                Message broadcastMessage =
                                        new Message(
                                                MessageType.BROADCAST,
                                                "High temperature detected!"
                                        );

                                String broadcastJson =
                                        JsonUtil.toJson(broadcastMessage);

                                client.sendMessage(broadcastJson);
                            }
                        }
                    }
                }
                else
                {
                    warningSent = false;
                }
            }

            // Client disconnected
            System.out.println("Client disconnected.");

            // Fjern client fra client listen
            ServerSocketManager.clients.remove(this);
            socket.close();
        }
        catch (IOException e)
        {
            System.out.println("Failed to create streams.");
        }
    }

    /*
     * Sender besked til client
     * synchronized beskytter mod race conditions
     */
    public synchronized void sendMessage(String message)
    {
        out.println(message);
    }
}