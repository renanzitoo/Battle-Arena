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

        Metrics metrics = new Metrics();

        MatchmakingService matchmakingService =
                new MatchmakingService(metrics);

        BattleSchedulerService schedulerService =
                new BattleSchedulerService(4, metrics);

        MatchmakingThread matchmakingThread =
                new MatchmakingThread(
                        matchmakingService,
                        schedulerService
                );

        SchedulerThread schedulerThread =
                new SchedulerThread(
                        schedulerService
                );

        MonitorThread monitorThread =
                new MonitorThread(
                        matchmakingService,
                        schedulerService
                );

        matchmakingThread.start();
        schedulerThread.start();
        monitorThread.start();

        System.out.println("\n===== SIMULATION STARTED =====\n");

        long simulationTime = 30000;
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < simulationTime) {

            Player player = new Player(
                    playerId,
                    "Player_" + playerId
            );

            BattleType battleType =
                    BattleType.values()[
                            random.nextInt(
                                    BattleType.values().length
                            )
                            ];

            QueueRequest request =
                    new QueueRequest(
                            player,
                            battleType
                    );

            matchmakingService.addToQueue(request);

            System.out.println(
                    "[NEW PLAYER] "
                            + player.getName()
                            + " entered "
                            + battleType
                            + " queue."
            );

            System.out.println("QUEUE");
            schedulerService.printBattleQueue();

            playerId++;

            Thread.sleep(
                    random.nextInt(1200) + 300
            );
        }

        System.out.println(
                "\n===== PLAYER GENERATION FINISHED ====="
        );

        System.out.println(
                "Waiting for remaining players to abandon queue and battles to finish...\n"
        );

        while (
                matchmakingService.hasPlayersWaiting()
                        || schedulerService.hasBattlesWaiting()
                        || schedulerService.hasActiveBattles()
        ) {
            Thread.sleep(1000);
            System.out.println("Actives threads: " + Thread.activeCount());
        }

        System.out.println(
                "\n===== NO MORE PLAYERS OR BATTLES ====="
        );

        matchmakingThread.stopMatchmaking();
        schedulerThread.stopScheduler();
        monitorThread.stopMonitor();

        matchmakingThread.join();
        schedulerThread.join();
        monitorThread.join();

        System.out.println(
                "\n===== FINAL REPORT ====="
        );

        metrics.printFinalReport();

        System.out.println(
                "\n===== SIMULATION FINISHED ====="
        );
    }
}