package client.socket;

import client.model.SensorStatistics;
import client.model.TemperatureSensor;
import javafx.application.Platform;
import shared.JsonUtil;
import shared.Message;
import shared.MessageType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ClientSocketManager {

    // Socket forbindelse til server
    private Socket socket;

    // Reader og writer streams
    private BufferedReader in;
    private PrintWriter out;

    // Bruges til at stoppe threads sikkert
    private volatile boolean running = true;

    // Temperatur sensor
    private TemperatureSensor sensor;

    // Statistik system
    private SensorStatistics statistics;

    public ClientSocketManager(
            String host,
            int port,
            String sensorId,
            ListView<String> temperatureList,
            Label averageLabel,
            Label highestLabel,
            Label measurementLabel,
            Label warningLabel) {
        System.out.println("Connecting to server...");

        try {
            // Opret socket forbindelse
            socket = new Socket(host, port);

            System.out.println("Connected to server!");

            // Streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            // Statistik objekt
            statistics = new SensorStatistics();

            // Sensor objekt
            sensor = new TemperatureSensor(sensorId, 3000);

            Random random = new Random();


            // THREAD 1 -> Lyt til server
            Thread listenerThread = new Thread(() ->
            {
                try {
                    while (running) {
                        String response = in.readLine();

                        if (!running) {
                            break;
                        }

                        if (response == null) {
                            break;
                        }
                        Message serverMessage = JsonUtil.fromJson(response);

                        // Broadcast besked
                        if (serverMessage.getType() == MessageType.BROADCAST) {
                            System.out.println("BROADCAST received!");

                            Platform.runLater(() ->
                            {
                                temperatureList.getItems().add("BROADCAST: " + serverMessage.getClientId());
                            });
                        }

                        // Interval ændring
                        else if (serverMessage.getType() == MessageType.CHANGE_INTERVAL) {sensor.setInterval(
                                    serverMessage.getInterval());

                            System.out.println("New interval: " + sensor.getInterval());
                        }
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Connection lost.");
                    }
                }
            });

            listenerThread.setDaemon(true);
            listenerThread.start();


            // THREAD 2 -> Send temperaturer
            while (running) {
                // Generer temperatur
                double temperature = Math.round((15 + random.nextDouble() * 15) * 100.0) / 100.0;

                sensor.setTemperature(temperature);

                statistics.addTemperature(temperature);

                // Opret temperature message
                Message message = new Message(MessageType.TEMPERATURE, sensor.getSensorId(), sensor.getTemperature());

                // Konverter til JSON
                String json = JsonUtil.toJson(message);

                // Send til server
                out.println(json);

                // Opdater GUI
                Platform.runLater(() ->
                {
                    temperatureList.getItems().add("Temperature: " + temperature);

                    averageLabel.setText(String.valueOf(Math.round(statistics.getAverageTemperature() * 100.0) / 100.0));

                    highestLabel.setText(String.valueOf(statistics.getHighestTemperature()));

                    measurementLabel.setText(String.valueOf(statistics.getMeasurementCount()));
                });

                // Vent før næste temperatur
                Thread.sleep(sensor.getInterval());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Client stopped.");
        }
    }

    // Luk client korrekt
    public synchronized void disconnect() {
        running = false;

        try {
            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Client disconnected.");
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }
}