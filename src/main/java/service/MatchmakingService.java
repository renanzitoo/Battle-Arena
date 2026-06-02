package service;

import model.BattleRequest;
import model.BattleType;
import model.Metrics;
import model.QueueRequest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class MatchmakingService {
    private final Object lock = new Object();
    private Metrics metrics;
    private Queue<QueueRequest> rankedDuelQueue;
    private Queue<QueueRequest> casualDuelQueue;
    private Queue<QueueRequest> tournamentDuelQueue;

    private java.util.function.Consumer<String> eventLogger;

    public MatchmakingService(Metrics metrics){
        this.metrics = metrics;
        this.casualDuelQueue = new LinkedList<>();
        this.rankedDuelQueue = new LinkedList<>();
        this.tournamentDuelQueue = new LinkedList<>();
    }

    public void setEventLogger(java.util.function.Consumer<String> logger) {
        this.eventLogger = logger;
    }

    private void log(String msg) {
        System.out.println(msg);
        if (eventLogger != null) eventLogger.accept(msg);
    }

    public void addToQueue(QueueRequest request){
        synchronized(lock) {
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

            metrics.incrementTotalPlayers();
            log("[QUEUE] " + request.getPlayer().getName() + " entered " + request.getBattleType());
        }
    }

    public BattleRequest tryCreateBattle(BattleType battleType){
        synchronized(lock) {
            Queue<QueueRequest> selectedQueue = getQueueByBattleType(battleType);

            if (selectedQueue.size() >= 2) {
                QueueRequest playerOne = selectedQueue.poll();
                QueueRequest playerTwo = selectedQueue.poll();

                BattleRequest request =
                        new BattleRequest(playerOne, playerTwo);

                log("[MATCH] Battle formed: " + playerOne.getPlayer().getName() + " vs " + playerTwo.getPlayer().getName());

                return request;
            }

            return null;
        }
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
        synchronized(lock) {
            removeAbandonedFromQueue(casualDuelQueue);
            removeAbandonedFromQueue(rankedDuelQueue);
            removeAbandonedFromQueue(tournamentDuelQueue);
        }
    }

    public void removeAbandonedFromQueue(Queue<QueueRequest> queue){
        Iterator<QueueRequest> iterator = queue.iterator();

        while(iterator.hasNext()){
            QueueRequest request = iterator.next();

            if(request.shouldAbandonQueue()){
                System.out.println("Player abandoned the queue: " + request);
                metrics.incrementAbandonedPlayers();
                iterator.remove();
            }
        }
    }

    public void printQueuesStatus() {
        synchronized(lock) {
            System.out.println("\n===== Queue status =====");
            System.out.println("Casual duels: " + casualDuelQueue.size());
            System.out.println("Ranked: " + rankedDuelQueue.size());
            System.out.println("Tournaments: " + tournamentDuelQueue.size());
        }
    }

    public Queue<QueueRequest> getCasualDuelQueue() {
        synchronized (lock) {
            return new LinkedList<>(casualDuelQueue);
        }
    }

    public Queue<QueueRequest> getRankedDuelQueue() {
        synchronized (lock) {
            return new LinkedList<>(rankedDuelQueue);
        }
    }

    public Queue<QueueRequest> getTournamentDuelQueue() {
        synchronized (lock) {
            return new LinkedList<>(tournamentDuelQueue);
        }
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public boolean hasPlayersWaiting() {
        synchronized (lock){
            return !casualDuelQueue.isEmpty()
                    || !rankedDuelQueue.isEmpty()
                    || !tournamentDuelQueue.isEmpty();
        }
    }
}
