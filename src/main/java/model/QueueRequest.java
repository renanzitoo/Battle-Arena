package model;

public class QueueRequest {
    private Player player;
    private BattleType battleType;
    private long timeWhenEnteredQueue;

    public QueueRequest(Player player, BattleType battleType){
        this.player = player;
        this.battleType = battleType;
        this.timeWhenEnteredQueue = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public BattleType getBattleType() {
        return battleType;
    }

    public long getTimeWhenEnteredQueue() {
        return timeWhenEnteredQueue;
    }

    public long calculateWaitingTime(){
        return System.currentTimeMillis() - timeWhenEnteredQueue;
    }

    public boolean shouldAbandonQueue() {
        return calculateWaitingTime() > battleType.getMaxWaitingDurationInMs();
    }

    @Override
    public String toString(){
        return "QueueRequest{" +
                "player: " + player +
                ", battleType: " + battleType +
                ", waitingTime: " + calculateWaitingTime() + "ms" +
                '}';
    }
}
