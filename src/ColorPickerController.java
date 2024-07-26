import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;

import java.util.List;

public class ColorPickerController implements ColorPickerEngine.TimerListener {
    @FXML private Button startStopBtn;
    @FXML private Label timeLabel;
    @FXML private Label scoreLabel;
    @FXML private Label colorLabel;
    @FXML private GridPane buttonGrid;
    @FXML private Label numOfColorsLabel;
    @FXML private ComboBox<String> diffiucltyCBox;
    @FXML private Button minusTimer;
    @FXML private Button plusTimer;
    @FXML private Button minusNumber;
    @FXML private Button plusNumber;


    private ColorPickerEngine gameEngine;
    private int timerDuration = 10;
    private int minDuration = 5;
    private int maxDuration = 30;

    private boolean gameStatus = false;
    private Button[] buttons;
    private int numOfButtons=4;

    private int gameLevel = 1;

    @FXML
    void startStopPressed(ActionEvent event) {
        if (!gameStatus) startGame();
        else stopGame();
    }

    @FXML
    void increaseColorsPressed(ActionEvent event) {
        if (numOfButtons < gameEngine.getMaxNumberOfColors()) {
            numOfButtons++;
        }
        generateButtons();
        numOfColorsLabel.setText(numOfButtons+"");
    }

    @FXML
    void reduceColorsPressed(ActionEvent event) {
        if (numOfButtons > 1) {
            numOfButtons--;
        }
        numOfColorsLabel.setText(numOfButtons+"");
        generateButtons();
    }

    @FXML
    void decreaseTimePressed(ActionEvent event) {
        if (timerDuration > minDuration) {
            timerDuration -= 5;
        }
        gameEngine.setTimerDuration(timerDuration);
    }

    @FXML
    void increaseTimePressed(ActionEvent event) {
        if (timerDuration < maxDuration) {
            timerDuration += 5;
        }
        gameEngine.setTimerDuration(timerDuration);
    }

    @FXML
    void initialize() {
        gameEngine = new ColorPickerEngine();
        gameEngine.setTimerListener(this); // Set the controller as the timer listener
        numOfColorsLabel.setText(numOfButtons+"");
        colorLabel.setText("");
        timeLabel.textProperty().bind(Bindings.createStringBinding(() -> gameEngine.timeLeftProperty().get() + "sec", gameEngine.timeLeftProperty()));
        scoreLabel.textProperty().bind(Bindings.createStringBinding(() -> gameEngine.scoreProperty().get()+"", gameEngine.scoreProperty()));
        resetGame(timerDuration);
        generateButtons();
        populateDifficulties();
        diffiucltyCBox.setValue("Select level");
        diffiucltyCBox.setOnAction(event -> {
            String level = diffiucltyCBox.getValue();
            setDiffiucltyLevel(level);
        });
    }

    private void generateButtons() {
        buttonGrid.getChildren().clear();
        buttonGrid.getColumnConstraints().clear();
        buttons = new Button[numOfButtons];
        ColumnConstraints colSize = new ColumnConstraints();
        colSize.setPercentWidth(100.0/numOfButtons);
        colSize.setHgrow(Priority.ALWAYS);

        for (int i=0; i< numOfButtons; i++) {
            buttonGrid.getColumnConstraints().add(colSize);
            buttons[i] = new Button("");
            buttons[i].setMaxWidth(Double.MAX_VALUE);
            buttonGrid.add(buttons[i],i,0);
            buttons[i].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    colorButtonPressed(event);
                }
            });
        }
    }

    private void populateDifficulties() {
        diffiucltyCBox.getItems().add("Easy");
        diffiucltyCBox.getItems().add("Hard");
    }

    private void setDiffiucltyLevel(String level) {
        switch (level) {
            case "Easy":
                gameLevel=1;
                break;
            case "Hard":
                gameLevel=2;
                break;
        }
    }

    private void resetGame(int timerDuration) {
        gameEngine.setTimerDuration(timerDuration);
    }

    private void startGame() {
        startStopBtn.setText("Stop");
        gameEngine.resetGame();
        gameEngine.startTimer();
        setRound();
        plusNumber.setDisable(true);
        minusNumber.setDisable(true);
        plusTimer.setDisable(true);
        minusTimer.setDisable(true);
        diffiucltyCBox.setDisable(true);
        gameStatus = !gameStatus;
    }
    private void stopGame() {
        startStopBtn.setText("Start");
        gameEngine.cancelTimer();
        plusNumber.setDisable(false);
        minusNumber.setDisable(false);
        plusTimer.setDisable(false);
        minusTimer.setDisable(false);
        diffiucltyCBox.setDisable(false);
        gameStatus = !gameStatus;
    }

    private void colorButtonPressed(ActionEvent event) {
        if (gameStatus) {
            gameEngine.checkSelectedColor((ColorPickerEngine.ColorChoice) ((Button) event.getSource()).getUserData());
            setRound();
        }
    }

    private void finishGame() {
        stopGame();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game is finished");
        alert.setHeaderText("Final results: " + gameEngine.scoreProperty().get());
        alert.showAndWait();
    }

    @Override
    public void onTimerFinished() {
        Platform.runLater(this::finishGame);
    }

    private void setRound() {
        List<ColorPickerEngine.ColorChoice> colors = gameEngine.getNewColors(numOfButtons);
        for (int i=0; i<numOfButtons; i++) {
            buttons[i].setBackground(new Background(new BackgroundFill(colors.get(i).getColorObj(),CornerRadii.EMPTY, Insets.EMPTY)));
            buttons[i].setUserData(colors.get(i));
        }
        colorLabel.setText(gameEngine.getTargetColorName());
        if (gameLevel == 1) {
            colorLabel.setTextFill(Color.BLACK);
        } else if (gameLevel == 2) {
            colorLabel.setTextFill(gameEngine.getRandomColor());
        }
    }

}
