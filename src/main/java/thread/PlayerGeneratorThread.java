package thread;

import model.BattleType;
import model.Player;
import model.QueueRequest;
import service.BattleSchedulerService;
import service.MatchmakingService;

import java.util.Random;

public class PlayerGeneratorThread extends Thread {
    private final MatchmakingService matchmakingService;
    private final BattleSchedulerService schedulerService;
    private final Random random = new Random();
    private boolean running = true;
    private int playerId = 1;

    public PlayerGeneratorThread(MatchmakingService matchmakingService, BattleSchedulerService schedulerService) {
        this.matchmakingService = matchmakingService;
        this.schedulerService = schedulerService;
    }

    @Override
    public void run() {
        System.out.println("[GENERATOR] Player generation started.");
        while (running) {
            try {
                Player player = new Player(playerId, "Player_" + playerId);
                BattleType battleType = BattleType.values()[random.nextInt(BattleType.values().length)];
                QueueRequest request = new QueueRequest(player, battleType);

                matchmakingService.addToQueue(request);

                System.out.println("[NEW PLAYER] " + player.getName() + " entered " + battleType + " queue.");
                
                playerId++;

                Thread.sleep(random.nextInt(1200) + 300);
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[GENERATOR] Player generation stopped.");
    }

    public void stopGenerator() {
        running = false;
        this.interrupt();
    }
}
