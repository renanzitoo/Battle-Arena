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

    public BattleSchedulerService(int totalResources) {
        this.totalResources = totalResources;
        this.availableResources = totalResources;

        this.battleQueue = new PriorityQueue<>(
                Comparator.comparingInt(BattleRequest::calculatePriorityWithAging).reversed()
        );
    }

    public void addBattleRequest(BattleRequest request){
        battleQueue.add(request);
        System.out.println("Request added to scheduler: " + request);
    }

    public void tryStartNextBattle(){
        if(battleQueue.isEmpty()) {
            System.out.println("No battle awaiting.");
            return;
        }

        BattleRequest request = battleQueue.peek();

        int requiredResources = request.getBattleType().getResourceCost();

        if(availableResources >= requiredResources){
            battleQueue.poll();

            availableResources -= requiredResources;

            Battle battle = new Battle(request);

            BattleThread battleThread = new BattleThread(battle,this);

            battleThread.start();
            System.out.println("Scheduler started a battle");
            System.out.println("Available resources: " + availableResources);
        } else {
            System.out.println("Unavailable resources to start: " + request);
        }
    }

    public void releaseResources(Battle battle) {
        int resources = battle.getRequest().getBattleType().getResourceCost();

        availableResources += resources;
        System.out.println("Free resources: " + resources);
        System.out.println("Resources available: "+ availableResources);
    }

    public boolean hasBattlesWaiting(){
        return !battleQueue.isEmpty();
    }
}
