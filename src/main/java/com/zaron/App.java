
package com.zaron;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private final List<File> playlist = new ArrayList<>();

    private ListView<String> playlistView;

    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private int currentIndex = -1;

    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button previousButton;
    private Button nextButton;
    private Button muteButton;

    private Slider volumeSlider;
    private Slider progressSlider;
    private Label titleLabel;
    private Label statusLabel;
    private Label timeLabel;
    private Label volumeLabel;

    private Label mediaTypeLabel;

    //dragging the progress bar.
    private boolean userChangingProgress = false;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        root.getStyleClass().add("root");

        // Header
        root.setTop(createHeader());

        // Main content
        root.setCenter(createMainContent(stage));

        // Player controls
        root.setBottom(createPlayerControls());

        Scene scene = new Scene(
                root,
                1200,
                720
        );

        // Load CSS
        try {

            String css = getClass()
                    .getResource("/style.css")
                    .toExternalForm();

            scene.getStylesheets().add(css);

        } catch (Exception e) {

            System.out.println(
                    "style.css was not found. " +
                    "Using default JavaFX styling."
            );
        }

        // Keyboard shortcuts
        setupKeyboardControls(scene);

        stage.setTitle(
                "Music Therapy"
        );

        stage.setMinWidth(950);
        stage.setMinHeight(620);

        stage.setScene(scene);

        stage.show();

        root.requestFocus();
    }

    private HBox createHeader() {

        Label logo = new Label("Z");

        logo.getStyleClass().add("logo");

        Label appName =
                new Label("ZARON");

        appName.getStyleClass().add("app-name");

        Label subName =
                new Label("MEDIA PLAYER");

        subName.getStyleClass().add("sub-name");

        VBox branding =
                new VBox(
                        0,
                        appName,
                        subName
                );

        branding.setAlignment(
                Pos.CENTER_LEFT
        );

        titleLabel =
                new Label("No media selected");

        titleLabel.getStyleClass()
                .add("header-title");

        HBox header =
                new HBox(
                        14,
                        logo,
                        branding,
                        titleLabel
                );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        18,
                        24,
                        18,
                        24
                )
        );

        HBox.setHgrow(
                titleLabel,
                Priority.ALWAYS
        );

        return header;
    }
    private VBox createMainContent(
            Stage stage
    ) {

        VBox main =
                new VBox(16);

        main.setPadding(
                new Insets(20)
        );

        StackPane mediaArea =
                createMediaArea();

        VBox.setVgrow(
                mediaArea,
                Priority.ALWAYS
        );

        HBox playlistPanel =
                createPlaylistPanel(stage);

        main.getChildren().addAll(
                mediaArea,
                playlistPanel
        );

        return main;
    }

    private StackPane createMediaArea() {

        StackPane mediaArea =
                new StackPane();

        mediaArea.getStyleClass()
                .add("media-area");

        mediaView =
                new MediaView();

        mediaView.setPreserveRatio(true);

        mediaView.setFitWidth(850);
        mediaView.setFitHeight(430);

        Label placeholder =
                new Label(
                        
                );

        placeholder.getStyleClass()
                .add("placeholder");

        Label hint =
                new Label(
                       
                );

        hint.getStyleClass()
                .add("hint");

        VBox emptyContent =
                new VBox(
                        8,
                        placeholder,
                        hint
                );

        emptyContent.setAlignment(
                Pos.CENTER
        );

        mediaTypeLabel =
                new Label("");

        mediaTypeLabel
                .getStyleClass()
                .add("media-type-label");

        StackPane.setAlignment(
                mediaTypeLabel,
                Pos.TOP_LEFT
        );

        StackPane.setMargin(
                mediaTypeLabel,
                new Insets(15)
        );
        mediaArea.getChildren().addAll(
                mediaView,
                emptyContent,
                mediaTypeLabel
        );

        mediaArea.widthProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            mediaView.setFitWidth(
                                    Math.max(
                                            300,
                                            newValue.doubleValue()
                                                    - 40
                                    )
                            );
                        }
                );

        mediaArea.heightProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            mediaView.setFitHeight(
                                    Math.max(
                                            220,
                                            newValue.doubleValue()
                                                    - 40
                                    )
                            );
                        }
                );

        return mediaArea;
    }
    private HBox createPlaylistPanel(
            Stage stage
    ) {

        playlistView =
                new ListView<>();

        playlistView.setPrefHeight(150);

        playlistView.setPlaceholder(
                new Label(
                        "Playlist is empty. " +
                        "Click ADD MEDIA to select MP3 or MP4 files."
                )
        );

        playlistView
                .getSelectionModel()
                .selectedIndexProperty()
                .addListener(
                        (observable,
                         oldIndex,
                         newIndex) -> {

                            int index =
                                    newIndex.intValue();

                            if (index >= 0 &&
                                    index < playlist.size()) {

                                loadMedia(
                                        index,
                                        true
                                );
                            }
                        }
                );

        Button addButton =
                new Button(
                        "ADD MEDIA"
                );

        addButton.getStyleClass()
                .add("primary-button");

        addButton.setTooltip(
                new Tooltip(
                        "Add MP3 or MP4 files"
                )
        );

        addButton.setOnAction(
                event -> addMedia(stage)
        );

        Button removeButton =
                new Button(
                        "REMOVE"
                );

        removeButton.getStyleClass()
                .add("secondary-button");

        removeButton.setTooltip(
                new Tooltip(
                        "Remove selected media"
                )
        );

        removeButton.setOnAction(
                event -> removeSelected()
        );
        Button clearButton =
                new Button(
                        "CLEAR"
                );

        clearButton.getStyleClass()
                .add("secondary-button");

        clearButton.setTooltip(
                new Tooltip(
                        "Clear playlist"
                )
        );

        clearButton.setOnAction(
                event -> clearPlaylist()
        );

        Label playlistTitle =
                new Label("PLAYLIST");

        playlistTitle.getStyleClass()
                .add("section-title");

        HBox buttons =
                new HBox(
                        8,
                        addButton,
                        removeButton,
                        clearButton
                );

        buttons.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox controls =
                new VBox(
                        10,
                        playlistTitle,
                        buttons
                );

        controls.setPrefWidth(300);

        controls.setAlignment(
                Pos.TOP_LEFT
        );

        HBox panel =
                new HBox(
                        20,
                        controls,
                        playlistView
                );

        panel.getStyleClass()
                .add("playlist-panel");

        panel.setPadding(
                new Insets(14)
        );

        HBox.setHgrow(
                playlistView,
                Priority.ALWAYS
        );

        return panel;
    }

    private VBox createPlayerControls() {

        progressSlider =
                new Slider(
                        0,
                        100,
                        0
                );

        progressSlider.setMaxWidth(
                Double.MAX_VALUE
        );

        progressSlider.setOnMousePressed(
                event ->
                        userChangingProgress = true
        );

        progressSlider.setOnMouseReleased(
                event -> {

                    userChangingProgress = false;

                    seekToSliderPosition();
                }
        );

        timeLabel =
                new Label(
                        "00:00 / 00:00"
                );

        timeLabel.getStyleClass()
                .add("time-label");

        HBox progressRow =
                new HBox(
                        10,
                        progressSlider,
                        timeLabel
                );

        progressRow.setAlignment(
                Pos.CENTER
        );

        HBox.setHgrow(
                progressSlider,
                Priority.ALWAYS
        );

        previousButton =
                playerButton(
                        "Previous",
                        "Previous — P"
                );

        playButton =
                playerButton(
                        "Play",
                        "Play — Spacebar"
                );

        pauseButton =
                playerButton(
                        "Pause",
                        "Pause — Spacebar"
                );

        stopButton =
                playerButton(
                        "Stop",
                        "Stop — S"
                );

        nextButton =
                playerButton(
                        "Next",
                        "Next — N"
                );

        muteButton =
                playerButton(
                        "Mute",
                        "Mute/Unmute — M"
                );

        previousButton.setOnAction(
                event -> previous()
        );

        playButton.setOnAction(
                event -> play()
        );

        pauseButton.setOnAction(
                event -> pause()
        );

        stopButton.setOnAction(
                event -> stopPlayback()
        );

        nextButton.setOnAction(
                event -> next()
        );

        muteButton.setOnAction(
                event -> toggleMute()
        );

        volumeSlider =
                new Slider(
                        0,
                        1,
                        0.7
                );

        volumeSlider.setPrefWidth(130);

        volumeSlider.valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            double volume =
                                    newValue.doubleValue();

                            if (mediaPlayer != null) {

                                mediaPlayer.setVolume(
                                        volume
                                );
                            }

                            updateVolumeLabel();
                        }
                );

        volumeLabel =
                new Label("70%");

        volumeLabel.getStyleClass()
                .add("volume-label");

        HBox transport =
                new HBox(
                        8,
                        previousButton,
                        playButton,
                        pauseButton,
                        stopButton,
                        nextButton
                );

        transport.setAlignment(
                Pos.CENTER
        );
        HBox volume =
                new HBox(
                        8,
                        muteButton,
                        volumeSlider,
                        volumeLabel
                );

        volume.setAlignment(
                Pos.CENTER_RIGHT
        );

        statusLabel =
                new Label("Ready");

        statusLabel.getStyleClass()
                .add("status-label");

        HBox bottomRow =
                new HBox(
                        20,
                        statusLabel,
                        transport,
                        volume
                );

        bottomRow.setAlignment(
                Pos.CENTER
        );

        HBox.setHgrow(
                statusLabel,
                Priority.ALWAYS
        );

        VBox playerBar =
                new VBox(
                        10,
                        progressRow,
                        bottomRow
                );

        playerBar.getStyleClass()
                .add("player-bar");

        playerBar.setPadding(
                new Insets(
                        14,
                        20,
                        18,
                        20
                )
        );

        return playerBar;
    }

    private Button playerButton(
            String text,
            String tooltip
    ) {

        Button button =
                new Button(text);

        button.getStyleClass()
                .add("control-button");

        button.setTooltip(
                new Tooltip(tooltip)
        );

        return button;
    }

    private void addMedia(
            Stage stage
    ) {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Select MP3 or MP4 Files"
        );

        chooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "MP3 / MP4 Files",
                                "*.mp3",
                                "*.MP3",
                                "*.mp4",
                                "*.MP4"
                        )
                );

        List<File> files =
                chooser.showOpenMultipleDialog(
                        stage
                );

        if (files == null ||
                files.isEmpty()) {

            return;
        }

        int added = 0;

        for (File file : files) {

            if (!playlist.contains(file)) {

                playlist.add(file);

                playlistView
                        .getItems()
                        .add(file.getName());

                added++;
            }
        }

        // Automatically select first file
        if (currentIndex == -1 &&
                !playlist.isEmpty()) {

            playlistView
                    .getSelectionModel()
                    .select(0);
        }

        statusLabel.setText(
                added +
                " media file(s) added"
        );
    }

    private void removeSelected() {

        int selected =
                playlistView
                        .getSelectionModel()
                        .getSelectedIndex();

        if (selected < 0 ||
                selected >= playlist.size()) {

            statusLabel.setText(
                    "Select a file first."
            );

            return;
        }

        boolean removingCurrent =
                selected == currentIndex;

        if (removingCurrent) {

            disposePlayer();
        }

        playlist.remove(selected);

        playlistView
                .getItems()
                .remove(selected);

        if (playlist.isEmpty()) {

            currentIndex = -1;

            titleLabel.setText(
                    "No media selected"
            );

            mediaTypeLabel.setText("");

            statusLabel.setText(
                    "Playlist is empty"
            );

            progressSlider.setValue(0);

            timeLabel.setText(
                    "00:00 / 00:00"
            );

            return;
        }

        if (removingCurrent) {

            int newIndex =
                    Math.min(
                            selected,
                            playlist.size() - 1
                    );

            currentIndex = -1;

            playlistView
                    .getSelectionModel()
                    .select(newIndex);

        } else {

            if (selected < currentIndex) {

                currentIndex--;
            }

            statusLabel.setText(
                    "Media removed"
            );
        }
    }


    private void clearPlaylist() {

        disposePlayer();

        playlist.clear();

        playlistView
                .getItems()
                .clear();

        currentIndex = -1;

        titleLabel.setText(
                "No media selected"
        );

        mediaTypeLabel.setText("");

        statusLabel.setText(
                "Playlist cleared"
        );

        progressSlider.setValue(0);

        timeLabel.setText(
                "00:00 / 00:00"
        );
    }

    private void loadMedia(
            int index,
            boolean autoPlay
    ) {

        if (index < 0 ||
                index >= playlist.size()) {

            return;
        }

        disposePlayer();

        currentIndex = index;

        File file =
                playlist.get(index);

        titleLabel.setText(
                file.getName()
        );

        statusLabel.setText(
                "Loading: " +
                file.getName()
        );

        String fileName =
                file.getName().toLowerCase();

        boolean isMp3 =
                fileName.endsWith(".mp3");

        boolean isMp4 =
                fileName.endsWith(".mp4");

        if (isMp3) {

            mediaTypeLabel.setText(
                    "♪ MP3 AUDIO"
            );

        } else if (isMp4) {

            mediaTypeLabel.setText(
                    "▶ MP4 VIDEO"
            );

        } else {

            mediaTypeLabel.setText(
                    "MEDIA"
            );
        }


        try {

        
            String mediaUrl =
                    file.toURI().toString();

            System.out.println(
                    "Loading media: " +
                    mediaUrl
            );

            media =
                    new Media(mediaUrl);

            // Check media error
            if (media.getError() != null) {

                statusLabel.setText(
                        "Media error: " +
                        media.getError().getMessage()
                );

                return;
            }

            mediaPlayer =
                    new MediaPlayer(media);

            // Attach player to MediaView
            mediaView.setMediaPlayer(
                    mediaPlayer
            );

            // Set volume
            mediaPlayer.setVolume(
                    volumeSlider.getValue()
            );

            mediaPlayer.setOnReady(() -> {

                Duration total =
                        mediaPlayer
                                .getTotalDuration();

                progressSlider.setValue(0);

                updateTimeLabel(
                        Duration.ZERO,
                        total
                );

                statusLabel.setText(
                        "Ready: " +
                        file.getName()
                );

                if (autoPlay) {

                    mediaPlayer.play();

                    statusLabel.setText(
                            "Playing: " +
                            file.getName()
                    );
                }
            });


            mediaPlayer.setOnError(() -> {

                if (mediaPlayer != null) {

                    System.out.println(
                            "MediaPlayer Error: " +
                            mediaPlayer.getError()
                    );
                }

                statusLabel.setText(
                        "Cannot play this MP3/MP4 file"
                );
            });

            mediaPlayer
                    .currentTimeProperty()
                    .addListener(
                            (observable,
                             oldTime,
                             newTime) -> {

                                if (mediaPlayer == null) {
                                    return;
                                }

                                Duration total =
                                        mediaPlayer
                                                .getTotalDuration();

                                if (!userChangingProgress &&
                                        total != null &&
                                        !total.isUnknown() &&
                                        total.toMillis() > 0) {

                                    double percentage =
                                            (
                                                    newTime.toMillis()
                                                    /
                                                    total.toMillis()
                                            )
                                            * 100;

                                    progressSlider.setValue(
                                            Math.min(
                                                    100,
                                                    Math.max(
                                                            0,
                                                            percentage
                                                    )
                                            )
                                    );
                                }

                                updateTimeLabel(
                                        newTime,
                                        total
                                );
                            }
                    );

            mediaPlayer.setOnEndOfMedia(() -> {

                statusLabel.setText(
                        "Finished: " +
                        file.getName()
                );
                if (!playlist.isEmpty()) {

                    next();
                }
            });

        } catch (Exception e) {

            e.printStackTrace();

            statusLabel.setText(
                    "Error loading media: " +
                    e.getMessage()
            );
        }
    }

    private void play() {

        if (mediaPlayer == null) {

            statusLabel.setText(
                    "Add an MP3 or MP4 file first."
            );

            return;
        }

        mediaPlayer.play();

        statusLabel.setText(
                "Playing"
        );
    }


    private void pause() {

        if (mediaPlayer == null) {

            return;
        }

        mediaPlayer.pause();

        statusLabel.setText(
                "Paused"
        );
    }


    private void togglePlayPause() {

        if (mediaPlayer == null) {

            statusLabel.setText(
                    "Add an MP3 or MP4 file first."
            );

            return;
        }

        if (mediaPlayer.getStatus()
                == MediaPlayer.Status.PLAYING) {

            pause();

        } else {

            play();
        }
    }

    private void stopPlayback() {

        if (mediaPlayer == null) {

            return;
        }

        mediaPlayer.stop();

        statusLabel.setText(
                "Stopped"
        );
    }

    private void next() {

        if (playlist.isEmpty()) {

            statusLabel.setText(
                    "Playlist is empty."
            );

            return;
        }

        int nextIndex =
                currentIndex + 1;

        if (nextIndex >= playlist.size()) {

            nextIndex = 0;
        }

        playlistView
                .getSelectionModel()
                .select(nextIndex);

        if (nextIndex == currentIndex) {

            loadMedia(
                    nextIndex,
                    true
            );
        }
    }

    private void previous() {

        if (playlist.isEmpty()) {

            statusLabel.setText(
                    "Playlist is empty."
            );

            return;
        }

        int previousIndex =
                currentIndex - 1;

        if (previousIndex < 0) {

            previousIndex =
                    playlist.size() - 1;
        }

        playlistView
                .getSelectionModel()
                .select(previousIndex);

        if (previousIndex == currentIndex) {

            loadMedia(
                    previousIndex,
                    true
            );
        }
    }

    private void toggleMute() {

        if (mediaPlayer == null) {

            statusLabel.setText(
                    "Add a media file first."
            );

            return;
        }

        boolean wasMuted =
                mediaPlayer.isMute();

        mediaPlayer.setMute(
                !wasMuted
        );

        if (wasMuted) {

            muteButton.setText(
                    "🔊"
            );

            statusLabel.setText(
                    "Unmuted"
            );

        } else {

            muteButton.setText(
                    "🔇"
            );

            statusLabel.setText(
                    "Muted"
            );
        }
    }
    private void changeVolume(
            double amount
    ) {

        double newVolume =
                volumeSlider.getValue()
                +
                amount;

        newVolume =
                Math.max(
                        0,
                        Math.min(
                                1,
                                newVolume
                        )
                );

        volumeSlider.setValue(
                newVolume
        );

        if (mediaPlayer != null) {

            mediaPlayer.setVolume(
                    newVolume
            );
        }

        updateVolumeLabel();

        statusLabel.setText(
                "Volume: " +
                Math.round(
                        newVolume * 100
                ) +
                "%"
        );
    }

    private void setupKeyboardControls(
            Scene scene
    ) {

        scene.setOnKeyPressed(
                event -> {

                    KeyCode key =
                            event.getCode();

                    switch (key) {

                        // SPACEBAR
                        case SPACE:

                            togglePlayPause();

                            event.consume();

                            break;

                        // S = STOP
                        case S:

                            stopPlayback();

                            event.consume();

                            break;

                        // N = NEXT
                        case N:

                            next();

                            event.consume();

                            break;

                        // P = PREVIOUS
                        case P:

                            previous();

                            event.consume();

                            break;

                        // UP ARROW
                        case UP:

                            changeVolume(
                                    0.10
                            );

                            event.consume();

                            break;

                        // DOWN ARROW
                        case DOWN:

                            changeVolume(
                                    -0.10
                            );

                            event.consume();

                            break;

                        // M = MUTE
                        case M:

                            toggleMute();

                            event.consume();

                            break;

                        default:

                            break;
                    }
                }
        );
    }
    private void seekToSliderPosition() {

        if (mediaPlayer == null) {

            return;
        }

        Duration total =
                mediaPlayer.getTotalDuration();

        if (total == null ||
                total.isUnknown()) {

            return;
        }

        double seconds =
                total.toSeconds()
                *
                (
                        progressSlider.getValue()
                        /
                        100.0
                );

        mediaPlayer.seek(
                Duration.seconds(seconds)
        );
    }

    private void updateVolumeLabel() {

        if (volumeLabel == null) {

            return;
        }

        int percentage =
                (int) Math.round(
                        volumeSlider.getValue()
                        * 100
                );

        volumeLabel.setText(
                percentage + "%"
        );
    }

    private void updateTimeLabel(
            Duration current,
            Duration total
    ) {

        if (timeLabel == null) {

            return;
        }

        timeLabel.setText(
                formatDuration(current)
                +
                " / "
                +
                formatDuration(total)
        );
    }
    private String formatDuration(
            Duration duration
    ) {

        if (duration == null ||
                duration.isUnknown() ||
                duration.isIndefinite()) {

            return "00:00";
        }

        long seconds =
                Math.max(
                        0,
                        Math.round(
                                duration.toSeconds()
                        )
                );

        long minutes =
                seconds / 60;

        long remainingSeconds =
                seconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                remainingSeconds
        );
    }

    private void disposePlayer() {

        if (mediaPlayer != null) {

            mediaPlayer.stop();

            mediaPlayer.dispose();

            mediaPlayer = null;
        }

        if (mediaView != null) {

            mediaView.setMediaPlayer(
                    null
            );
        }

        media = null;
    }

    @Override
    public void stop() {

        disposePlayer();
    }

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}

