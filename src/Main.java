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

    public static void main(String[] args) {

        MatchmakingService matchmakingService = new MatchmakingService();

        BattleSchedulerService schedulerService =
                new BattleSchedulerService(10);

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

        System.out.println("=== SISTEMA INICIADO ===");

        while (true) {

            Player player = new Player(
                    playerId++,
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
                    "[NOVO JOGADOR] " +
                            player.getName() +
                            " entrou na fila " +
                            battleType
            );

            try {

                // jogador chega entre 0.5 e 3 segundos

                Thread.sleep(
                        random.nextInt(2500) + 500
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}