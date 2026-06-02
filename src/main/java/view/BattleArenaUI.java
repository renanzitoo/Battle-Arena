package view;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import service.BattleSchedulerService;
import service.MatchmakingService;
import thread.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BattleArenaUI extends Application {

    private MatchmakingService matchmakingService;
    private BattleSchedulerService schedulerService;
    private MatchmakingThread matchmakingThread;
    private SchedulerThread schedulerThread;
    private MonitorThread monitorThread;
    private PlayerGeneratorThread playerGenerator;
    private Metrics metrics;

    // UI Components to update
    private Label casualQueueLabel, rankedQueueLabel, tournamentQueueLabel;
    private TableView<BattleRequest> schedulerTable;
    private FlowPane activeBattlesPane;
    private Label avgWaitLabel, abandonRateLabel, finishedBattlesLabel, activeThreadsLabel;
    private ProgressBar resourceBar;
    private Label resourceLabel;
    private Label timeLabel;

    private ListView<String> eventLog;
    private final ObservableList<String> eventMessages = FXCollections.observableArrayList();

    // Thread Indicators
    private Circle mainIndicator, matchmakingIndicator, schedulerIndicator, monitorIndicator, generatorIndicator;

    @Override
    public void start(Stage primaryStage) {
        initBackend();

        // Pass logger to services
        matchmakingService.setEventLogger(msg -> javafx.application.Platform.runLater(() -> {
            eventMessages.add(0, msg);
            if (eventMessages.size() > 50) eventMessages.remove(50);
        }));
        schedulerService.setEventLogger(msg -> javafx.application.Platform.runLater(() -> {
            eventMessages.add(0, msg);
            if (eventMessages.size() > 50) eventMessages.remove(50);
        }));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // --- HEADER ---
        VBox header = createHeader();
        root.setTop(header);

        // --- LEFT PANEL (Queues) ---
        VBox leftPanel = createLeftPanel();
        root.setLeft(leftPanel);

        // --- CENTER PANEL (Scheduler & Log) ---
        VBox centerPanel = createCenterPanel();
        root.setCenter(centerPanel);

        // --- RIGHT PANEL (Active Battles) ---
        VBox rightPanel = createRightPanel();
        root.setRight(rightPanel);

        // --- BOTTOM PANEL (Metrics & Threads) ---
        VBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        primaryStage.setTitle("Battle Arena Manager - OS Project");
        primaryStage.setScene(scene);
        
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.show();

        startUpdateTimer();
    }

    private void initBackend() {
        metrics = new Metrics();
        matchmakingService = new MatchmakingService(metrics);
        schedulerService = new BattleSchedulerService(10, metrics);

        matchmakingThread = new MatchmakingThread(matchmakingService, schedulerService);
        schedulerThread = new SchedulerThread(schedulerService);
        monitorThread = new MonitorThread(matchmakingService, schedulerService);

        matchmakingThread.start();
        schedulerThread.start();
        monitorThread.start();
    }

    private VBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER);

        Text title = new Text("BATTLE ARENA MANAGER");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        timeLabel = new Label();
        timeLabel.getStyleClass().add("metric-label");
        timeLabel.setStyle("-fx-font-size: 14px;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        header.getChildren().addAll(spacer, title, spacer2, timeLabel);
        return new VBox(header);
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setMinWidth(250);
        panel.getStyleClass().add("side-panel");

        Label title = new Label("MATCHMAKING QUEUES");
        title.getStyleClass().add("panel-title");

        casualQueueLabel = new Label("0");
        VBox casualCard = createQueueCard("QUICK DUEL", "⚔", casualQueueLabel);

        rankedQueueLabel = new Label("0");
        VBox rankedCard = createQueueCard("RANKED MATCH", "🏆", rankedQueueLabel);

        tournamentQueueLabel = new Label("0");
        VBox tournamentCard = createQueueCard("TOURNAMENT", "👑", tournamentQueueLabel);

        Button startBtn = new Button("START GENERATOR");
        startBtn.getStyleClass().add("button-lol");
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setOnAction(e -> {
            if (playerGenerator == null || !playerGenerator.isAlive()) {
                playerGenerator = new PlayerGeneratorThread(matchmakingService, schedulerService);
                playerGenerator.start();
                eventMessages.add(0, "[SYSTEM] Generator Started");
            }
        });

        Button stopBtn = new Button("STOP GENERATOR");
        stopBtn.getStyleClass().add("button-lol");
        stopBtn.setMaxWidth(Double.MAX_VALUE);
        stopBtn.setOnAction(e -> {
            if (playerGenerator != null) {
                playerGenerator.stopGenerator();
                eventMessages.add(0, "[SYSTEM] Generator Stopping...");
            }
        });

        Button exitBtn = new Button("EXIT SIMULATOR");
        exitBtn.setStyle("-fx-background-color: #321414; -fx-border-color: #FF4646; -fx-text-fill: #FF4646;");
        exitBtn.getStyleClass().add("button-lol");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setOnAction(e -> exitSimulator());

        panel.getChildren().addAll(title, casualCard, rankedCard, tournamentCard, new Region(), startBtn, stopBtn, exitBtn);
        return panel;
    }

    private void exitSimulator() {
        if (playerGenerator != null) playerGenerator.stopGenerator();
        
        Alert waitingAlert = new Alert(Alert.AlertType.INFORMATION);
        waitingAlert.setTitle("System Shutdown");
        waitingAlert.setHeaderText("Cleaning up...");
        waitingAlert.setContentText("Waiting for remaining players to leave and battles to finish.");
        waitingAlert.show();

        new Thread(() -> {
            while (matchmakingService.hasPlayersWaiting() 
                   || schedulerService.hasBattlesWaiting() 
                   || schedulerService.hasActiveBattles()) {
                try { Thread.sleep(1000); } catch (InterruptedException ex) {}
            }
            javafx.application.Platform.runLater(() -> {
                waitingAlert.close();
                showFinalReportDialog();
            });
        }).start();
    }

    private void showFinalReportDialog() {
        Stage reportStage = new Stage();
        reportStage.setTitle("FINAL SIMULATION REPORT");
        reportStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

        VBox root = new VBox(20);
        root.getStyleClass().add("root");
        root.setStyle("-fx-border-color: #C89B3C; -fx-border-width: 2; -fx-padding: 30;");
        root.setAlignment(Pos.CENTER);

        Text title = new Text("FINAL REPORT");
        title.getStyleClass().add("header-title");

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        addReportRow(grid, 0, "Total Players:", String.valueOf(metrics.getTotalPlayers()));
        addReportRow(grid, 1, "Abandoned Players:", String.valueOf(metrics.getAbandonedPlayers()));
        addReportRow(grid, 2, "Battles Started:", String.valueOf(metrics.getStartedBattles()));
        addReportRow(grid, 3, "Battles Finished:", String.valueOf(metrics.getFinishedBattles()));
        addReportRow(grid, 4, "Avg Wait Time:", String.format("%.2fms", metrics.getAverageWaitingTime()));
        addReportRow(grid, 5, "Abandon Rate:", String.format("%.2f%%", metrics.getAbandonmentRate()));
        addReportRow(grid, 6, "Avg Utilization:", String.format("%.2f%%", metrics.getAverageUtilization()));

        Button closeBtn = new Button("CLOSE APPLICATION");
        closeBtn.getStyleClass().add("button-lol");
        closeBtn.setStyle("-fx-background-color: #1E2328; -fx-border-color: #C89B3C; -fx-min-width: 200;");
        closeBtn.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, grid, closeBtn);

        Scene scene = new Scene(root, 500, 600);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());
        reportStage.setScene(scene);
        reportStage.show();
    }

    private void addReportRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");
        Label val = new Label(value);
        val.getStyleClass().add("metric-value");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    private VBox createQueueCard(String name, String icon, Label countLabel) {
        VBox card = new VBox(5);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(icon + " " + name);
        nameLabel.getStyleClass().add("metric-label");
        countLabel.getStyleClass().add("metric-value");
        
        Label waitLabel = new Label("Status: Active");
        waitLabel.getStyleClass().add("metric-label");

        card.getChildren().addAll(nameLabel, countLabel, waitLabel);
        return card;
    }

    private VBox createCenterPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));

        Label title = new Label("BATTLE SCHEDULER");
        title.getStyleClass().add("panel-title");
        
        Label subtitle = new Label("POLICIES: Priority Scheduling + Aging");
        subtitle.setStyle("-fx-text-fill: #C89B3C; -fx-font-style: italic;");

        schedulerTable = new TableView<>();
        schedulerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        schedulerTable.setPrefHeight(300);

        TableColumn<BattleRequest, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBattleId()));

        TableColumn<BattleRequest, String> typeCol = new TableColumn<>("TYPE");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBattleType().toString()));

        TableColumn<BattleRequest, Number> prioCol = new TableColumn<>("BASE PRIO");
        prioCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBattleType().getPriority()));

        TableColumn<BattleRequest, Number> waitCol = new TableColumn<>("WAITING (S)");
        waitCol.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().calculateWaitingTime() / 1000));

        TableColumn<BattleRequest, Number> effectiveCol = new TableColumn<>("EFFECTIVE PRIO");
        effectiveCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().calculatePriorityWithAging()));

        schedulerTable.getColumns().addAll(idCol, typeCol, prioCol, waitCol, effectiveCol);

        VBox resourceBox = new VBox(5);
        resourceBox.setPadding(new Insets(10, 0, 10, 0));
        resourceLabel = new Label("Resources: 0/10 (0%)");
        resourceLabel.getStyleClass().add("metric-label");
        resourceBar = new ProgressBar(0);
        resourceBar.setMaxWidth(Double.MAX_VALUE);
        resourceBox.getChildren().addAll(resourceLabel, resourceBar);

        Label logTitle = new Label("LIVE EVENT FEED");
        logTitle.getStyleClass().add("panel-title");
        eventLog = new ListView<>(eventMessages);
        eventLog.getStyleClass().add("event-log");
        eventLog.setPrefHeight(200);

        panel.getChildren().addAll(title, subtitle, schedulerTable, resourceBox, logTitle, eventLog);
        VBox.setVgrow(schedulerTable, Priority.ALWAYS);
        return panel;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        panel.setMinWidth(300);

        Label title = new Label("ACTIVE BATTLES");
        title.getStyleClass().add("panel-title");

        activeBattlesPane = new FlowPane();
        activeBattlesPane.setVgap(10);
        activeBattlesPane.setHgap(10);
        activeBattlesPane.setPrefWrapLength(280);

        ScrollPane scroll = new ScrollPane(activeBattlesPane);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("transparent-scroll");
        
        panel.getChildren().addAll(title, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return panel;
    }

    private VBox createBottomPanel() {
        HBox metricsBox = new HBox(40);
        metricsBox.setPadding(new Insets(20));
        metricsBox.setAlignment(Pos.CENTER);
        metricsBox.getStyleClass().add("header");

        avgWaitLabel = new Label("0ms");
        VBox m1 = createMetricView("AVG WAIT TIME", avgWaitLabel);
        
        abandonRateLabel = new Label("0%");
        VBox m2 = createMetricView("ABANDON RATE", abandonRateLabel);
        
        finishedBattlesLabel = new Label("0");
        VBox m3 = createMetricView("FINISHED BATTLES", finishedBattlesLabel);
        
        activeThreadsLabel = new Label("0");
        VBox m4 = createMetricView("ACTIVE THREADS", activeThreadsLabel);

        metricsBox.getChildren().addAll(m1, m2, m3, m4);

        HBox threadsBox = new HBox(20);
        threadsBox.setPadding(new Insets(10, 20, 10, 20));
        threadsBox.setAlignment(Pos.CENTER);
        
        mainIndicator = createThreadIndicator("Main");
        matchmakingIndicator = createThreadIndicator("Matchmaking");
        schedulerIndicator = createThreadIndicator("Scheduler");
        monitorIndicator = createThreadIndicator("Monitor");
        generatorIndicator = createThreadIndicator("Generator");

        threadsBox.getChildren().addAll(
            new Label("THREADS:"),
            mainIndicator.getParent(), 
            matchmakingIndicator.getParent(), 
            schedulerIndicator.getParent(), 
            monitorIndicator.getParent(), 
            generatorIndicator.getParent()
        );

        return new VBox(metricsBox, threadsBox);
    }

    private VBox createMetricView(String label, Label valueLabel) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");
        valueLabel.getStyleClass().add("metric-value");
        box.getChildren().addAll(valueLabel, lbl);
        return box;
    }

    private Circle createThreadIndicator(String name) {
        Circle c = new Circle(6);
        c.getStyleClass().add("thread-running");
        Label l = new Label(name);
        l.getStyleClass().add("metric-label");
        HBox container = new HBox(5, c, l);
        container.setAlignment(Pos.CENTER);
        return c;
    }

    private void startUpdateTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> updateUI()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateUI() {
        casualQueueLabel.setText(String.valueOf(matchmakingService.getCasualDuelQueue().size()));
        rankedQueueLabel.setText(String.valueOf(matchmakingService.getRankedDuelQueue().size()));
        tournamentQueueLabel.setText(String.valueOf(matchmakingService.getTournamentDuelQueue().size()));

        List<BattleRequest> requests = schedulerService.getBattleQueue();
        requests.sort((a, b) -> b.calculatePriorityWithAging() - a.calculatePriorityWithAging());
        schedulerTable.setItems(FXCollections.observableArrayList(requests));

        int total = schedulerService.getTotalResources();
        int avail = schedulerService.getAvailableResources();
        int used = total - avail;
        double util = (double) used / total;
        
        // Corrigido: Garantindo que a barra reflita 0 a 1.0 e atualize o texto
        resourceBar.setProgress(util);
        resourceLabel.setText(String.format("Resources: %d/%d (%.0f%%)", used, total, util * 100));

        activeBattlesPane.getChildren().clear();
        for (Battle battle : schedulerService.getActiveBattles()) {
            activeBattlesPane.getChildren().add(createBattleCard(battle));
        }

        avgWaitLabel.setText(String.format("%.0fms", metrics.getAverageWaitingTime()));
        abandonRateLabel.setText(String.format("%.1f%%", metrics.getAbandonmentRate()));
        finishedBattlesLabel.setText(String.valueOf(metrics.getFinishedBattles()));
        activeThreadsLabel.setText(String.valueOf(Thread.activeCount()));
        
        timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        updateIndicator(matchmakingIndicator, matchmakingThread != null && matchmakingThread.isAlive());
        updateIndicator(schedulerIndicator, schedulerThread != null && schedulerThread.isAlive());
        updateIndicator(monitorIndicator, monitorThread != null && monitorThread.isAlive());
        updateIndicator(generatorIndicator, playerGenerator != null && playerGenerator.isAlive());
    }

    private void updateIndicator(Circle c, boolean alive) {
        c.getStyleClass().removeAll("thread-running", "thread-stopped");
        c.getStyleClass().add(alive ? "thread-running" : "thread-stopped");
    }

    private VBox createBattleCard(Battle battle) {
        VBox card = new VBox(2);
        card.getStyleClass().add("card");
        card.setPrefWidth(130);

        Label id = new Label("BATTLE #" + battle.getRequest().getBattleId());
        id.setStyle("-fx-font-weight: bold; -fx-text-fill: #C89B3C;");
        
        Label type = new Label(battle.getRequest().getBattleType().toString());
        type.getStyleClass().add("metric-label");
        
        Label p1 = new Label("P1: " + battle.getRequest().getPlayerOne().getName());
        Label p2 = new Label("P2: " + battle.getRequest().getPlayerTwo().getName());
        p1.setStyle("-fx-font-size: 10px; -fx-text-fill: #F0E6D2;");
        p2.setStyle("-fx-font-size: 10px; -fx-text-fill: #F0E6D2;");

        card.getChildren().addAll(id, type, p1, p2);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
