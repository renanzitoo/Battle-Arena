package view;

import javafx.application.Application;

public class BattleArenaLauncher {
    public static void main(String[] args) {
        // Isso evita o erro de falta de componentes de runtime do JavaFX
        Application.launch(BattleArenaUI.class, args);
    }
}
