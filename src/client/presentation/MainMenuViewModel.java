package client.presentation;

import client.socket.ClientSocketManager;
import client.socket.SocketListener;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class MainMenuViewModel implements SocketListener {

    // Socket manager reference
    private ClientSocketManager socketManager;

    // Thread used for socket connection
    private Thread currentThread;

    // References to JavaFX controls
    private ListView<String> temperatureList;
    private Label averageLabel;
    private Label highestLabel;
    private Label measurementLabel;
    private Label warningLabel;


    // Creates connection to server.

    public void connectToServer(
            String sensorId,
            ListView<String> temperatureList,
            Label averageLabel,
            Label highestLabel,
            Label measurementLabel,
            Label warningLabel)
    {
        // Save JavaFX references
        this.temperatureList = temperatureList;
        this.averageLabel = averageLabel;
        this.highestLabel = highestLabel;
        this.measurementLabel = measurementLabel;
        this.warningLabel = warningLabel;

        // Create thread for socket communication
        currentThread = new Thread(() ->
        {
            socketManager = new ClientSocketManager("localhost", 6789, sensorId, this);});

        // Daemon thread closes automatically
        // when application exits
        currentThread.setDaemon(true);

        currentThread.start();
    }


     // Disconnects from server safely.
    public void disconnect()
    {
        if (socketManager != null)
        {
            socketManager.disconnect();
        }

        if (currentThread != null)
        {
            currentThread.interrupt();
        }
    }

    /*
     * Called when broadcast message
     * is received from server.
     */
    @Override
    public void onBroadcastReceived(String message)
    {
        Platform.runLater(() ->
        {
            temperatureList.getItems().add("BROADCAST: " + message);});
    }


    //Called when interval changes.
    @Override
    public void onIntervalChanged(int newInterval)
    {
        Platform.runLater(() ->
        {
            warningLabel.setText("New interval: " + newInterval + " ms");
        });
    }


   // Called if connection is lost.
    @Override
    public void onConnectionLost()
    {
        Platform.runLater(() ->
        {
            warningLabel.setText("Connection to server lost.");});
    }

    @Override
    public void onTemperatureGenerated(
            double temperature,
            double average,
            double highest,
            int measurements)
    {
        Platform.runLater(() ->
        {
            // Add temperature to list
            temperatureList.getItems().add("Temperature: " + temperature);

            // Update statistics labels
            averageLabel.setText(String.valueOf(Math.round(average * 100.0) / 100.0));

            highestLabel.setText(String.valueOf(highest));

            measurementLabel.setText(String.valueOf(measurements));});
    }
}