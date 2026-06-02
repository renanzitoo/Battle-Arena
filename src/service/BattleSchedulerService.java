package service;

import model.Battle;
import model.BattleRequest;
import model.Metrics;
import thread.BattleThread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class BattleSchedulerService {
    private PriorityQueue<BattleRequest> battleQueue;
    private List<Battle> activeBattles;

    private Metrics metrics;

    private int totalResources;
    private int availableResources;
    private final Object lock = new Object();

    public BattleSchedulerService(int totalResources, Metrics metrics) {
        this.totalResources = totalResources;
        this.availableResources = totalResources;
        this.metrics = metrics;

        this.battleQueue = new PriorityQueue<>(
                Comparator.comparingInt(BattleRequest::calculatePriorityWithAging).reversed()
        );

        this.activeBattles = new ArrayList<>();
    }

    public void addBattleRequest(BattleRequest request) {
        synchronized (lock) {
            battleQueue.add(request);
            lock.notifyAll();
        }

        System.out.println("Request added to scheduler: " + request);
    }

    public void tryStartNextBattle() {
        synchronized (lock) {
            if (battleQueue.isEmpty()) {
                return;
            }

            BattleRequest request = battleQueue.peek();

            int requiredResources = request.getBattleType().getResourceCost();

            if (availableResources >= requiredResources) {
                battleQueue.poll();

                availableResources -= requiredResources;

                long averageWaitingTime =
                        (
                                request.getPlayerOneRequest().calculateWaitingTime()
                                        + request.getPlayerTwoRequest().calculateWaitingTime()
                        ) / 2;

                metrics.registerStartedBattle(averageWaitingTime);

                Battle battle = new Battle(request);
                activeBattles.add(battle);

                BattleThread battleThread = new BattleThread(battle, this);
                battleThread.start();

                System.out.println("Scheduler started a battle");
                System.out.println("Available resources: " + availableResources);
            }
        }
    }

    public void releaseResources(Battle battle) {
        synchronized (lock) {
            int resources = battle.getRequest()
                    .getBattleType()
                    .getResourceCost();

            availableResources += resources;

            activeBattles.remove(battle);

            metrics.incrementFinishedBattles();

            System.out.println("Free resources: " + resources);
            System.out.println("Resources available: " + availableResources);

            lock.notifyAll();
        }
    }

//    public void removeExpiredBattleRequest() {
//        synchronized (lock) {
//            battleQueue.removeIf(request -> {
//                boolean expired = request.shouldBeCancelled();
//
//                if (expired) {
//                    System.out.println("Battle request expired: " + request);
//                }
//
//                return expired;
//            });
//        }
//    }

    public void printSchedulerStatus() {
        synchronized (lock) {
            System.out.println("\n===== SCHEDULER STATUS =====");
            System.out.println("Battles awaiting: " + battleQueue.size());
            System.out.println("Active battles: " + activeBattles.size());
            System.out.println("Total resources: " + totalResources);
            System.out.println("Available resources: " + availableResources);
            System.out.println("Using resources: " + (totalResources - availableResources));
            System.out.printf("System utilization: %.2f%%\n", getCurrentUtilization());
        }
    }

    public double getCurrentUtilization() {
        synchronized (lock) {
            return (double) (totalResources - availableResources) / totalResources * 100;
        }
    }

    public boolean hasBattlesWaiting() {
        synchronized (lock) {
            return !battleQueue.isEmpty();
        }
    }

    public boolean hasActiveBattles() {
        synchronized (lock) {
            return !activeBattles.isEmpty();
        }
    }
}