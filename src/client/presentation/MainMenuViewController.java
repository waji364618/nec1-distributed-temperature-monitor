package client.presentation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainMenuViewController {

    @FXML
    private TextField sensorIdField;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private ListView<String> temperatureList;

    @FXML
    private Label averageLabel;

    @FXML
    private Label highestLabel;

    @FXML
    private Label measurementLabel;

    @FXML
    private Label warningLabel;

    private MainMenuViewModel viewModel;

    @FXML
    public void initialize()
    {
        viewModel = new MainMenuViewModel();
    }

    //Metode der kører når brugeren klikker på Connect
    @FXML
    public void connectToServer(ActionEvent event)
    {
        if (sensorIdField.getText().isEmpty())
        {
            warningLabel.setText("Enter sensor ID");
            return;
        }

        String sensorId = sensorIdField.getText();

        // Vis status til brugeren
        warningLabel.setText("Connecting sensor: " + sensorId);

        connectButton.setDisable(true);

        disconnectButton.setDisable(false);

        // Forbind klient til server
        viewModel.connectToServer(
                sensorId,
                temperatureList,
                averageLabel,
                highestLabel,
                measurementLabel,
                warningLabel);
    }

    @FXML
    public void disconnectFromServer(ActionEvent event)
    {
        warningLabel.setText("Disconnected.");

        connectButton.setDisable(false);

        disconnectButton.setDisable(true);

        viewModel.disconnect();
    }
}
