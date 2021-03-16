package tech.nilsson.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import tech.nilsson.interaction_schemes.HighBlinkingInteractionScheme;
import tech.nilsson.interaction_schemes.InteractionScheme;
import tech.nilsson.interaction_schemes.LowBlinkingInteractionScheme;
import tech.nilsson.thinkgear.Format;
import tech.nilsson.thinkgear.ThinkGearConnection;
import tech.nilsson.thinkgear.events.BlinkEvent;
import tech.nilsson.thinkgear.events.ESenseEvent;
import tech.nilsson.utils.Interval;
import tech.nilsson.utils.SelectionManager;
import tech.nilsson.utils.intervals.ChangeAttentionIncreasersInterval;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ResourceBundle;

public class MainController implements ThinkGearConnection.Listener, Initializable {
    public static String DEFAULT_THINK_GEAR_HOST = "localhost";
    public static int DEFAULT_THINK_GEAR_PORT = 13854;
    private ThinkGearConnection thinkGearConnection;

    private static final int ATTENTION_INCREASER_WAIT_TIME = 4 * 1000;

    private SelectionManager selectionManager;
    private InteractionScheme currentInteractionScheme;

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
    @FXML
    private TextField simpleAttentionIncreaserTextField;
    @FXML
    private TextField hardAttentionIncreaserTextField;
    @FXML
    private TextField strongBlinkThresholdTextField;
    @FXML
    private TextField weakBlinkThresholdTextField;
    @FXML
    private RadioButton lowBlinkRadioButton;
    @FXML
    private RadioButton highBlinkRadioButton;

    // Devices
    @FXML
    private Pane bedroomLampDevice;
    @FXML
    private Pane bedroomFanDevice;
    @FXML
    private Pane bathroomLampDevice;
    @FXML
    private Pane alarmDevice;
    @FXML
    private Pane tvDevice;
    // -----

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            thinkGearConnection = new ThinkGearConnection(DEFAULT_THINK_GEAR_HOST, DEFAULT_THINK_GEAR_PORT);
            Format requestedFormat = new Format(false, "json");
            thinkGearConnection.send(requestedFormat);
            new Thread(thinkGearConnection).start();
            thinkGearConnection.addListener(this);

            Interval changeMathEquationsInterval = new Interval(
                    new ChangeAttentionIncreasersInterval(simpleAttentionIncreaserTextField, hardAttentionIncreaserTextField),
                    ATTENTION_INCREASER_WAIT_TIME
            );
            new Thread(changeMathEquationsInterval).start();

            selectionManager = new SelectionManager(
                    this::resetSelectedStyling,
                    this::highlightSelected,
                    bedroomLampDevice,
                    bedroomFanDevice,
                    bathroomLampDevice,
                    alarmDevice,
                    tvDevice
            );
            highlightSelected();

            currentInteractionScheme = new HighBlinkingInteractionScheme(
                    selectionManager,
                    Integer.parseInt(weakBlinkThresholdTextField.getText()),
                    Integer.parseInt(strongBlinkThresholdTextField.getText()));
            thinkGearConnection.addListener(currentInteractionScheme);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void highlightSelected() {
        Node selectedDevice = selectionManager.getSelectedNode();
        selectedDevice.setStyle("-fx-border-color:  #8e44ad; -fx-border-width:  3;");
    }

    private void resetSelectedStyling() {
        Node selectedDevice = selectionManager.getSelectedNode();
        selectedDevice.setStyle("-fx-border-color:  gray; -fx-border-width:  1;");
    }

    @FXML
    private void onChangeInteractionScheme() {
        currentInteractionScheme.stop();
        thinkGearConnection.removeListener(currentInteractionScheme);
        if (lowBlinkRadioButton.isSelected()) {
            currentInteractionScheme = new LowBlinkingInteractionScheme(
                    selectionManager,
                    Integer.parseInt(weakBlinkThresholdTextField.getText()),
                    Integer.parseInt(strongBlinkThresholdTextField.getText())
            );
        } else {
            currentInteractionScheme = new HighBlinkingInteractionScheme(
                    selectionManager,
                    Integer.parseInt(weakBlinkThresholdTextField.getText()),
                    Integer.parseInt(strongBlinkThresholdTextField.getText())
            );
        }
        thinkGearConnection.addListener(currentInteractionScheme);
    }

    @FXML
    private void onSetBlinkThreshold() {
        int weakThreshold = Integer.parseInt(weakBlinkThresholdTextField.getText());
        int strongThreshold = Integer.parseInt(strongBlinkThresholdTextField.getText());
        // if weak <= strong -> error
        currentInteractionScheme.setWeakBlinkThreshold(weakThreshold);
        currentInteractionScheme.setStrongBlinkThreshold(strongThreshold);
    }

    @Override
    public void onReceived(ESenseEvent eSenseEvent) {
        Platform.runLater(() -> {
            double normalizedAttention = (double)eSenseEvent.geteSense().getAttention() / 100;
            double normalizedMeditation = (double)eSenseEvent.geteSense().getMeditation() / 100;
            double normalizedSignalQuality = (double)(eSenseEvent.geteSense().getPoorSignalLevel()) / 200;
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
