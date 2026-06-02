import model.*;
import service.BattleSchedulerService;
import service.MatchmakingService;
import thread.MatchmakingThread;
import thread.MonitorThread;
import thread.SchedulerThread;

import java.util.Random;

public class Main {

    private static final Random random = new Random();
    private static int playerId = 1;

    public static void main(String[] args) throws InterruptedException {
        MatchmakingService matchmakingService = new MatchmakingService();
        BattleSchedulerService schedulerService = new BattleSchedulerService(4);

        MatchmakingThread matchmakingThread =
                new MatchmakingThread(matchmakingService, schedulerService);

        SchedulerThread schedulerThread =
                new SchedulerThread(schedulerService);

        MonitorThread monitorThread =
                new MonitorThread(matchmakingService, schedulerService);

        matchmakingThread.start();
        schedulerThread.start();
        monitorThread.start();

        System.out.println("=== SIMULAÇÃO INICIADA ===");

        long simulationStart = System.currentTimeMillis();
        long simulationDuration = 30000;

        while (System.currentTimeMillis() - simulationStart < simulationDuration) {
            Player player = new Player(playerId, "Player_" + playerId);

            BattleType battleType = BattleType.values()[
                    random.nextInt(BattleType.values().length)
                    ];

            QueueRequest request = new QueueRequest(player, battleType);

            matchmakingService.addToQueue(request);

            System.out.println("[NOVO JOGADOR] " +
                    player.getName() +
                    " entrou na fila " +
                    battleType);

            playerId++;
            Thread.sleep(random.nextInt(1000) + 300);
        }

        System.out.println("\n=== ENCERRANDO SIMULAÇÃO ===");

        matchmakingThread.stopMatchmaking();
        schedulerThread.stopScheduler();
        monitorThread.stopMonitor();

        matchmakingThread.join();
        schedulerThread.join();
        monitorThread.join();

        System.out.println("=== SIMULAÇÃO FINALIZADA ===");
    }
}