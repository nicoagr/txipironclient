package eus.ehu.txipironesmastodonfx.controllers.windowControllers;

import eus.ehu.txipironesmastodonfx.controllers.main.MainWindowController;
import eus.ehu.txipironesmastodonfx.data_access.AsyncUtils;
import eus.ehu.txipironesmastodonfx.data_access.NetworkUtils;
import eus.ehu.txipironesmastodonfx.domain.MediaAttachment;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * This class is used to represent a media player
 * It will show up when viewing a media attachment
 * in a toot.
 *
 * @author Nicolás Aguado
 * @author Haizea Bermejo
 * @author Xiomara Cáceces
 * @author Marcos Chouciño
 */
public class MediaViewController {
    List<MediaAttachment> media;
    private static final Logger logger = LogManager.getLogger("MediaViewController");
    private int i;
    @FXML
    private ImageView imageView;
    @FXML
    private MediaView mediaView;
    @FXML
    private Button nextBtn;
    @FXML
    private Button prevBtn;
    private MainWindowController master;


    @FXML
    public AnchorPane anchor;

    /**
     * First method called when the view is loaded.
     * It loads the first element in the passed-as-parameter
     * media attachment list
     *
     * @param list (List<MediaAttachment>) - List of media attachments to be shown.
     */
    public void setMedia(List<MediaAttachment> list) {
        media = list;
        i = 0;
        loadContent(i);
        if (media.size() == 1) {
            nextBtn.setDisable(true);
            prevBtn.setDisable(true);
        }
    }

    @FXML
    private Label loadingTxt;
    @FXML
    private Label mediaCounterTxt;

    /**
     * Loads a specific media attachment
     * from the media list.
     * It will distinguish between images and videos
     * so that it can load an ImageView or a MediaView
     * accordingly.
     *
     * @param i (int) - Index of the media attachment to be loaded.
     */
    private void loadContent(int i) {
        loadingTxt.setVisible(true);
        mediaView.setVisible(false);
        imageView.setVisible(false);
        mediaBar.setVisible(false);
        mediaCounterTxt.setText("Viewing media " + (i + 1) + " of " + media.size() + ".");
        logger.info("Loading media " + (i + 1) + " of " + media.size() + ".");
        AsyncUtils.asyncTask(() -> {
            MediaAttachment m = media.get(i);
            if (m.type.equals("image") || m.type.equals("gifv")) {
                logger.debug("Loaded IMAGE with url" + m.url);
                return List.of("img", new Image(m.url));
            } else {
                logger.debug("Loaded VIDEO with url" + m.url);
                return List.of("media", new MediaPlayer(new Media(m.url)));
            }
        }, list -> {
            if (list.get(0).equals("img")) {
                imageView.setImage((Image) list.get(1));
                imageView.setVisible(true);
            } else {
                mp = (MediaPlayer) list.get(1);
                mediaView.setMediaPlayer(mp);
                if (master != null && master.autoplayMedia)
                    mp.setAutoPlay(true);
                buildbar(mp);
                mediaView.setVisible(true);
                mediaBar.setVisible(true);
            }
            loadingTxt.setVisible(false);
        });

    }

    private Stage popupStage;

