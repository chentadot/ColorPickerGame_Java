import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

import java.util.*;


public class ColorPickerEngine {
    private IntegerProperty score;
    private IntegerProperty timeLeft;
    private int timerDuration;
    private TimerListener timerListener;
    private ColorChoice[] colors = ColorChoice.values();
    private ColorChoice targetColor;
    private Task<Void> timerTask;


    public ColorPickerEngine(int duration) {
        this.score = new SimpleIntegerProperty(0);
        this.timeLeft = new SimpleIntegerProperty(duration);
        this.timerDuration = duration;
    }
    public ColorPickerEngine() {
        this(30);
    }

    public interface TimerListener {
        void onTimerFinished();
    }

    public enum ColorChoice {
        RED(1,"אדום", Color.RED),
        BLUE(2,"כחול", Color.BLUE),
        GREEN(3, "ירוק", Color.GREEN),
        YELLOW(4,"צהוב",Color.YELLOW),
        BLACK(5,"שחור",Color.BLACK),
        PURPLE(6,"סגול", Color.PURPLE),
        PINK(7,"ורוד", Color.PINK),
        BROWN(8,"חום",Color.BROWN),
        GRAY(9,"אפור",Color.GRAY);


        private final int value;
        private final String colorName;
        private final Color colorObj;

        private ColorChoice(int value, String colorName, Color colorObj) {
            this.value=value;
            this.colorName=colorName;
            this.colorObj=colorObj;
        }

        public int getValue() {
            return value;
        }
        public String getColorName() {
            return colorName;
        }
        public Color getColorObj() {
            return colorObj;
        }
    }

    public void setScore(int newScore) {
        this.score = new SimpleIntegerProperty(newScore);
    }
    public IntegerProperty timeLeftProperty() {
        return timeLeft;
    }
    public IntegerProperty scoreProperty() {return score;}
    public int getMaxNumberOfColors() {return colors.length;}

    public void setTimerDuration(int duration) {
        this.timerDuration=duration;
        this.timeLeft.set(duration);
    }

    public void startTimer() {
        timerTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i=timerDuration; i>=0; i--) {
                    if (isCancelled()) {
                        break;      // break if timer is cancelled
                    }
                    final int finalI = i;
                    Platform.runLater(() -> {
                        updateMessage("Time left: " + finalI);
                        timeLeft.set(finalI);
                    });
                    Thread.sleep(1000);
                }
                if (!isCancelled()) {
                    Platform.runLater(() -> {
                        if (timerListener != null) {
                            timerListener.onTimerFinished(); // Notify the listener
                        }
                    });
                }
                return null;
            }
        };
        new Thread(timerTask).start();

        // Listen to changes in the message property
        /*
        timerTask.messageProperty().addListener((obs, oldMsg, newMsg) -> {
            System.out.println("Timer Message: " + newMsg);
        });
        */
    }

    public void cancelTimer() {
        if (timerTask != null && timerTask.isRunning()) {
            timerTask.cancel();
        }
    }

    public void setTimerListener(TimerListener listener) {
        this.timerListener = listener;
    }

    public void resetGame() {
        score.set(0);
    }

    // Game logic from here

    public List<ColorChoice> getNewColors(int numOfColors) {
        Random r = new Random();
        List<ColorChoice> randomListOfColors = new ArrayList<>(Arrays.asList(colors));
        Collections.shuffle(randomListOfColors);

        int i = r.nextInt(numOfColors);     // randomly select the target color
        targetColor = randomListOfColors.get(i);

        for (int j=colors.length; j>numOfColors; j--)    // remove un necessary colors
            randomListOfColors.remove(j-1);

        return randomListOfColors;
    }

    public String getTargetColorName() {
        return targetColor.getColorName();
    }

    public void checkSelectedColor(ColorChoice color) {
        if (color.getValue() == targetColor.getValue())
            score.set(score.get()+1);
    }

    public Color getRandomColor() {
        Random r = new Random();
        return colors[r.nextInt(colors.length)].getColorObj();
    }

    
}
