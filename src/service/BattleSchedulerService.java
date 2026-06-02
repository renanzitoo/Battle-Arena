package service;

import model.Battle;
import model.BattleRequest;
import thread.BattleThread;

import java.util.Comparator;
import java.util.PriorityQueue;


public class BattleSchedulerService {
    private PriorityQueue<BattleRequest> battleQueue;

    private int totalResources;
    private int availableResources;
    private final Object lock = new Object();

    public BattleSchedulerService(int totalResources) {
        this.totalResources = totalResources;
        this.availableResources = totalResources;

        this.battleQueue = new PriorityQueue<>(
                Comparator.comparingInt(BattleRequest::calculatePriorityWithAging).reversed()
        );
    }

    public void addBattleRequest(BattleRequest request){
        synchronized (lock) {
            battleQueue.add(request);
            lock.notifyAll();
        }

        System.out.println("Request added to scheduler: " + request);
    }

    public void tryStartNextBattle(){
        synchronized (lock) {

            if (battleQueue.isEmpty()) {
                System.out.println("No battle awaiting.");
                return;
            }

            BattleRequest request = battleQueue.peek();

            int requiredResources = request.getBattleType().getResourceCost();

            if (availableResources >= requiredResources) {
                battleQueue.poll();

                availableResources -= requiredResources;

                Battle battle = new Battle(request);

                BattleThread battleThread = new BattleThread(battle, this);

                battleThread.start();
                System.out.println("Scheduler started a battle");
                System.out.println("Available resources: " + availableResources);
            } else {
                System.out.println("Unavailable resources to start: " + request);
            }
        }
    }

    public void releaseResources(Battle battle) {
        synchronized (lock) {
            int resources = battle.getRequest().getBattleType().getResourceCost();

            availableResources += resources;
            System.out.println("Free resources: " + resources);
            System.out.println("Resources available: " + availableResources);
            lock.notifyAll();
        }
    }

    public void removeExpiredBattleRequest(){
        synchronized (lock){
            battleQueue.removeIf(request -> {
                boolean expired = request.shouldBeCancelled();

                if(expired){
                    System.out.println("Battle request expired: " + request);
                }
                return expired;
            });
        }
    }

    public void printSchedulerStatus() {
        synchronized (lock) {
            System.out.println("\n===== SCHEDULER STATUS =====");
            System.out.println("Battles awaiting: " + battleQueue.size());
            System.out.println("Total resources: " + totalResources);
            System.out.println("Available resources: " + availableResources);
            System.out.println("Using resources: " + (totalResources - availableResources));
        }
    }

    public boolean hasBattlesWaiting(){
        return !battleQueue.isEmpty();
    }
}
