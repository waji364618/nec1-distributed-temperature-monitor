package client.presentation;

import client.socket.ClientSocketManager;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class MainMenuViewModel {

    // Gemmer socket manager
    private ClientSocketManager socketManager;

    // Gemmer thread reference
    private Thread currentThread;

    // Opret forbindelse til server
    public void connectToServer(
            String sensorId,
            ListView<String> temperatureList,
            Label averageLabel,
            Label highestLabel,
            Label measurementLabel,
            Label warningLabel)
    {
        currentThread = new Thread(() ->
        {
            socketManager = new ClientSocketManager(
                    "localhost",
                    6789,
                    sensorId,
                    temperatureList,
                    averageLabel,
                    highestLabel,
                    measurementLabel,
                    warningLabel);
        });

        // Gør thread til daemon
        currentThread.setDaemon(true);

        // Start thread
        currentThread.start();
    }

    // Disconnect fra server
    public void disconnect()
    {
        if (socketManager != null)
        {
            socketManager.disconnect();
        }

        // Stop thread
        if (currentThread != null)
        {
            currentThread.interrupt();
        }
    }
}