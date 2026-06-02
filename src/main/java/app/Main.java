package app;

import model.Metrics;
import service.BattleSchedulerService;
import service.MatchmakingService;
import thread.MatchmakingThread;
import thread.MonitorThread;
import thread.PlayerGeneratorThread;
import thread.SchedulerThread;

import java.util.Scanner;

public class Main {

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

        PlayerGeneratorThread playerGenerator = null;

        System.out.println("\n===== SYSTEM READY =====");
        System.out.println("Commands: 'start' (generate players), 'stop' (pause generation), 'exit' (finish simulation)");

        Scanner scanner = new Scanner(System.in);
        boolean exiting = false;

        while (!exiting) {
            String command = scanner.nextLine().toLowerCase().trim();

            switch (command) {
                case "start":
                    if (playerGenerator == null || !playerGenerator.isAlive()) {
                        playerGenerator = new PlayerGeneratorThread(matchmakingService, schedulerService);
                        playerGenerator.start();
                    } else {
                        System.out.println("Generator is already running.");
                    }
                    break;

                case "stop":
                    if (playerGenerator != null && playerGenerator.isAlive()) {
                        playerGenerator.stopGenerator();
                        playerGenerator.join();
                        System.out.println("Generator stopped manually.");
                    } else {
                        System.out.println("Generator is not running.");
                    }
                    break;

                case "exit":
                    if (playerGenerator != null && playerGenerator.isAlive()) {
                        playerGenerator.stopGenerator();
                        playerGenerator.join();
                    }
                    exiting = true;
                    break;

                default:
                    System.out.println("Unknown command. Use: start, stop, exit");
                    break;
            }
        }

        System.out.println("\n===== SHUTTING DOWN =====");
        System.out.println("Waiting for remaining players and battles to finish...\n");

        while (
                matchmakingService.hasPlayersWaiting()
                        || schedulerService.hasBattlesWaiting()
                        || schedulerService.hasActiveBattles()
        ) {
            Thread.sleep(1000);
            System.out.println("Active battles/players still in system...");
        }

        matchmakingThread.stopMatchmaking();
        schedulerThread.stopScheduler();
        monitorThread.stopMonitor();

        matchmakingThread.join();
        schedulerThread.join();
        monitorThread.join();

        System.out.println("\n===== FINAL REPORT =====");
        metrics.printFinalReport();

        System.out.println("\n===== SIMULATION FINISHED =====");
        scanner.close();
    }
}
