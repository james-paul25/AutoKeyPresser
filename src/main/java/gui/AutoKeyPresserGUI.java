package gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import service.Presser;

import java.util.ArrayList;
import java.util.List;

public class AutoKeyPresserGUI extends VBox {

    private static final Color GRAY = Color.web("#6b7285");
    private static final Color GREEN = Color.web("#2fbf71");
    private static final Color RED = Color.web("#e5484d");

    private final TextField[] keyFields = new TextField[5];
    private final TextField afterKeyField = numberField("0.5");
    private final TextField afterAllField = numberField("5");
    private final TextField startDelayField = numberField("3");
    private final CheckBox onTopBox = new CheckBox("Keep window on top");

    private final Circle statusDot = new Circle(5, GRAY);
    private final Label statusLabel = new Label("Idle");
    private final Button startButton = new Button("Start");
    private final Button stopButton = new Button("Stop");

    public AutoKeyPresserGUI(Stage stage) {
        getStyleClass().add("root-pane");
        setSpacing(14);
        setPadding(new Insets(20));
        setPrefWidth(460);

        getChildren().addAll(buildHeader(), buildKeyCard(), buildTimingCard(), buildStatusBar());

        onTopBox.selectedProperty().addListener((obs, old, on) -> stage.setAlwaysOnTop(on));
        startButton.setOnAction(e -> onStart());
        stopButton.setOnAction(e -> onStop());
        setIdleUi();
    }

    private Node buildHeader() {
        Label title = new Label("Auto Key Presser");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Presses your key sequence on loop until you stop it.");
        subtitle.getStyleClass().add("app-subtitle");
        return new VBox(2, title, subtitle);
    }

    private Node buildKeyCard() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);

        for (int i = 0; i < keyFields.length; i++) {
            TextField tf = new TextField();
            tf.getStyleClass().add("key-field");
            tf.setPrefSize(58, 58);
            tf.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().length() <= 1 ? c : null));
            keyFields[i] = tf;

            Label index = new Label("Key " + (i + 1));
            index.getStyleClass().add("key-index");

            VBox cell = new VBox(6, tf, index);
            cell.setAlignment(Pos.CENTER);
            row.getChildren().add(cell);
        }

        return card("Key sequence", "One character per box. Empty boxes are skipped.", row);
    }

    private Node buildTimingCard() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        addTimingRow(grid, 0, "After each key", afterKeyField, "seconds  (0.5 = half a second)");
        addTimingRow(grid, 1, "After all keys", afterAllField, "seconds  (5 = five seconds)");
        addTimingRow(grid, 2, "Start delay", startDelayField, "seconds  (time to click your game)");
        grid.add(onTopBox, 0, 3, 3, 1);
        GridPane.setMargin(onTopBox, new Insets(6, 0, 0, 0));

        return card("Timing", null, grid);
    }

    private Node buildStatusBar() {
        statusLabel.getStyleClass().add("status-label");
        startButton.getStyleClass().add("start-button");
        stopButton.getStyleClass().add("stop-button");

        HBox status = new HBox(8, statusDot, statusLabel);
        status.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(10, status, spacer, stopButton, startButton);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(4, 0, 0, 0));
        return bar;
    }

    private static VBox card(String title, String hint, Node content) {
        VBox box = new VBox(10);
        box.getStyleClass().add("card");

        Label t = new Label(title);
        t.getStyleClass().add("card-title");
        box.getChildren().add(t);

        if (hint != null) {
            Label h = new Label(hint);
            h.getStyleClass().add("card-hint");
            box.getChildren().add(h);
        }
        box.getChildren().add(content);
        return box;
    }

    private static int secondsToMillis(String text) {
        double seconds = Double.parseDouble(text.trim().replace(',', '.'));
        if (seconds < 0)
            throw new NumberFormatException("negative");
        return (int) Math.round(seconds * 1000);
    }

    private static void addTimingRow(GridPane grid, int row, String label, TextField field, String unit) {
        Label l = new Label(label);
        l.getStyleClass().add("field-label");
        l.setMinWidth(100);
        Label u = new Label(unit);
        u.getStyleClass().add("card-hint");
        grid.addRow(row, l, field, u);
    }

    private static TextField numberField(String initial) {
        TextField tf = new TextField(initial);
        tf.setPrefWidth(100);
        tf.setTextFormatter(
                new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,4}([.,]\\d{0,3})?") ? c : null));
        return tf;
    }

    private void onStart() {
        List<String> keys = new ArrayList<>();
        for (TextField f : keyFields) {
            if (!f.getText().isEmpty())
                keys.add(f.getText());
        }
        if (keys.isEmpty()) {
            showError("Enter at least one key.");
            return;
        }

        int afterKey, afterAll, startDelay;
        try {
            afterKey = secondsToMillis(afterKeyField.getText());
            afterAll = secondsToMillis(afterAllField.getText());
            startDelay = secondsToMillis(startDelayField.getText());
        } catch (NumberFormatException ex) {
            showError("Timing fields must be numbers, e.g. 0.5 or 3.");
            return;
        }
        if (afterAll < 10) {
            showError("'After all keys' must be at least 0.01 seconds.");
            return;
        }

        setRunningUi();
        Presser.startAutoKey(keys, afterKey, afterAll, startDelay,
                () -> Platform.runLater(this::setIdleUi),
                msg -> Platform.runLater(() -> {
                    setIdleUi();
                    showError(msg);
                }));
    }

    private void onStop() {
        statusLabel.setText("Stopping…");
        stopButton.setDisable(true);
        Presser.stopAutoKey(); // UI returns to idle via the onStopped callback
    }

    private void setIdleUi() {
        setStatus(GRAY, "Idle", false);
        startButton.setDisable(false);
        stopButton.setDisable(true);
        setInputsDisabled(false);
    }

    private void setRunningUi() {
        setStatus(GREEN, "Running — switch to your target window", false);
        startButton.setDisable(true);
        stopButton.setDisable(false);
        setInputsDisabled(true);
    }

    private void showError(String message) {
        setStatus(RED, message, true);
    }

    private void setStatus(Color color, String text, boolean error) {
        statusDot.setFill(color);
        statusLabel.setText(text);
        statusLabel.getStyleClass().remove("status-error");
        if (error)
            statusLabel.getStyleClass().add("status-error");
    }

    private void setInputsDisabled(boolean disabled) {
        for (TextField f : keyFields)
            f.setDisable(disabled);
        afterKeyField.setDisable(disabled);
        afterAllField.setDisable(disabled);
        startDelayField.setDisable(disabled);
    }

}