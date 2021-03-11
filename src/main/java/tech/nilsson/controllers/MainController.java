package tech.nilsson.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import tech.nilsson.thinkgear.Format;
import tech.nilsson.thinkgear.ThinkGearConnection;
import tech.nilsson.thinkgear.events.BlinkEvent;
import tech.nilsson.thinkgear.events.ESenseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements ThinkGearConnection.Listener, Initializable {
    public static String DEFAULT_THINK_GEAR_HOST = "localhost";
    public static int DEFAULT_THINK_GEAR_PORT = 13854;
    private ThinkGearConnection thinkGearConnection;

    @FXML
    private ProgressBar attentionProgressBar;
    @FXML
    private ProgressBar meditationProgressBar;
    @FXML
    private ProgressBar signalQualityProgressBar;
    @FXML
    private Label attentionValueLabel;
    @FXML
    private Label meditationValueLabel;
    @FXML
    private Label blinkStrengthValueLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            thinkGearConnection = new ThinkGearConnection(DEFAULT_THINK_GEAR_HOST, DEFAULT_THINK_GEAR_PORT);
            Format requestedFormat = new Format(false, "json");
            thinkGearConnection.send(requestedFormat);
            new Thread(thinkGearConnection).start();
            thinkGearConnection.addListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceived(ESenseEvent eSenseEvent) {
        Platform.runLater(() -> {
            double normalizedAttention = (double)eSenseEvent.geteSense().getAttention() / 100;
            double normalizedMeditation = (double)eSenseEvent.geteSense().getMeditation() / 100;
            double normalizedSignalQuality = (double)(eSenseEvent.geteSense().getPoorSignalLevel()) / 200;
            System.out.println(eSenseEvent.geteSense().getPoorSignalLevel());
            attentionProgressBar.setProgress(normalizedAttention);
            meditationProgressBar.setProgress(normalizedMeditation);
            signalQualityProgressBar.setProgress(normalizedSignalQuality);
            String attentionAsString = Integer.toString(eSenseEvent.geteSense().getAttention());
            String meditationAsString = Integer.toString(eSenseEvent.geteSense().getMeditation());
            attentionValueLabel.setText(attentionAsString);
            meditationValueLabel.setText(meditationAsString);
        });
    }

    @Override
    public void onReceived(BlinkEvent blinkEvent) {
        Platform.runLater(() -> {
            String blinkStrengthAsString = Integer.toString(blinkEvent.getBlinkStrength());
            blinkStrengthValueLabel.setText(blinkStrengthAsString);
        });
    }
}
