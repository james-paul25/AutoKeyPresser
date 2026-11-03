package gui;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.KeySender;
import service.Presser;
import service.RobotKeySender;
import service.WindowFinder;
import service.WindowInfo;

import java.util.ArrayList;
import java.util.List;

public class AutoKeyPresserGUI extends VBox {

    private static final Color GRAY = Color.web("#6b7285");
    private static final Color GREEN = Color.web("#2fbf71");
    private static final Color RED = Color.web("#e5484d");

    private static final double MINI_WIDTH = 480;
    private static final double MINI_HEIGHT = 56;

    private final Stage stage;
    private Stage miniStage;

    private final TextField[] keyFields = new TextField[5];
    private final TextField afterKeyField = numberField("0.5");
    private final TextField afterAllField = numberField("5");
    private final TextField startDelayField = numberField("3");
    private final CheckBox onTopBox = new CheckBox("Keep window on top");
    private final TextField titleFilterField = new TextField("Artix");
    private final Button findButton = new Button("Find");
    private final ComboBox<WindowInfo> targetBox = new ComboBox<>();

    // main UI's
    private final Circle statusDot = new Circle(5, GRAY);
    private final Label statusLabel = new Label("Idle");
    private final Button startButton = new Button("Start");
    private final Button stopButton = new Button("Stop");
    private final Button miniButton = new Button("Mini mode");

    // mini UI's
    private final Circle miniDot = new Circle(5, GRAY);
    private final Label miniLabel = new Label("Idle");
    private final Button miniStartButton = new Button("Start");
    private final Button miniStopButton = new Button("Stop");
    private final Button expandButton = new Button("Expand");

    public AutoKeyPresserGUI(Stage stage) {
        this.stage = stage;

        getStyleClass().add("root-pane");
        setSpacing(14);
        setPadding(new Insets(18));
        setPrefWidth(580);

        HBox bottomRow = new HBox(14, buildTimingCard(), buildControlCard());
        getChildren().addAll(buildHeader(), buildTargetCard(), buildKeyCard(), bottomRow);

        findButton.setOnAction(e -> refreshWindows());
        onTopBox.selectedProperty().addListener((obs, old, on) -> stage.setAlwaysOnTop(on));

        startButton.setOnAction(e -> onStart());
        miniStartButton.setOnAction(e -> onStart());
        stopButton.setOnAction(e -> onStop());
        miniStopButton.setOnAction(e -> onStop());
        miniButton.setOnAction(e -> showMini());
        expandButton.setOnAction(e -> showMain());

        setIdleUi();
    }

    private Node buildHeader() {
        Label title = new Label("Presser");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Presses your key sequence on loop until you stop it for AQW, peaceyow.");
        subtitle.getStyleClass().add("app-subtitle");
        return new VBox(2, title, subtitle);
    }

    private Node buildTargetCard() {
        titleFilterField.setPromptText("Window title contains…");
        HBox.setHgrow(titleFilterField, Priority.ALWAYS);
        findButton.getStyleClass().add("find-button");
        HBox searchRow = new HBox(8, titleFilterField, findButton);

        targetBox.getItems().add(WindowInfo.FOCUSED);
        targetBox.getSelectionModel().selectFirst();
        targetBox.setMaxWidth(Double.MAX_VALUE);

        return card("Target window", "keys go only to this window", new VBox(10, searchRow, targetBox));
    }

    private Node buildKeyCard() {
        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER);

        for (int i = 0; i < keyFields.length; i++) {
            TextField tf = new TextField();
            tf.getStyleClass().add("key-field");
            tf.setPrefSize(60, 60);
            tf.setTextFormatter(new TextFormatter<>(c -> c.getControlNewText().length() <= 1 ? c : null));
            keyFields[i] = tf;

            Label index = new Label("Key " + (i + 1));
            index.getStyleClass().add("key-index");

            VBox cell = new VBox(6, tf, index);
            cell.setAlignment(Pos.CENTER);
            row.getChildren().add(cell);
        }

