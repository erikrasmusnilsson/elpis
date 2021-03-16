package tech.nilsson.utils.intervals;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import tech.nilsson.utils.Interval;

import java.security.SecureRandom;

public class ChangeAttentionIncreasersInterval implements Interval.Callback {
    private final TextField simpleMathOperationTextField;
    private final TextField hardMathOperationTextField;

    public ChangeAttentionIncreasersInterval(
            TextField simpleMathOperationTextField,
            TextField hardMathOperationTextField
    ) {
        this.simpleMathOperationTextField = simpleMathOperationTextField;
        this.hardMathOperationTextField = hardMathOperationTextField;
    }

    @Override
    public void callback() {
        SecureRandom random = new SecureRandom();
        int simpleA = random.nextInt(10);
        int simpleB = random.nextInt(10);
        int hardA = random.nextInt(10);
        int hardB = random.nextInt(10);
        char operatorSimple = random.nextInt(3) > 1 ? '+' : '-';
        char operatorHard = random.nextInt(3) > 1 ? '*' : '/';
        Platform.runLater(() -> {
            String simpleMathOperationAsString = String.format("%d %c %d", simpleA, operatorSimple, simpleB);
            simpleMathOperationTextField.setText(simpleMathOperationAsString);
            String hardMathOperationAsString = String.format("%d %c %d", hardA, operatorHard, hardB);
            hardMathOperationTextField.setText(hardMathOperationAsString);
        });
    }
}
