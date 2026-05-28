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

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    private final SensorStatistics statistics = new SensorStatistics();

    private boolean warningSent = false;

        public ServerClientHandler(Socket socket) {
        this.socket = socket;

        try {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Failed to create streams.");
        }
    }

    @Override
    public void run() {
            System.out.println("Client handler thread started!");

        try {
            while (true) {

                String request = in.readLine();

                if (request == null) {
                    break;
                }

                // Convert JSON to Message object
                Message message = JsonUtil.fromJson(request);

                // Update statistics
                statistics.addTemperature(message.getTemperature());


                System.out.println("Client " + message.getClientId() + " sent temperature: " + message.getTemperature());

                System.out.println("Average temperature: " + Math.round(statistics.getAverageTemperature() * 100.0) / 100.0);

                System.out.println("Highest temperature: " + statistics.getHighestTemperature());

                System.out.println("Measurements: " + statistics.getMeasurementCount());


                if (message.getTemperature() > 25) {

                    if (!warningSent) {
                        warningSent = true;

                        System.out.println("High temperature detected.");

                        Message intervalMessage = new Message(MessageType.CHANGE_INTERVAL, 2000);

                        String intervalJson = JsonUtil.toJson(intervalMessage);

                        out.println(intervalJson);

                        System.out.println("Interval changed to 2000 ms.");


                        synchronized (ServerSocketManager.clients) {
                            for (ServerClientHandler client : ServerSocketManager.clients) {
                                Message broadcastMessage =
                                        new Message(MessageType.BROADCAST, "High temperature detected!");

                                String broadcastJson = JsonUtil.toJson(broadcastMessage);

                                client.sendMessage(broadcastJson);
                            }
                        }
                    }
                }

                /*
                 * Reset warning state when
                 * temperature becomes stable again.
                 */
                else if (message.getTemperature() < 23) {
                    warningSent = false;
                }
            }

            System.out.println("Client disconnected.");

            // Remove client from list
            ServerSocketManager.clients.remove(this);


            socket.close();
        } catch (IOException e) {
            System.out.println("Connection lost.");
        }
    }

    public synchronized void sendMessage(String message) {
        out.println(message);
    }
}