    /**
     * This method is called by the main controller
     * and it will set a reference to the popup stage
     * (which is the stage that contains this view)
     */
    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }

    /**
     * Setter for the reference to the main window controller
     *
     * @param master (MainWindowController) - The reference to the main window controller
     */
    public void setReference(MainWindowController master) {
        this.master = master;
    }

    /**
     * This method is called when the user clicks
     * the close button.
     * It will stop the media player if it is playing
     * and close the popup stage.
     */
    @FXML
    void closePopup() {
        if (mp != null)
            mp.stop();
        popupStage.close();
    }

    /**
     * This method is called when the user clicks
     * the next button.
     * It will load the next media attachment in the list.
     * If the current media attachment is the last one,
     * it will do nothing.
     */
    @FXML
    void nextAction() {
        if (i == media.size() - 1) {
            return;
        }
        loadContent(++i);
    }

    /**
     * This method is called when the user clicks
     * the previous button.
     * It will load the previous media attachment in the list.
     * If the current media attachment is the first one,
     * it will do nothing.
     */
    @FXML
    void prevAction() {
        if (i == 0) {
            return;
        }
        loadContent(--i);
    }

    /**
     * This method is called when the user clicks
     * the open in browser button.
     * It will open the current media attachment
     * in the default browser defined in the OS.
     */
    @FXML
    void openInBrowser() {
        NetworkUtils.openWebPage(media.get(i).url);
    }

        /* --------------------------------------------------------------------------------------
                                BEWARE TRAVELER, HERE BE DRAGONS
                    The code contained below this line is an adaptation of some
                    ancient code found in the oracle documentation. It has been
                    adapted to fit our needs, but it's assembled together poorly
                    and without much care. If you shall try to modify it, I
                        suggest you to read the following web page first:
               https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
                 (It has also been saved on web.archive.org in case the link goes down)
       --------------------------------------------------------------------------------------*/

    private MediaPlayer mp;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;
    @FXML
    private HBox mediaBar;

    /**
     * This method will build the media bar
     * if the media type is a video.
     *
     * @param mp (MediaPlayer) - Media player to be used
     */
    private void buildbar(final MediaPlayer mp) {
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(5, 10, 5, 10));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);

        final Button playButton = new Button(">");

        playButton.setOnAction(e -> {
            MediaPlayer.Status status = mp.getStatus();

            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                // don't do anything in these states
                return;
            }

            if (status == MediaPlayer.Status.PAUSED
                    || status == MediaPlayer.Status.READY
                    || status == MediaPlayer.Status.STOPPED) {
                // rewind the movie if we're sitting at the end
                if (atEndOfMedia) {
                    mp.seek(mp.getStartTime());
                    atEndOfMedia = false;
                }
                mp.play();
            } else {
                mp.pause();
            }
        });
        mp.currentTimeProperty().addListener(ov -> updateValues());

        mp.setOnPlaying(() -> {
            if (stopRequested) {
                mp.pause();
                stopRequested = false;
            } else {
                playButton.setText("||");
            }
        });

        mp.setOnPaused(() -> playButton.setText(">"));

        mp.setOnReady(() -> {
            duration = mp.getMedia().getDuration();
            updateValues();
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(() -> {
            if (!repeat) {
                playButton.setText(">");
                stopRequested = true;
                atEndOfMedia = true;
            }
        });

        mediaBar.getChildren().add(playButton);
        // Add spacer
        Label spacer = new Label("   ");
        mediaBar.getChildren().add(spacer);

        // Add Time label
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(ov -> {
            if (timeSlider.isValueChanging()) {
                // multiply duration by percentage calculated by slider position
                mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
            }
        });
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);

        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(ov -> {
            if (volumeSlider.isValueChanging()) {
                mp.setVolume(volumeSlider.getValue() / 100.0);
            }
        });
        mediaBar.getChildren().add(volumeSlider);

        mediaBar.setVisible(true);
    }

    /**
     * This method will update the values of the media bar
     * if the media type is a video.
     * It will control the time slider and the volume slider.
     */
    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mp.getCurrentTime();
                playTime.setText(formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    // yes, this method is deprecated, couldn't bother finding the updated one
                    timeSlider.setValue(currentTime.divide(duration).toMillis()
                            * 100.0);
                }
                if (!volumeSlider.isValueChanging()) {
                    volumeSlider.setValue((int) Math.round(mp.getVolume()
                            * 100));
                }
            });
        }
    }

    /**
     * This method will format the time of the media bar
     * so that it fits the adequate time format.
     *
     * @param elapsed  (Duration) - Media Elapsed time
     * @param duration (Duration) - Media Duration
     * @return (String) - Formatted time
     */
    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

}
