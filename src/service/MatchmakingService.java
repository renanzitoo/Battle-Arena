package service;

import model.BattleRequest;
import model.BattleType;
import model.QueueRequest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class MatchmakingService {
    private Queue<QueueRequest> rankedDuelQueue;
    private Queue<QueueRequest> casualDuelQueue;
    private Queue<QueueRequest> tournamentDuelQueue;

    public MatchmakingService(){
        this.casualDuelQueue = new LinkedList<>();
        this.rankedDuelQueue = new LinkedList<>();
        this.tournamentDuelQueue = new LinkedList<>();
    }

    public void addToQueue(QueueRequest request){
        switch(request.getBattleType()){
            case CASUAL_MATCH:
                casualDuelQueue.add(request);
                break;

            case RANKED_MATCH:
                rankedDuelQueue.add(request);
                break;

            case TOURNAMENT_MATCH:
                tournamentDuelQueue.add(request);
                break;
        }

        System.out.println("Player entered the queue: "+ request);
    }

    public BattleRequest tryCreateBattle(BattleType battleType){
        Queue<QueueRequest> selectedQueue = getQueueByBattleType(battleType);

        if (selectedQueue.size() >= 2) {
            QueueRequest playerOne = selectedQueue.poll();
            QueueRequest playerTwo = selectedQueue.poll();

            BattleRequest request =
                    new BattleRequest(playerOne, playerTwo);

            System.out.println("Battle Begin: " + request);

            return request;
        }

        return null;
    }

    private Queue<QueueRequest> getQueueByBattleType(BattleType battleType) {
        switch (battleType) {
            case CASUAL_MATCH:
                return casualDuelQueue;

            case RANKED_MATCH:
                return rankedDuelQueue;

            case TOURNAMENT_MATCH:
                return tournamentDuelQueue;

            default:
                throw new IllegalArgumentException("Tipo de batalha inválido.");
        }
    }

    public void removeAbandonedRequests(){
        removeAbandonedFromQueue(casualDuelQueue);
        removeAbandonedFromQueue(rankedDuelQueue);
        removeAbandonedFromQueue(tournamentDuelQueue);
    }

    public void removeAbandonedFromQueue(Queue<QueueRequest> queue){
        Iterator<QueueRequest> iterator = queue.iterator();

        while(iterator.hasNext()){
            QueueRequest request = iterator.next();

            if(request.shouldAbandonQueue()){
                System.out.println("Player abandoned the queue: " + request);
                iterator.remove();
            }
        }
    }

    public void printQueuesStatus() {
        System.out.println("\n===== Queue status =====");
        System.out.println("Casual duels: " + casualDuelQueue.size());
        System.out.println("Ranked: " + rankedDuelQueue.size());
        System.out.println("Tournaments: " + tournamentDuelQueue.size());
    }
}
