module BattleArena {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens view to javafx.fxml;
    exports view;
    exports model;
    exports service;
    exports thread;
    exports app;
}