        return card("Key sequence", "one character per box, empty boxes are skipped", row);
    }

    private Node buildTimingCard() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        addTimingRow(grid, 0, "After each key", afterKeyField, "sec");
        addTimingRow(grid, 1, "After all keys", afterAllField, "sec");
        addTimingRow(grid, 2, "Start delay", startDelayField, "sec");
        Tooltip.install(startDelayField, new Tooltip("Only used when typing into the focused window"));
        grid.add(onTopBox, 0, 3, 3, 1);
        GridPane.setMargin(onTopBox, new Insets(6, 0, 0, 0));

        VBox card = card("Timing", "in seconds, e.g. 0.5", grid);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private Node buildControlCard() {
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);
        statusLabel.setMinHeight(54);
        statusLabel.setPrefHeight(54);
        statusLabel.setAlignment(Pos.TOP_LEFT);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        HBox.setMargin(statusDot, new Insets(4, 0, 0, 0));

        HBox status = new HBox(8, statusDot, statusLabel);
        status.setAlignment(Pos.TOP_LEFT);

        startButton.getStyleClass().addAll("start-button", "control-button");
        stopButton.getStyleClass().addAll("stop-button", "control-button");
        miniButton.getStyleClass().add("find-button");
        for (Button b : new Button[] { startButton, stopButton, miniButton }) {
            b.setMaxWidth(Double.MAX_VALUE);
        }
        HBox.setHgrow(startButton, Priority.ALWAYS);
        HBox.setHgrow(stopButton, Priority.ALWAYS);

        VBox box = new VBox(10, status, new HBox(8, stopButton, startButton), miniButton);
        box.getStyleClass().add("card");
        box.setMinWidth(190);
        box.setPrefWidth(190);
        box.setMaxWidth(190);
        return box;
    }

    private static VBox card(String title, String hint, Node content) {
        Label t = new Label(title);
        t.getStyleClass().add("card-title");

        HBox titleRow = new HBox(10, t);
        titleRow.setAlignment(Pos.BASELINE_LEFT);
        if (hint != null) {
            Label h = new Label(hint);
            h.getStyleClass().add("card-hint");
            titleRow.getChildren().add(h);
        }

        VBox box = new VBox(10, titleRow, content);
        box.getStyleClass().add("card");
        return box;
    }

    private static void addTimingRow(GridPane grid, int row, String label, TextField field, String unit) {
        Label l = new Label(label);
        l.getStyleClass().add("field-label");
        l.setMinWidth(96);
        Label u = new Label(unit);
        u.getStyleClass().add("card-hint");
        grid.addRow(row, l, field, u);
    }

    private static TextField numberField(String initial) {
        TextField tf = new TextField(initial);
        tf.setPrefWidth(80);
        tf.setTextFormatter(
                new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,4}([.,]\\d{0,3})?") ? c : null));
        return tf;
    }

    // sec to ms converter method
    private static int secondsToMillis(String text) {
        double seconds = Double.parseDouble(text.trim().replace(',', '.'));
        if (seconds < 0)
            throw new NumberFormatException("negative");
        return (int) Math.round(seconds * 1000);
    }

    // mini UI mode
    private Stage buildMiniStage() {
        miniLabel.getStyleClass().add("status-label");
        miniLabel.setMinWidth(0);
        miniLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(miniLabel, Priority.ALWAYS);

        miniStartButton.getStyleClass().addAll("start-button", "mini-button");
        miniStopButton.getStyleClass().addAll("stop-button", "mini-button");
        expandButton.getStyleClass().addAll("find-button", "mini-button");

        HBox root = new HBox(10, miniDot, miniLabel, miniStopButton, miniStartButton, expandButton);
        root.getStyleClass().add("mini-root");
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(0, 12, 0, 16));
        root.setPrefSize(MINI_WIDTH, MINI_HEIGHT);

        // Drag the bar by any empty part of it
        double[] grab = new double[2];
        root.setOnMousePressed(e -> {
            grab[0] = e.getSceneX();
            grab[1] = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            miniStage.setX(e.getScreenX() - grab[0]);
            miniStage.setY(e.getScreenY() - grab[1]);
        });

        boolean transparent = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        Scene scene = new Scene(root, MINI_WIDTH, MINI_HEIGHT);
        scene.setFill(transparent ? Color.TRANSPARENT : Color.web("#1c1f28"));
        scene.getStylesheets().addAll(stage.getScene().getStylesheets());

        Stage mini = new Stage();
        mini.initStyle(transparent ? StageStyle.TRANSPARENT : StageStyle.UNDECORATED);
        mini.setTitle("Presser");
        mini.setAlwaysOnTop(true);
        mini.setResizable(false);
        mini.setScene(scene);
        mini.setOnCloseRequest(e -> Presser.stopAutoKey());

        Rectangle2D area = Screen.getPrimary().getVisualBounds();
        mini.setX(area.getMaxX() - MINI_WIDTH - 24);
        mini.setY(area.getMaxY() - MINI_HEIGHT - 24);
        return mini;
    }

    private void showMini() {
        if (miniStage == null)
            miniStage = buildMiniStage();
        miniStage.show();
        stage.hide();
    }

    private void showMain() {
        stage.show(); // same rule in reverse
        stage.toFront();
        miniStage.hide();
    }

    private void refreshWindows() {
        try {
            List<WindowInfo> found = WindowFinder.find(titleFilterField.getText().trim());
            targetBox.getItems().setAll(WindowInfo.FOCUSED);
            targetBox.getItems().addAll(found);
            if (found.isEmpty()) {
                targetBox.getSelectionModel().selectFirst();
                showError("No window found. Is the launcher open?");
            } else {
                targetBox.getSelectionModel().select(1);
                setStatus(GRAY, "Found " + found.size() + " window(s).", false);
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
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

        WindowInfo target = targetBox.getValue();
        KeySender sender;
        int effectiveDelay = startDelay;
        try {
            if (target == null || target.isGlobal()) {
                sender = new RobotKeySender();
            } else {
                sender = WindowFinder.senderFor(target);
                effectiveDelay = 0;
            }
        } catch (Exception ex) {
            showError("Can't start: " + ex.getMessage());
            return;
        }

        setRunningUi(target);
        Presser.startAutoKey(keys, afterKey, afterAll, effectiveDelay, sender,
                () -> Platform.runLater(this::setIdleUi),
                msg -> Platform.runLater(() -> {
                    setIdleUi();
                    showError(msg);
                }));
    }

    private void onStop() {
        setStatus(GRAY, "Stopping…", false);
        stopButton.setDisable(true);
        miniStopButton.setDisable(true);
        Presser.stopAutoKey(); // UI returns to idle via the onStopped callback
    }

    private void setIdleUi() {
        setStatus(GRAY, "Idle", false);
        setButtons(false);
        setInputsDisabled(false);
    }

    private void setRunningUi(WindowInfo target) {
        String text = (target == null || target.isGlobal())
                ? "Running - switch to your target window"
                : "Running in background -> " + target.title();
        setStatus(GREEN, text, false);
        setButtons(true);
        setInputsDisabled(true);
    }

    private void showError(String message) {
        setStatus(RED, message, true);
    }

    private void setStatus(Color color, String text, boolean error) {
        statusDot.setFill(color);
        miniDot.setFill(color);
        for (Label l : new Label[] { statusLabel, miniLabel }) {
            l.setText(text);
            l.getStyleClass().remove("status-error");
            if (error)
                l.getStyleClass().add("status-error");
        }
    }

    private void setButtons(boolean running) {
        startButton.setDisable(running);
        miniStartButton.setDisable(running);
        stopButton.setDisable(!running);
        miniStopButton.setDisable(!running);
    }

    private void setInputsDisabled(boolean disabled) {
        for (TextField f : keyFields)
            f.setDisable(disabled);
        afterKeyField.setDisable(disabled);
        afterAllField.setDisable(disabled);
        startDelayField.setDisable(disabled);
        titleFilterField.setDisable(disabled);
        findButton.setDisable(disabled);
        targetBox.setDisable(disabled);
    }
}