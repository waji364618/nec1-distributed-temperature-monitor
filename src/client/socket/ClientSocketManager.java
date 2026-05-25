package client.socket;

import client.model.SensorStatistics;
import client.model.TemperatureSensor;
import shared.JsonUtil;
import shared.Message;
import shared.MessageType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

/*
 * Handles all socket communication
 * between client and server.
 *
 * This class is only responsible
 * for networking logic.
 */
public class ClientSocketManager {

    // Socket connection
    private Socket socket;

    // Input and output streams
    private BufferedReader in;
    private PrintWriter out;

    // Controls thread execution
    private boolean running = true;

    // Temperature sensor model
    private TemperatureSensor sensor;

    // Statistics model
    private SensorStatistics statistics;

    // Listener used for communication
    // with the ViewModel
    private SocketListener listener;

    /*
     * Constructor creates connection
     * to server and starts threads.
     */
    public ClientSocketManager(
            String host,
            int port,
            String sensorId,
            SocketListener listener)
    {
        this.listener = listener;

        System.out.println("Connecting to server...");

        try
        {
            // Create socket connection
            socket = new Socket(host, port);

            System.out.println("Connected to server!");

            // Create streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            // Create sensor and statistics
            sensor = new TemperatureSensor(sensorId, 3000);

            statistics = new SensorStatistics();

            Random random = new Random();

            /*
             * Thread responsible for listening
             * to messages from server.
             */
            Thread listenerThread = new Thread(() ->
            {
                try
                {
                    while (running)
                    {
                        String response = in.readLine();

                        // Connection closed
                        if (response == null)
                        {
                            break;
                        }

                        Message serverMessage = JsonUtil.fromJson(response);

                        // Broadcast message
                        if (serverMessage.getType() == MessageType.BROADCAST)
                        {
                            listener.onBroadcastReceived(serverMessage.getClientId());
                        }

                        // Interval update
                        else if (serverMessage.getType() == MessageType.CHANGE_INTERVAL)
                        {
                            sensor.setInterval(serverMessage.getInterval());

                            listener.onIntervalChanged(sensor.getInterval());
                        }
                    }
                }
                catch (IOException e)
                {
                    if (running)
                    {
                        listener.onConnectionLost();
                    }
                }
            });

            listenerThread.setDaemon(true);

            listenerThread.start();

            /*
             * Main loop for sending
             * temperature data to server.
             */
            while (running)
            {
                // Generate random temperature
                double temperature = Math.round((15 + random.nextDouble() * 15) * 100.0) / 100.0;

                // Update sensor
                sensor.setTemperature(temperature);

                // Update statistics
                statistics.addTemperature(temperature);
                listener.onTemperatureGenerated(
                        temperature,
                        statistics.getAverageTemperature(),
                        statistics.getHighestTemperature(),
                        statistics.getMeasurementCount());

                // Create message
                Message message = new Message(
                                MessageType.TEMPERATURE,
                                sensor.getSensorId(),
                                sensor.getTemperature());

                // Convert to JSON
                String json = JsonUtil.toJson(message);

                // Send to server
                out.println(json);

                System.out.println("Temperature sent: " + temperature);

                // Wait before next measurement
                Thread.sleep(sensor.getInterval());
            }
        }
        catch (IOException e)
        {
            System.out.println("Failed to connect to server.");
        }
        catch (InterruptedException e)
        {
            System.out.println("Thread interrupted.");
        }
    }

    /*
     * Safely disconnects client
     * from server.
     */
    public void disconnect()
    {
        running = false;

        try
        {
            if (in != null)
            {
                in.close();
            }

            if (out != null)
            {
                out.close();
            }

            if (socket != null)
            {
                socket.close();
            }

            System.out.println("Disconnected from server.");
        }
        catch (IOException e)
        {
            System.out.println("Failed to close connection.");
        }
    }
}